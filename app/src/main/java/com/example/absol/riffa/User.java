package com.example.absol.riffa;


import java.util.ArrayList;

public class User {

    private String fName;
    private String lName;
    private String email;
    private String uID;
    private ArrayList<Recording> recordings;

    public User(String fName, String lName, String email, String uID) {
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.uID = uID;
        this.recordings = new ArrayList<>();
    }

    public String getEmail() {
        return email;
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
