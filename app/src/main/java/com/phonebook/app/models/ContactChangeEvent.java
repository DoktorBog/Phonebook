package com.phonebook.app.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class ContactChangeEvent {
    private int typeChange;
    private Contact contact;
}
