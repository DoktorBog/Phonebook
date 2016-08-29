package com.phonebook.app.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.phonebook.app.MyApplication;
import com.phonebook.app.R;
import com.phonebook.app.models.Contact;
import com.phonebook.app.models.User;
import com.phonebook.app.untils.Untiles;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.loginTV) MaterialEditText loginTV;
    @Bind(R.id.passwordTV) MaterialEditText passwordTV;
    private static ProgressDialog pd;
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        context = this;
    }

    @OnClick(R.id.loginButton)
    public void onClick() {

        if(!validateName())
            return;

        if(!validatePassword())
            return;

        String login = loginTV.getText().toString();
        String password = passwordTV.getText().toString();

        if(login.equals(getString(R.string.login))){
            if(password.equals(getString(R.string.password))){
                MyApplication.getInstance().getPrefManager().storeUser(new User("Admin","admin@phonebook.com"));
                if(Build.VERSION.SDK_INT<23) saveAndStart();
                else insertDummyContactWrapper();
            } else {
                passwordTV.setError(getString(R.string.its)+" "+getString(R.string.password));
                requestFocus(passwordTV);
            }
        }else {
            loginTV.setError(getString(R.string.its)+" "+getString(R.string.login));
            requestFocus(loginTV);
        }
    }

    public boolean validateName(){
        if(loginTV.getText().toString().isEmpty()) {
            loginTV.setError(getString(R.string.empty_error));
            requestFocus(loginTV);
            return false;
        }
        else return true;
    }

    public boolean validatePassword(){
        if(passwordTV.getText().toString().isEmpty()){
            passwordTV.setError(getString(R.string.empty_error));
            requestFocus(passwordTV);
            return false;
        }
        else return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @TargetApi(Build.VERSION_CODES.M)
    private void insertDummyContactWrapper() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_CONTACTS);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveAndStart();
                } else {
                    startActivity(new Intent(this, ContactsActivity.class));
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void saveAndStart(){
        new MyTask().execute();
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        private ArrayList<Contact> contacts;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LoginActivity.showProgress(getString(R.string.loading));
        }

        @Override
        protected Void doInBackground(Void... params) {
            contacts = Untiles.getContactsFromPhone(LoginActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(contacts);
            realm.commitTransaction();
            hideProgress();

            startActivity(new Intent(LoginActivity.this, ContactsActivity.class));
            finish();
        }
    }

    public static void showProgress(String message) {
        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(message);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
    }

    public static void hideProgress() {
        pd.dismiss();
    }
}
