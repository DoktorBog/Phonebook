package com.phonebook.app.models;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Contact extends RealmObject implements Serializable{

    @PrimaryKey
    private String uuid;
    private String name;
    private String photoURI;
    private boolean favorite = false;
    private String linkedInURL;
    private RealmList<PhoneNumber> numbers;
}
