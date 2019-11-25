package com.maakki.maakkiapp;


public class OrderMaster implements java.io.Serializable {

    // 編號、日期時間、顏色、標題、內容、照片檔案名稱、錄音檔案名稱、經緯度、修改、已選擇
    private long order_id;
    private int isTakeOut, table_id, status;
    private long order_date;
    //private boolean selected;

    // 提醒日期時間
    //private long alarmDatetime;

    public OrderMaster() {
        order_id = 0;
        isTakeOut = 0; //外带為 1
        table_id = 0;
        status = 0; //待结账为0,已结帐为1,
        order_date = 0;
    }

    public OrderMaster(long order_id, int isTakeOut, int table_id, int status, long order_date) {
        this.order_id = order_id;
        this.isTakeOut = isTakeOut;
        this.table_id = table_id;
        this.status = status;
        this.order_date = order_date;
    }

    public long getOrderId() {
        return order_id;
    }

    public void setOrderId(long order_id) {
        this.order_id = order_id;
    }

    public int getIsTakeOut() {
        return isTakeOut;
    }

    public void setIsTakeOut(int isTakeOut) {
        this.isTakeOut = isTakeOut;
    }

    public int getTableId() {
        return table_id;
    }

    public void setTableId(int table_id) {
        this.table_id = table_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getOrderDate() {
        return order_date;
    }

    public void setOrderDate(long order_date) {
        this.order_date = order_date;
    }

}

