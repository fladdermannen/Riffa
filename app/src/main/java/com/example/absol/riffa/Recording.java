package com.example.absol.riffa;

import java.io.Serializable;

public class Recording implements Serializable {

    private String title;
    private int length;
    private String genre;
    private String date;
    private boolean access;
    private String url;
    private boolean favorite;
    private String key;
    private String author;
    private String storageName;

    public Recording(String title, int length, String genre, String date, String uri, String key, String author, String storageName){
        this.title = title;
        this.length = length;
        this.genre = genre;
        this.date = date;
        this.url = uri;
        this.access = true;
        this.favorite = false;
        this.key = key;
        this.author = author;
        this.storageName = storageName;
    }

    public Recording() {

    }



    public int getLength() {
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
        return url;
    }

    public boolean getFavorite() {
        return favorite;
    }

    public String getKey() {
        return key;
    }

    public String getAuthor() {
        return author;
    }

    public String getStorageName() {
        return storageName;
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

    public void setLength(int length) {
        this.length = length;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.url = link;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }

}
