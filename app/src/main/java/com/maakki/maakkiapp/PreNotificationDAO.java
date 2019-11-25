package com.maakki.maakkiapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


// 資料功能類別
public class PreNotificationDAO {
    // 表格名稱
    public static final String TABLE_NAME = "Notification";

    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "_id";

    // 其它表格欄位名稱
    public static final String Title_COLUMN = "title";
    public static final String Message_Column = "message";
    //1代表Chat_public,2代表Chat_private,3代表admin_receiver,4代表inform,5代表MGS通知
    public static final String MessageType_Column = "type";
    public static final String Picfile_COLUMN = "picfile";
    //判断pintent，1是要传给Activity，2还是要连结到网址
    //public static final String isActivity_COLUMN = "isActivity";
    //将索要开启的Activity或是要连结到的Url记录在这
    //public static final String ActivityOrUrl_COLUMN = "activityOrUrl_COLUMN";
    //日期時間
    public static final String LASTMODIFY_COLUMN = "lastmodify";

    // 使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Title_COLUMN + " TEXT NOT NULL, " +
                    Message_Column + " TEXT NOT NULL, " +
                    MessageType_Column + " INTEGER NOT NULL, " +
                    Picfile_COLUMN + " TEXT NOT NULL, " +
                    LASTMODIFY_COLUMN + " INTEGER NOT NULL)";
    // 資料庫物件
    private SQLiteDatabase db;

    // 建構子，一般的應用都不需要修改
    public PreNotificationDAO(Context context) {
        db = PreNotificationDBHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }

    // 新增參數指定的物件
    public PreNotification insert(PreNotification prev) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(Title_COLUMN, prev.gettitle());
        cv.put(Message_Column, prev.getmessage());
        cv.put(MessageType_Column, prev.getmessagetype());
        cv.put(Picfile_COLUMN, prev.getPicfile());
        //cv.put(isActivity_COLUMN, prev.getisActivity());
        //cv.put(ActivityOrUrl_COLUMN, prev.getActivityOrUrl());
        cv.put(LASTMODIFY_COLUMN, prev.getLastModify());


        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);

        // 設定編號
        prev.setid(id);

        // 回傳結果
        return prev;
    }

    // 修改參數指定的物件
    public boolean update(PreNotification prev) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(Title_COLUMN, prev.gettitle());
        cv.put(Message_Column, prev.getmessage());
        cv.put(MessageType_Column, prev.getmessagetype());
        cv.put(Picfile_COLUMN, prev.getPicfile());
        //cv.put(isActivity_COLUMN, prev.getisActivity());
        //cv.put(ActivityOrUrl_COLUMN, prev.getActivityOrUrl());
        cv.put(LASTMODIFY_COLUMN, prev.getLastModify());

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = KEY_ID + "=" + prev.getid();

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

    public void deleteAll() {
        db.execSQL("delete from " + TABLE_NAME);
        db.close();
    }

    // 讀取所有記事資料
    public List<PreNotification> getAll() {
        List<PreNotification> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    // 取得指定編號的資料物件
    public PreNotification get(long id) {
        // 準備回傳結果用的物件
        PreNotification prev = null;
        // 使用編號為查詢條件
        String NotificationID = KEY_ID + "=" + id;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, NotificationID, null, null, null, null, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            prev = getRecord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return prev;
    }

    // 取得指定編號的資料物件
    public PreNotification getbyNotificationiID(long id) {
        // 準備回傳結果用的物件
        PreNotification prev = null;
        // 使用編號為查詢條件
        String query = KEY_ID + "=" + id;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, query, null, null, null, null, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            prev = getRecord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return prev;
    }
    // 取得指定編號的資料物件
    public PreNotification getLastNotificationTime() {
        // 準備回傳結果用的物件
        PreNotification result = null;
        // 使用編號為查詢條件
        //String query = MaakkiID_Column + "=" + id;
        // 執行查詢
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, LASTMODIFY_COLUMN+" DESC ", null);

        // 如果有查詢結果
        if (cursor.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            result = getRecord(cursor);
        }

        // 關閉Cursor物件
        cursor.close();
        // 回傳結果
        return result;
    }
    // 把Cursor目前的資料包裝為物件
    public PreNotification getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        PreNotification result = new PreNotification();
        result.setid(cursor.getLong(0));
        result.settitle(cursor.getString(1));
        result.setmessage(cursor.getString(2));
        result.setmessagetype(cursor.getInt(3));
        result.setPicfile(cursor.getString(4));
        result.setLastModify(cursor.getLong(5));
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