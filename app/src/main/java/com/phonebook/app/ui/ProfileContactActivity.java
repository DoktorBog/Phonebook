package com.phonebook.app.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.phonebook.app.R;
import com.phonebook.app.adapters.AdapterContactInfo;
import com.phonebook.app.adapters.ItemClickSupport;
import com.phonebook.app.models.Contact;
import com.phonebook.app.models.ContactChangeEvent;
import com.phonebook.app.untils.BusProvider;
import com.phonebook.app.untils.Helper;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;

public class ProfileContactActivity extends AppCompatActivity {

    @Bind(R.id.backdrop) ImageView backdrop;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.conversation) RecyclerView conversation;
    @Bind(R.id.main_L) CoordinatorLayout main;
    private AdapterContactInfo adapterContactInfo;
    private Realm realm;
    private LinearLayoutManager llm;
    private Animation slide_down;
    private Animation slide_up;
    private Contact contact;
    private int pos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String uuid = getIntent().getStringExtra("contact_id");
        realm = Realm.getDefaultInstance();
        contact = realm.where(Contact.class).equalTo("uuid", uuid).findFirst();

        setContentView(R.layout.activity_profile_contact);
        ButterKnife.bind(this);

        slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);

        if (savedInstanceState == null)
            main.startAnimation(slide_up);


        toolbar.setTitle(contact.getName() + "");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(view -> {

            slide_down.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation arg0) {}

                @Override
                public void onAnimationRepeat(Animation arg0) {}

                @Override
                public void onAnimationEnd(Animation arg0) {
                    finish();
                    overridePendingTransition(0, 0);
                }
            });

            main.startAnimation(slide_down);
        });

        if (contact.getPhotoURI() != null) {
            Glide.with(this).load(contact.getPhotoURI())
                    .placeholder(R.drawable.default_profile_img)
                    .into(backdrop);
        }

        llm = new LinearLayoutManager(this);
        conversation.setLayoutManager(llm);

        adapterContactInfo = new AdapterContactInfo();
        adapterContactInfo.setInfo(this, contact.getNumbers());
        conversation.setAdapter(adapterContactInfo);

        ItemClickSupport.addTo(conversation).setOnItemClickListener((recyclerView1, position, v) -> {
            pos = -1;
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact.getNumbers().get(position).getPhone()));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= 23)
                    insertDummyContactWrapper();
                pos = position;
                return;
            }
            this.startActivity(intent);
        });


    }

    final private int REQUEST_CODE_ASK_PERMISSIONS = 1234;

    @TargetApi(Build.VERSION_CODES.M)
    private void insertDummyContactWrapper() {
        requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                REQUEST_CODE_ASK_PERMISSIONS);
        return;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (pos != -1) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact.getNumbers().get(pos)
                                .getPhone()));
                        pos = -1;
                        startActivity(intent);}}
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);

        if(contact.isFavorite())
            menu.findItem(R.id.favorite).setIcon(R.drawable.ic_star);

        return true;
    }

    @Override
    public void onBackPressed() {

        slide_down.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {}

            @Override
            public void onAnimationRepeat(Animation arg0) {}

            @Override
            public void onAnimationEnd(Animation arg0) {
                finish();
                overridePendingTransition(0, 0);
            }
        });

        main.startAnimation(slide_down);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                Intent editIntent = new Intent(this, EditAndAddContact.class);
                editIntent.putExtra("uuid", contact.getUuid());
                editIntent.putExtra("status", false);
                startActivity(editIntent);

                finish();
                return true;

            case R.id.favorite:
                if(contact.isFavorite()) {

                    realm.beginTransaction();
                    contact.setFavorite(false);
                    realm.commitTransaction();

                    item.setIcon(R.drawable.ic_star_border);
                    BusProvider.getInstance().post(new ContactChangeEvent(Helper.TYPE_CHANGE_FAVORITE, contact));

                } else {

                    realm.beginTransaction();
                    contact.setFavorite(true);
                    realm.commitTransaction();

                    item.setIcon(R.drawable.ic_star);
                    BusProvider.getInstance().post(new ContactChangeEvent(Helper.TYPE_CHANGE_FAVORITE, contact));
                }
                return true;

            case R.id.delete:
                new MaterialDialog.Builder(this)
                        .title(R.string.delete_contact)
                        .iconRes(R.drawable.ic_delete_green)
                        .content(getString(R.string.delete_contact) +" " + contact.getName() +" from Phonebook ?")
                        .positiveText(R.string.yes)
                        .negativeText(R.string.no)
                        .onPositive((dialog, which) -> {
                            realm.beginTransaction();
                            contact.deleteFromRealm();
                            realm.commitTransaction();
                            BusProvider.getInstance().post(new ContactChangeEvent(Helper.TYPE_CHANGE_DELETE, contact));
                            finish();
                        })
                        .show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
