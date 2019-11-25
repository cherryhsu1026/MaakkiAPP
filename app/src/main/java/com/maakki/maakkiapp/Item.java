package com.maakki.maakkiapp;


public class Item implements java.io.Serializable {

    private long item_id;
    private String item_name;
    private long cat_id;
    private int item_type;
    private double price;
    private int status;
    private int position;
    private long lastModify;

    public Item() {
        item_id = 0;
        item_name = "";
        cat_id = 0;
        item_type = 0;
        price = 0;
        status = 0;
        position = 0;
        lastModify = 0;
    }

    public Item(long item_id, String item_name, long cat_id, int item_type, double price, int status, int position, long lastModify) {
        this.item_id = item_id;
        this.item_name = item_name;
        this.cat_id = cat_id;
        this.item_type = item_type;
        this.price = price;
        this.status = status;
        this.position = position;
        this.lastModify = lastModify;
    }

    public long getItemId() {
        return item_id;
    }

    public void setItemId(long item_id) {
        this.item_id = item_id;
    }

    public String getItemName() {
        return item_name;
    }

    public void setItemName(String item_name) {
        this.item_name = item_name;
    }

    public long getCatId() {
        return cat_id;
    }

    public void setCatId(long cat_id) {
        this.cat_id = cat_id;
    }

    public int getItemType() {
        return item_type;
    }

    public void setItemType(int item_type) {
        this.item_type = item_type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getLastModify() {
        return lastModify;
    }

    public void setLastModify(long lastModify) {
        this.lastModify = lastModify;
    }

}

