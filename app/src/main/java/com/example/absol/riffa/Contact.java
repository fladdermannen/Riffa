package com.example.absol.riffa;

/**
 * Created by absol on 2018-03-16.
 */

public class Contact {
    private String name;
    private String email;
    private int image = R.drawable.ic_contacts;

    public Contact(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
