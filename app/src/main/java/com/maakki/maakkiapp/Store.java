package com.maakki.maakkiapp;


public class Store implements java.io.Serializable {

    public double distance;
    // 編號、日期時間、顏色、標題、內容、照片檔案名稱、錄音檔案名稱、經緯度、修改、已選擇
    private long id;
    private String storename;
    private int storeid;
    private int maakkiid;
    private String picfile;
    private String address;
    private double latitude;
    private double longitude;
    private String isOpen;
    private long lastModify;
    //private boolean selected;

    // 提醒日期時間
    //private long alarmDatetime;

    public Store() {
        id = 0;
        storename = "";
        storeid = 0;
        maakkiid = 0;
        picfile = "";
        address = "";
        latitude = 0;
        longitude = 0;
        isOpen = "N";
        distance = 0;
        lastModify = 0;
    }

    public Store(long id, String storename, int storeid, int maakkiid, String picfile, String address,
                 double latitude, double longitude, String isOpen, double distance, long lastModify) {
        this.id = id;
        this.storename = storename;
        this.storeid = storeid;
        this.maakkiid = maakkiid;
        this.picfile = picfile;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isOpen = isOpen;
        this.distance = distance;
        this.lastModify = lastModify;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {this.id = id;}

    public String getStoreName() {return storename;}

    public void setStoreName(String storename) {
        this.storename = storename;
    }

    public int getStoreID() {
        return storeid;
    }

    public void setStoreID(int storeid) {
        this.storeid = storeid;
    }

    public int getMaakkiID() {
        return maakkiid;
    }

    public void setMaakkiID(int maakkiid) {
        this.maakkiid = maakkiid;
    }

    public String getPicfile() {
        return picfile;
    }

    public void setPicfile(String picfile) {
        this.picfile = picfile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getLastModify() {
        return lastModify;
    }

    public void setLastModify(long lastModify) {
        this.lastModify = lastModify;
    }

    /*public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public long getAlarmDatetime() {
        return alarmDatetime;
    }*/

}

