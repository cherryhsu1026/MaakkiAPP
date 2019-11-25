package com.maakki.maakkiapp;


public class CheckDetail implements java.io.Serializable {

    // 編號、日期時間、顏色、標題、內容、照片檔案名稱、錄音檔案名稱、經緯度、修改、已選擇
    private long detail_id, check_id, item_id;
    private int qty;
    private double price;
    private String item_name;
    //private boolean selected;

    // 提醒日期時間
    //private long alarmDatetime;

    public CheckDetail() {
        detail_id = 0;
        check_id = 0;
        item_id = 0;
        item_name = "";
        qty = 0;
        price = 0;
    }

    public CheckDetail(long detail_id, long check_id, long item_id, String item_name, int qty, double price) {
        this.detail_id = detail_id;
        this.check_id = check_id;
        this.item_id = item_id;
        this.item_name = item_name;
        this.qty = qty;
        this.price = price;
    }

    public long getDetailId() {
        return detail_id;
    }

    public void setDetailId(long detail_id) {
        this.detail_id = detail_id;
    }

    public long getCheckId() {
        return check_id;
    }

    public void setCheckId(long check_id) {
        this.check_id = check_id;
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

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

}

