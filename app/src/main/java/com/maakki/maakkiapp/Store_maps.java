package com.maakki.maakkiapp;

/**
 * Created by ryan on 2016/8/28.
 */


import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ryan on 2016/8/16.
 */

public class Store_maps {
    private String Storename, Address, picfile;
    private int sid;
    private LatLng Latlng;
    private int mid;

    public Store_maps() {
    }

    public Store_maps(LatLng Latlng, String Storename, String Address, String picfile,int mid) {
        this.Latlng = Latlng;
        this.Storename = Storename;
        this.Address = Address;
        this.picfile = picfile;
        this.mid=mid;
    }

    public int getStoreid() {
        return sid;
    }

    public void setStoreid(int sid) {
        this.sid = sid;
    }

    public LatLng getLatLng() {
        return Latlng;
    }

    public void setLatLng(LatLng Latlng) {
        this.Latlng = Latlng;
    }

    public String getStorename() {
        return Storename;
    }

    public void setStorename(String Storename) {
        this.Storename = Storename;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public String getPicfile() {
        return picfile;
    }

    public void setPicfile(String picfile) {
        this.picfile = picfile;
    }

    public int getMaakkiid(){return mid;}

    public void setMaakkiid(int mid){this.mid=mid;}

}

