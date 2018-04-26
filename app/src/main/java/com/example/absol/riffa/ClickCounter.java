package com.example.absol.riffa;

public class ClickCounter {

    private int clicks;
    private String key;
    private String userId;
    private boolean access;
    private long time;

    public ClickCounter(String key, String userId, long time) {
        this.clicks = 0;
        this.key = key;
        this.userId = userId;
        this.access = true;
        this.time = time;
    }

    public ClickCounter() {

    }

    public int getClicks() {
        return clicks;
    }

    public String getKey() {
        return key;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }

    public void setAccess(boolean access) {
        this.access = access;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public boolean getAccess() {
        return this.access;
    }

    public long getTime() {
        return time;
    }
}

