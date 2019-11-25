package com.maakki.maakkiapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

// 資料功能類別
public class CheckMasterDAO {
    // 表格名稱
    public static final String TABLE_NAME = "CheckMaster";

    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "check_id";

    // 其它表格欄位名稱
    public static final String OrderID_COLUMN = "order_id";
    public static final String CheckDate_COLUMN = "check_date";
    public static final String InvoiceDate_COLUMN = "invoice_date";
    public static final String InvoiceNo_COLUMN = "invoice_no";
    public static final String Status_COLUMN = "status";
    public static final String Amount_COLUMN = "amount";
    public static final String Discount_COLUMN = "discount";
    public static final String Currency_COLUMN = "currency";
    public static final String InvoiceType_COLUMN = "invoice_type";
    public static final String CustomerTitle_COLUMN = "customer_title";
    public static final String CustomerNo_COLUMN = "customer_no";
    public static final String MaakkiID_COLUMN = "maakki_id";
    public static final String PaymentID_COLUMN = "payment_id";
    // 使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    OrderID_COLUMN + " INTEGER NOT NULL, " +
                    CheckDate_COLUMN + " INTEGER NOT NULL, " +
                    InvoiceDate_COLUMN + " INTEGER, " +
                    InvoiceNo_COLUMN + " Text, " +
                    Status_COLUMN + " INTEGER NOT NULL, " +
                    Amount_COLUMN + " REAL NOT NULL, " +
                    Discount_COLUMN + " INTEGER NOT NULL, " +
                    Currency_COLUMN + " String NOT NULL, " +
                    InvoiceType_COLUMN + " INTEGER NOT NULL, " +
                    CustomerTitle_COLUMN + " Text, " +
                    CustomerNo_COLUMN + " Text, " +
                    MaakkiID_COLUMN + " INTEGER, " +
                    PaymentID_COLUMN + " INTEGER NOT NULL)";


    // 資料庫物件//
    private SQLiteDatabase db;

    // 建構子，一般的應用都不需要修改
    public CheckMasterDAO(Context context) {
        db = MyDBHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }

