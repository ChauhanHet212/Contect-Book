package com.example.contactbook;

import android.view.View;

import com.example.contactbook.Models.Contacts;

public interface ClickListener {
    void onClick(Contacts contacts);

    void onLongClick(Contacts contacts, View view);
}
