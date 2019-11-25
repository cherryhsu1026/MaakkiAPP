package com.maakki.maakkiapp;

import android.app.PendingIntent;

/**
 * Created by ryan on 2017/7/11.
 */
public class PrevNotification {
    private long id;
    private Integer icon, messagetype, isActivity;
    private String title, message;
    private PendingIntent pintent;
    private long lastModify;
    private Boolean isVisible;

    public PrevNotification() {
    }

    public PrevNotification(long id, String title, String message, int icon, int messagetype, PendingIntent pintent, Long lastModify, Boolean isVisible) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.messagetype = messagetype;
        this.icon = icon;
        this.pintent = pintent;
        this.lastModify = lastModify;
        this.isVisible = isVisible;
    }

    public long getid() {
        return id;
    }

    public void setid(long id) {
        this.id = id;
    }

    public String gettitle() {
        return title;
    }

    public void settitle(String title) {
        this.title = title;
    }

    public String getmessage() {
        return message;
    }

    public void setmessage(String message) {
        this.message = message;
    }

    public Integer getmessagetype() {
        return messagetype;
    }

    public void setmessagetype(Integer messagetype) {
        this.messagetype = messagetype;
    }

    public int geticon() {
        return icon;
    }

    public void seticon(int icon) {
        this.icon = icon;
    }

    public int getisActivity() {
        return isActivity;
    }

    public void setisActivity(int isActivity) {
        this.isActivity = isActivity;
    }

    public PendingIntent getpintent() {
        return pintent;
    }

    public void setpintent(PendingIntent pintent) {
        this.pintent = pintent;
    }

    public long getLastModify() {
        return lastModify;
    }

    public void setLastModify(long lastModify) {
        this.lastModify = lastModify;
    }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }
}

