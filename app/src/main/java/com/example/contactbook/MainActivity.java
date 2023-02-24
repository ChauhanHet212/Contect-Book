package com.example.contactbook;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactbook.Adapters.ContactsAdapter;
import com.example.contactbook.Database.DBHelper;
import com.example.contactbook.Models.Contacts;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button dialog_add, dialog_cancel;
    TextInputEditText edtName, edtNumber;
    FloatingActionButton btnAdd;
    SearchView searchView;
    RecyclerView recyclerView;
    ContactsAdapter contactsAdapter;
    List<Contacts> allcontacts = new ArrayList<>();
    DBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);

        helper = new DBHelper(MainActivity.this);
        allcontacts = helper.getAllContacts();

        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        contactsAdapter = new ContactsAdapter(MainActivity.this, allcontacts, listener);
        recyclerView.setAdapter(contactsAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()){
                    List<Contacts> filterlist = new ArrayList<>();
                    for (Contacts singleContact: allcontacts) {
                        if (singleContact.getName().toLowerCase().contains(newText.toLowerCase()) ||
                            singleContact.getNumber().toLowerCase().contains(newText.toLowerCase())) {
                            filterlist.add(singleContact);
                        }
                        contactsAdapter.filteredList(filterlist);
                    }
                } else {
                    contactsAdapter.normal(allcontacts);
                }
                return true;
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.add_dialog);
                dialog.setCancelable(false);

                dialog_add = dialog.findViewById(R.id.dialog_add);
                dialog_cancel = dialog.findViewById(R.id.dialog_cancel);
                edtName = dialog.findViewById(R.id.edtName);
                edtNumber = dialog.findViewById(R.id.edtNumber);

                dialog_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = edtName.getText().toString();
                        String number = edtNumber.getText().toString();
                        if (!name.isEmpty() && !number.isEmpty()) {
                            if (number.length() == 10) {
                                int cnt = 0;
                                for (int i = 0; i < allcontacts.size(); i++) {
                                    if (allcontacts.get(i).getNumber().equals(number)) {
                                        cnt++;
                                    }
                                }
                                if (cnt == 0) {
                                    helper.addContact(name, number);
                                    edtName.setText("");
                                    edtNumber.setText("");
                                    dialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Contact Added", Toast.LENGTH_SHORT).show();
                                    refresh();
                                } else {
                                    edtNumber.setError("Contact Already Exist");
                                }
                            } else {
                                edtNumber.setError("Please Enter 10 Digit Number");
                            }
                        } else {
                            if (name.isEmpty()){
                                edtName.setError("Enter Name");
                            }
                            if (number.isEmpty()){
                                edtNumber.setError("Enter Number");
                            }
                        }
                    }
                });

                dialog.show();
            }
        });
    }

    ClickListener listener = new ClickListener() {
        @Override
        public void onClick(Contacts contacts) {
            String number = contacts.getNumber();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel: +91" + number));
            startActivity(intent);
        }

        @Override
        public void onLongClick(Contacts contacts, View view) {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
            popupMenu.inflate(R.menu.popup_menu);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popupMenu.setForceShowIcon(true);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                popupMenu.setGravity(Gravity.END);
            }
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.popup_copy) {
                        ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData data = ClipData.newPlainText("text", "+91" + contacts.getNumber());
                        manager.setPrimaryClip(data);

                        Toast.makeText(MainActivity.this, "Text Copied", Toast.LENGTH_SHORT).show();
                        return true;
                    } else if (menuItem.getItemId() == R.id.popup_message){
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("smsto:" + Uri.decode("+91" + contacts.getNumber())));
                        startActivity(intent);
                        return true;
                    } else if (menuItem.getItemId() == R.id.popup_share){
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, contacts.getName() + "\n" + contacts.getNumber());
                        startActivity(intent);
                        return true;
                    } else if (menuItem.getItemId() == R.id.popup_edit) {
                        Dialog dialog = new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.edit_dialog);
                        dialog.setCancelable(false);

                        Button edt_save, edt_cancel;
                        TextInputEditText edt_name, edt_number;
                        edt_save = dialog.findViewById(R.id.edt_save);
                        edt_cancel = dialog.findViewById(R.id.edt_cancel);
                        edt_name = dialog.findViewById(R.id.edt_Name);
                        edt_number = dialog.findViewById(R.id.edt_Number);

                        edt_name.setText(contacts.getName());
                        edt_number.setText(contacts.getNumber());

                        edt_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        edt_save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String name = edt_name.getText().toString();
                                String number = edt_number.getText().toString();
                                if (!name.isEmpty() && !number.isEmpty()) {
                                    if (number.length() == 10) {
                                        helper.updateContact(new Contacts(contacts.getId(), name, number));
                                        edt_name.setText("");
                                        edt_number.setText("");
                                        dialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Contact Updated", Toast.LENGTH_SHORT).show();
                                        refresh();
                                    } else {
                                        edt_number.setError("Enter 10 Digit Number");
                                    }
                                } else {
                                    if (name.isEmpty()){
                                        edt_name.setError("Enter Name");
                                    }
                                    if (number.isEmpty()){
                                        edt_number.setError("Enter Number");
                                    }
                                }
                            }
                        });

                        dialog.show();
                        return true;
                    } else {
                        Dialog dialog = new Dialog(MainActivity.this);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.delete_dialog);

                        Button delete_no, delete_yes;
                        delete_no = dialog.findViewById(R.id.delete_no);
                        delete_yes = dialog.findViewById(R.id.delete_yes);

                        delete_yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                helper.deleteContact(contacts.getId());
                                refresh();
                                dialog.dismiss();
                                Toast.makeText(MainActivity.this, "Contact Deleted", Toast.LENGTH_SHORT).show();
                            }
                        });

                        delete_no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                        return true;
                    }
                }
            });
            popupMenu.show();
        }
    };

    public void refresh() {
        allcontacts.clear();
        allcontacts.addAll(helper.getAllContacts());
        contactsAdapter.notifyDataSetChanged();
    }
}