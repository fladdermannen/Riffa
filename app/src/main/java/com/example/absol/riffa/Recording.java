package com.example.absol.riffa;

import android.net.Uri;



public class Recording {

    private String title;
    private String length;
    private String genre;
    private String date;
    private boolean access;
    private String link;

    public Recording(String title, String length, String genre, String date, Uri uri){
        this.title = title;
        this.length = length;
        this.genre = genre;
        this.date = date;
        this.link = uri.toString();
    }

    public String getLength() {
        return length;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public String getDate() {
        return date;
    }

    public boolean getAccess() {
        return access;
    }

    public String getLink() {
        return link;
    }

    public void setAccess(boolean access) {
        this.access = access;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(Uri link) {
        this.link = link.toString();
    }
}
