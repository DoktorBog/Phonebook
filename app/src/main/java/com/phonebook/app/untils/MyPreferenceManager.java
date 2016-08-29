package com.phonebook.app.untils;


import android.content.Context;
import android.content.SharedPreferences;

import com.phonebook.app.models.Contact;
import com.phonebook.app.models.User;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.phonebook.app.untils.Helper.KEY_USER_EMAIL;
import static com.phonebook.app.untils.Helper.KEY_USER_NAME;
import static com.phonebook.app.untils.Helper.PREF_NAME;

public class MyPreferenceManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    public MyPreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void storeUser(User user) {
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.commit();
    }


    public User getUser() {
        if(pref.getString(KEY_USER_NAME, null)!=null) {
            String name, email;
            name = pref.getString(KEY_USER_NAME, null);
            email = pref.getString(KEY_USER_EMAIL, null);

            User user = new User(name, email);
            return user;
        }
        return null;
    }


    public void clear() {
        editor.clear();
        editor.commit();

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Contact> contacts = realm.where(Contact.class).findAll();

        realm.beginTransaction();
        contacts.deleteAllFromRealm();
        realm.commitTransaction();
    }
}
