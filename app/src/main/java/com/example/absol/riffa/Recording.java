package com.example.absol.riffa;

/**
 * Created by absol on 2018-03-15.
 */

public class Recording {

    private String title;
    private String length;
    private String genre;

    public Recording(String title, String length, String genre){
        this.title = title;
        this.length = length;
        this.genre = genre;
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

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
