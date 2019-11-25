package com.maakki.maakkiapp;


public class CheckMaster implements java.io.Serializable {

    // 編號、日期時間、顏色、標題、內容、照片檔案名稱、錄音檔案名稱、經緯度、修改、已選擇
    private long check_id, order_id, check_date, invoice_date;
    private double amount;
    private int invoice_type, status, maakki_id, payment_id, discount;
    private String invoice_no, customer_title, customer_no, currency;


    // 提醒日期時間
    //private long alarmDatetime;

    public CheckMaster() {
        check_id = 0;
        order_id = 0;
        check_date = 0;
        invoice_date = 0;
        invoice_no = "";
        status = 0;   //1:已確認、0:作廢 , Default:1
        amount = 0;
        discount = 0;
        currency = "RMB";
        invoice_type = 0;
        customer_title = "";
        customer_no = "";
        maakki_id = 0;
        payment_id = 0; //0为现金，1为i点
    }

    public CheckMaster(long check_id, long order_id, long check_date, long invoice_date, String invoice_no, int status, double amount, int discount, String currency, int invoice_type, String customer_title, String customer_no, int maakki_id, int payment_id) {
        this.check_id = check_id;
        this.order_id = order_id;
        this.check_date = check_date;
        this.invoice_date = invoice_date;
        this.invoice_no = invoice_no;
        this.status = status;
        this.amount = amount;
        this.discount = discount;
        this.currency = currency;
        this.invoice_type = invoice_type;
        this.customer_title = customer_title;
        this.customer_no = customer_no;
        this.maakki_id = maakki_id;
        this.payment_id = payment_id;
    }

    public long getCheckId() {
        return check_id;
    }

    public void setCheckId(long check_id) {
        this.check_id = check_id;
    }

    public long getOrderId() {
        return order_id;
    }

    public void setOrderId(long order_id) {
        this.order_id = order_id;
    }

    public long getCheckDate() {
        return check_date;
    }

    public void setCheckDate(long check_date) {
        this.check_date = check_date;
    }

    public long getInvoiceDate() {
        return invoice_date;
    }

    public void setInvoiceDate(long invoice_date) {
        this.invoice_date = invoice_date;
    }

    public String getInvoiceNo() {
        return invoice_no;
    }

    public void setInvoiceNo(String invoice_no) {
        this.invoice_no = invoice_no;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getInvoiceType() {
        return invoice_type;
    }

    public void setInvoiceType(int invoice_type) {
        this.invoice_type = invoice_type;
    }

    public String getCustomerTitle() {
        return customer_title;
    }

    public void setCustomerTitle(String customer_title) {
        this.customer_title = customer_title;
    }

    public String getCustomerNo() {
        return customer_no;
    }

    public void setCustomerNo(String customer_no) {
        this.customer_no = customer_no;
    }

    public int getMaakkiId() {
        return maakki_id;
    }

    public void setMaakkiId(int maakki_id) {
        this.maakki_id = maakki_id;
    }

    public int getPaymentId() {
        return payment_id;
    }

    public void setPaymentId(int payment_id) {
        this.payment_id = payment_id;
    }
}

