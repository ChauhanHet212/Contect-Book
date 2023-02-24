package com.example.contactbook.Models;

public class Contacts {
    private int id;
    private String name, number;

    public Contacts(int id, String name, String number) {
        this.id = id;
        this.name = name;
        this.number = number;
    }

    public Contacts(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
