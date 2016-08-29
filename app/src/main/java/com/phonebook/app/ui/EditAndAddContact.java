package com.phonebook.app.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.phonebook.app.R;
import com.phonebook.app.adapters.AdapterListFields;
import com.phonebook.app.models.Contact;
import com.phonebook.app.models.ContactChangeEvent;
import com.phonebook.app.models.PhoneNumber;
import com.phonebook.app.untils.BusProvider;
import com.phonebook.app.untils.Helper;

import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;

public class EditAndAddContact extends AppCompatActivity {

    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.profileImage) ImageView profileImage;
    @Bind(R.id.editTextName) AppCompatEditText editTextName;
    private AdapterListFields adapterListFields;
    private boolean editOrCreateStatus = true;
    private RealmList<PhoneNumber> phoneNumberArrayList = new RealmList<>();
    private Realm realm;
    private Contact contact;
    private String photoURI = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_and_add_contact);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(view -> finish());

        realm = Realm.getDefaultInstance();
        editOrCreateStatus = getIntent().getBooleanExtra("status", true);

        if (savedInstanceState == null) {
            if (editOrCreateStatus) {
                phoneNumberArrayList.add(new PhoneNumber());
                contact = new Contact();
                contact.setUuid(UUID.randomUUID().toString());
                contact.setFavorite(false);
            } else {
                String uuid = getIntent().getStringExtra("uuid");
                contact = realm.where(Contact.class).equalTo("uuid", uuid).findFirst();
                photoURI = contact.getPhotoURI();
                for (PhoneNumber phoneNumber : contact.getNumbers())
                    phoneNumberArrayList.add(new PhoneNumber(phoneNumber.getPhone(), phoneNumber.getType()));

                Glide.with(this).load(contact.getPhotoURI()).placeholder(R.drawable.default_profile_img).into(profileImage);
                editTextName.setText(contact.getName());
            }
        }

        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        adapterListFields = new AdapterListFields(phoneNumberArrayList);
        recyclerView.setAdapter(adapterListFields);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done:

                for (int i = 0; i < phoneNumberArrayList.size(); i++) {
                    if(phoneNumberArrayList.get(i).getPhone().isEmpty()){
                        phoneNumberArrayList.remove(i);
                    }
                }

                if(!phoneNumberArrayList.isEmpty()) {
                    RealmList<PhoneNumber> phoneNumbers = new RealmList<>();
                    phoneNumbers.addAll(phoneNumberArrayList);

                    Contact contactRealmSave = new Contact();
                    contactRealmSave.setUuid(contact.getUuid());
                    contactRealmSave.setNumbers(phoneNumbers);
                    contactRealmSave.setName(editTextName.getText().toString());
                    contactRealmSave.setPhotoURI(photoURI);

                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(contactRealmSave);
                    realm.commitTransaction();

                    finish();
                    BusProvider.getInstance().post(new ContactChangeEvent(Helper.TYPE_CHANGE_FAVORITE, contact));
                }else {
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private static final int ADD_MULTIMEDIA = 166;
    @OnClick(R.id.profileImage)
    public void onClick() {
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23)
                insertDummyContactWrapper();
            return;
        }
        startActivityForResult(i, ADD_MULTIMEDIA);
    }

    final private int REQUEST_CODE_ASK_PERMISSIONS = 12345;
    @TargetApi(Build.VERSION_CODES.M)
    private void insertDummyContactWrapper() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_CODE_ASK_PERMISSIONS);
        return;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, ADD_MULTIMEDIA);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_MULTIMEDIA:
                if(data!=null){
                    photoURI = getRealPathFromURIImage(data.getData());
                    Glide.with(this).load(photoURI).into(profileImage);
                }
                break;
            default:
                break;
        }
    }

    public String getRealPathFromURIImage(Uri contentUri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return contentUri.getPath();
        }
    }
}
