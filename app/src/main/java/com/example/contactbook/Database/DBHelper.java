package com.example.contactbook.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.contactbook.Models.Contacts;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(@Nullable Context context) {
        super(context, "Contacts_DB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String quarry = "CREATE TABLE Contacts (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, number TEXT)";
        sqLiteDatabase.execSQL(quarry);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public List<Contacts> getAllContacts(){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM Contacts", null);
        List<Contacts> arrcontacts = new ArrayList<>();

        while (cursor.moveToNext()){
            int id =cursor.getInt(0);
            String name = cursor.getString(1);
            String number = cursor.getString(2);
            arrcontacts.add(new Contacts(id, name, number));
        }
        return arrcontacts;
    }

    public void addContact(String name, String number){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String quarry = "INSERT INTO Contacts (name, number) VALUES ('" + name + "', '" + number + "')";
        sqLiteDatabase.execSQL(quarry);
    }

    public void updateContact(Contacts contacts){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", contacts.getName());
        values.put("number", contacts.getNumber());

        sqLiteDatabase.update("Contacts", values, "id = ?", new String[]{String.valueOf(contacts.getId())});
    }

    public void deleteContact(int id){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete("Contacts", "id = ?", new String[]{String.valueOf(id)});
    }
}
