package com.maakki.maakkiapp;


public class Category implements java.io.Serializable {

    // 編號、日期時間、顏色、標題、內容、照片檔案名稱、錄音檔案名稱、經緯度、修改、已選擇
    private long cat_id;
    private String cat_name;
    private int position, group_id, status;
    private long lastModify;
    //private boolean selected;

    // 提醒日期時間
    //private long alarmDatetime;

    public Category() {
        cat_id = 0;
        cat_name = "";
        position = 0;
        group_id = 0;
        status = 0;
        lastModify = 0;
    }

    public Category(long cat_id, String cat_name, int position, int group_id, int status, long lastModify) {
        this.cat_id = cat_id;
        this.cat_name = cat_name;
        this.position = position;
        this.group_id = group_id;
        this.status = status;
        this.lastModify = lastModify;
    }

    public long getCatId() {
        return cat_id;
    }

    public void setCatId(long cat_id) {
        this.cat_id = cat_id;
    }

    public String getCatName() {
        return cat_name;
    }

    public void setCatName(String cat_name) {
        this.cat_name = cat_name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getGroupId() {
        return group_id;
    }

    public void setGroupId(int group_id) {
        this.group_id = group_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getLastModify() {
        return lastModify;
    }

    public void setLastModify(long lastModify) {
        this.lastModify = lastModify;
    }

}

