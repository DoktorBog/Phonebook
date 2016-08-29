package com.phonebook.app.models;

import android.provider.ContactsContract;

import java.io.Serializable;

import io.realm.RealmObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PhoneNumber extends RealmObject implements Serializable {
    private String phone = "";
    private int type = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
}