    // 新增參數指定的物件
    public CheckMaster insert(CheckMaster checkMaster) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(OrderID_COLUMN, checkMaster.getOrderId());
        cv.put(CheckDate_COLUMN, checkMaster.getCheckDate());
        cv.put(InvoiceDate_COLUMN, checkMaster.getInvoiceDate());
        cv.put(InvoiceNo_COLUMN, checkMaster.getInvoiceNo());
        cv.put(Status_COLUMN, checkMaster.getStatus());
        cv.put(Amount_COLUMN, checkMaster.getAmount());
        cv.put(Discount_COLUMN, checkMaster.getDiscount());
        cv.put(Currency_COLUMN, checkMaster.getCurrency());
        cv.put(InvoiceType_COLUMN, checkMaster.getInvoiceType());
        cv.put(CustomerTitle_COLUMN, checkMaster.getCustomerTitle());
        cv.put(CustomerNo_COLUMN, checkMaster.getCustomerNo());
        cv.put(MaakkiID_COLUMN, checkMaster.getMaakkiId());
        cv.put(PaymentID_COLUMN, checkMaster.getPaymentId());
        // 提醒日期時間
        //cv.put(ALARMDATETIME_COLUMN, store.getAlarmDatetime());

        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);

        // 設定編號
        checkMaster.setCheckId(id);
        // 回傳結果
        return checkMaster;
    }

    // 修改參數指定的物件
    public boolean update(CheckMaster checkMaster) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        cv.put(OrderID_COLUMN, checkMaster.getOrderId());
        cv.put(CheckDate_COLUMN, checkMaster.getCheckDate());
        cv.put(InvoiceDate_COLUMN, checkMaster.getInvoiceDate());
        cv.put(InvoiceNo_COLUMN, checkMaster.getInvoiceNo());
        cv.put(Status_COLUMN, checkMaster.getStatus());
        cv.put(Amount_COLUMN, checkMaster.getAmount());
        cv.put(Discount_COLUMN, checkMaster.getDiscount());
        cv.put(Currency_COLUMN, checkMaster.getCurrency());
        cv.put(InvoiceType_COLUMN, checkMaster.getInvoiceType());
        cv.put(CustomerTitle_COLUMN, checkMaster.getCustomerTitle());
        cv.put(CustomerNo_COLUMN, checkMaster.getCustomerNo());
        cv.put(MaakkiID_COLUMN, checkMaster.getMaakkiId());
        cv.put(PaymentID_COLUMN, checkMaster.getPaymentId());
        // 提醒日期時間
        //cv.put(ALARMDATETIME_COLUMN, store.getAlarmDatetime());

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = KEY_ID + "=" + checkMaster.getCheckId();

        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    // 刪除參數指定編號的資料
    public boolean delete(long id) {
        // 設定條件為編號，格式為「欄位名稱=資料」
        String where = KEY_ID + "=" + id;
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME, where, null) > 0;
    }

    public boolean deleteByOrderId(long id) {
        // 設定條件為編號，格式為「欄位名稱=資料」
        String where = OrderID_COLUMN + "=" + id;
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME, where, null) > 0;
    }

    /*public long getNextOrderId(long order_id){
        // 準備回傳結果用的物件
        OrderMaster orderMaster = null;
        // 使用編號為查詢條件
        String SELECTSTRING = KEY_ID + ">" + order_id ;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, SELECTSTRING, null, null, null, null, null);
        //Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ID +"=" +id, null);
        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            orderMaster = getRecord(result);
        }
        else{return 0;}
        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return orderMaster.getOrderId();
    }
    public long getPreviousOrderId(long order_id){
        // 準備回傳結果用的物件
        OrderMaster orderMaster = null;
        // 使用編號為查詢條件
        String SELECTSTRING = KEY_ID + "<" + order_id ;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, SELECTSTRING, null, null, null, null, null);
        //Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ID +"=" +id, null);
        // 如果有查詢結果
        if (result.moveToLast()) {
            // 讀取包裝一筆資料的物件
            orderMaster = getRecord(result);
        }
        else{return 0;}
        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return orderMaster.getOrderId();
    }*/
    public void deleteAll() {
        db.execSQL("delete from " + TABLE_NAME);
        db.close();
    }

    // 讀取所有記事資料
    public List<CheckMaster> getAll() {
        List<CheckMaster> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    public CheckMaster getByOrderID(long id) {
        // 準備回傳結果用的物件
        CheckMaster checkMaster = null;
        // 使用編號為查詢條件
        String SELECTSTRING = OrderID_COLUMN + "=" + id;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, SELECTSTRING, null, null, null, null, null);
        //Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ID +"=" +id, null);
        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            checkMaster = getRecord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return checkMaster;
    }


    public CheckMaster get(long id) {
        // 準備回傳結果用的物件
        CheckMaster checkMaster = null;
        // 使用編號為查詢條件
        String SELECTSTRING = KEY_ID + "=" + id;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, SELECTSTRING, null, null, null, null, null);
        //Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ID +"=" +id, null);
        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            checkMaster = getRecord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return checkMaster;
    }

    // 把Cursor目前的資料包裝為物件
    public CheckMaster getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        CheckMaster result = new CheckMaster();
        result.setCheckId(cursor.getLong(0));
        result.setOrderId(cursor.getLong(1));
        result.setCheckDate(cursor.getLong(2));
        result.setInvoiceDate(cursor.getLong(3));
        result.setInvoiceNo(cursor.getString(4));
        result.setStatus(cursor.getInt(5));
        result.setAmount(cursor.getDouble(6));
        result.setDiscount(cursor.getInt(7));
        result.setCurrency(cursor.getString(8));
        result.setInvoiceType(cursor.getInt(9));
        result.setCustomerTitle(cursor.getString(10));
        result.setCustomerNo(cursor.getString(11));
        result.setMaakkiId(cursor.getInt(12));
        result.setPaymentId(cursor.getInt(13));
        // 提醒日期時間
        //result.setAlarmDatetime(cursor.getLong(9));
        // 回傳結果
        return result;
    }

    // 取得資料數量
    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        return result;
    }


}