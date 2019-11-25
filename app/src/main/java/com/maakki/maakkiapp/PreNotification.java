package com.maakki.maakkiapp;

import android.app.PendingIntent;
import android.content.Intent;

/**
 * Created by ryan on 2017/7/11.
 */
public class PreNotification {
    private long id;
    private Integer messagetype, isActivity;
    private String picfile,title, message;
    private Intent intent;
    private long lastModify;
    private Boolean isVisible;

    public PreNotification() {  }

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

    public String getPicfile() {
        return picfile;
    }

    public void setPicfile(String picfile) {
        this.picfile = picfile;
    }

    public int getisActivity() {
        return isActivity;
    }

    public void setisActivity(int isActivity) {
        this.isActivity = isActivity;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
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

