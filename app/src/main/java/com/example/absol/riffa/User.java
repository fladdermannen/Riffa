package com.example.absol.riffa;


import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable{

    private String fName;
    private String lName;
    private String fullName;
    private String email;
    private String uID;
    private ArrayList<User> contacts;
    private ArrayList<Recording> recordings;
    private ArrayList<Recording> favorites;

    public User(String fName, String lName, String email, String uID) {
        this.fName = fName;
        this.lName = lName;
        this.fullName = fName.toLowerCase() + " " + lName.toLowerCase();
        this.email = email;
        this.uID = uID;
        this.recordings = new ArrayList<>();
        this.contacts = new ArrayList<>();
        this.favorites = new ArrayList<>();
    }

    public User() {

    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public String getuID() {
        return uID;
    }

    public ArrayList<Recording> getRecordings() {
        return recordings;
    }

    public ArrayList<User> getContacts() {
        return contacts;
    }

    public ArrayList<Recording> getFavorites() {
        return favorites;
    }
}
