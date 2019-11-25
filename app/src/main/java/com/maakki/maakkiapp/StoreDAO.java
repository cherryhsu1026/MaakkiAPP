package com.maakki.maakkiapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

// 資料功能類別
public class StoreDAO {
    // 表格名稱
    public static final String TABLE_NAME = "Store";

    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "_id";

    // 其它表格欄位名稱
    public static final String StoreName_COLUMN = "storename";
    public static final String StoreID_Column = "storeid";
    public static final String MaakkiID_Column = "maakkiid";
    public static final String Picfile_COLUMN = "picfile";
    public static final String Address_COLUMN = "address";
    public static final String LATITUDE_COLUMN = "latitude";
    public static final String LONGITUDE_COLUMN = "longitude";
    public static final String Isopen_Column = "isOpen";
    public static final String LASTMODIFY_COLUMN = "lastmodify";

    // 提醒日期時間
    public static final String ALARMDATETIME_COLUMN = "alarmdatetime";

    // 使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    StoreName_COLUMN + " TEXT NOT NULL, " +
                    StoreID_Column + " INTEGER NOT NULL, " +
                    MaakkiID_Column + " TEXT NOT NULL, " +
                    Picfile_COLUMN + " TEXT NOT NULL, " +
                    Address_COLUMN + " TEXT, " +
                    LATITUDE_COLUMN + " REAL, " +
                    LONGITUDE_COLUMN + " REAL, " +
                    Isopen_Column + " TEXT NOT NULL, " +
                    LASTMODIFY_COLUMN + " INTEGER)";

    // 資料庫物件
    private SQLiteDatabase db;

    // 建構子，一般的應用都不需要修改
    public StoreDAO(Context context) {
        db = MyDBHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }

    // 新增參數指定的物件
    public Store insert(Store store) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(StoreName_COLUMN, store.getStoreName());
        cv.put(StoreID_Column, store.getStoreID());
        cv.put(MaakkiID_Column, store.getMaakkiID());
        cv.put(Picfile_COLUMN, store.getPicfile());
        cv.put(Address_COLUMN, store.getAddress());
        cv.put(LATITUDE_COLUMN, store.getLatitude());
        cv.put(LONGITUDE_COLUMN, store.getLongitude());
        cv.put(Isopen_Column, store.getIsOpen());
        cv.put(LASTMODIFY_COLUMN, store.getLastModify());

        // 提醒日期時間
        //cv.put(ALARMDATETIME_COLUMN, store.getAlarmDatetime());

        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);

        // 設定編號
        store.setId(id);
        // 回傳結果
        return store;
    }

    // 修改參數指定的物件
    public boolean update(Store store) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(StoreName_COLUMN, store.getStoreName());
        cv.put(StoreID_Column, store.getStoreID());
        cv.put(MaakkiID_Column, store.getMaakkiID());
        cv.put(Picfile_COLUMN, store.getPicfile());
        cv.put(Address_COLUMN, store.getAddress());
        cv.put(LATITUDE_COLUMN, store.getLatitude());
        cv.put(LONGITUDE_COLUMN, store.getLongitude());
        cv.put(Isopen_Column, store.getIsOpen());
        cv.put(LASTMODIFY_COLUMN, store.getLastModify());

        // 提醒日期時間
        //cv.put(ALARMDATETIME_COLUMN, store.getAlarmDatetime());

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = KEY_ID + "=" + store.getId();

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
    public List<Store> getAll() {
        List<Store> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    // 取得指定編號的資料物件
    public Store get(long id) {
        // 準備回傳結果用的物件
        Store store = null;
        // 使用編號為查詢條件
        String MaakkiStore = KEY_ID + "=" + id;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, MaakkiStore, null, null, null, null, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            store = getRecord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return store;
    }

    // 取得指定編號的資料物件
    public Store getbyMaakkiID(int id) {
        // 準備回傳結果用的物件
        Store store = null;
        // 使用編號為查詢條件
        String query = MaakkiID_Column + "=" + id;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, query, null, null, null, null, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            store = getRecord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return store;
    }

    // 把Cursor目前的資料包裝為物件
    public Store getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        Store result = new Store();
        result.setId(cursor.getLong(0));
        result.setStoreName(cursor.getString(1));
        result.setStoreID(cursor.getInt(2));
        result.setMaakkiID(cursor.getInt(3));
        result.setPicfile(cursor.getString(4));
        result.setAddress(cursor.getString(5));
        result.setLatitude(cursor.getDouble(6));
        result.setLongitude(cursor.getDouble(7));
        result.setIsOpen(cursor.getString(8));
        result.setLastModify(cursor.getLong(9));

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