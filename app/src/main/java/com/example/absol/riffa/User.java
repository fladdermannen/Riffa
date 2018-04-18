package com.example.absol.riffa;


import java.util.ArrayList;

public class User {

    private String fName;
    private String lName;
    private String fullName;
    private String email;
    private String uID;
    private ArrayList<Recording> recordings;

    public User(String fName, String lName, String email, String uID) {
        this.fName = fName;
        this.lName = lName;
        this.fullName = fName.toLowerCase() + " " + lName.toLowerCase();
        this.email = email;
        this.uID = uID;
        this.recordings = new ArrayList<>();
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
}
