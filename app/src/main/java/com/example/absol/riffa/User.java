package com.example.absol.riffa;


public class User {

    private String fName;
    private String lName;
    private String email;

    public User(String fName, String lName, String email) {
        this.fName = fName;
        this.lName = lName;
        this.email = email;
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
}
