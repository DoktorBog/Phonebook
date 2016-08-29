package com.phonebook.app.untils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.phonebook.app.models.Contact;
import com.phonebook.app.models.PhoneNumber;

import java.util.ArrayList;
import java.util.UUID;

import io.realm.RealmList;

public class Untiles {

    public static ArrayList<Contact> getContactsFromPhone(Context ctx){
        ArrayList<Contact> contacts = new ArrayList<>();

        ContentResolver cr = ctx.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                String photo = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.PHOTO_URI));

                RealmList<PhoneNumber> numbers = new RealmList<>();
                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);

                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        int type = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        if(phoneNo!=null){
                            PhoneNumber phoneNumber = new PhoneNumber();
                            phoneNumber.setPhone(phoneNo);
                            phoneNumber.setType(type);
                            numbers.add(phoneNumber);
                        }
                    }
                    pCur.close();
                }

                if(!numbers.isEmpty()) {
                    Contact contact = new Contact();
                    contact.setUuid(UUID.randomUUID().toString());
                    contact.setName(name);
                    contact.setNumbers(numbers);
                    if (photo != null) {
                        if (!photo.isEmpty())
                            contact.setPhotoURI(photo);
                    }
                    contacts.add(contact);
                }
            }
        }

        return contacts;
    }
}
