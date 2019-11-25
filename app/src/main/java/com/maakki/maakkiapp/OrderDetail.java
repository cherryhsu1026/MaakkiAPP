package com.maakki.maakkiapp;


public class OrderDetail implements java.io.Serializable {

    // 編號、日期時間、顏色、標題、內容、照片檔案名稱、錄音檔案名稱、經緯度、修改、已選擇
    private long detail_id, order_id, item_id;
    private int qty;
    private String set_string;
    //private boolean selected;

    // 提醒日期時間
    //private long alarmDatetime;

    public OrderDetail() {
        detail_id = 0;
        order_id = 0; //外代為1
        item_id = 0;
        qty = 0;
        set_string = "";
    }

    public OrderDetail(long detail_id, long order_id, long item_id, int qty, String set_string) {
        this.detail_id = detail_id;
        this.order_id = order_id;
        this.item_id = item_id;
        this.qty = qty;
        this.set_string = set_string;
    }

    public long getDetailId() {
        return detail_id;
    }

    public void setDetailId(long detail_id) {
        this.detail_id = detail_id;
    }

    public long getOrderId() {
        return order_id;
    }

    public void setOrderId(long order_id) {
        this.order_id = order_id;
    }

    public long getItemId() {
        return item_id;
    }

    public void setItemId(long item_id) {
        this.item_id = item_id;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getSetString() {
        return set_string;
    }

    public void setSetString(String set_string) {
        this.set_string = set_string;
    }

}

