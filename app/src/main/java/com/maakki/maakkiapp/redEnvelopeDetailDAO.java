package com.maakki.maakkiapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

// 資料功能類別
public class redEnvelopeDetailDAO {
    // 表格名稱
    public static final String TABLE_NAME = "redEnvelopeDetail";

    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "_id";

    // 其它表格欄位名稱
    public static final String redenvelope_id_COLUMN = "redenvelope_id";
    public static final String maakkiid_Column = "maakkiid";
    public static final String receivedAmt_COLUMN = "receivedAmt";
    public static final String currency_COLUMN = "currency";
    public static final String reply_COLUMN = "reply";
    public static final String create_date_COLUMN = "create_date";

    // 提醒日期時間
    public static final String ALARMDATETIME_COLUMN = "alarmdatetime";

    // 使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    redenvelope_id_COLUMN + " INTEGER NOT NULL, " +
                    maakkiid_Column + " INTEGER NOT NULL, " +
                    receivedAmt_COLUMN + " REAL NOT NULL, " +
                    currency_COLUMN + " TEXT NOT NULL, " +
                    reply_COLUMN + " TEXT NOT NULL, " +
                    create_date_COLUMN + " REAL NOT NULL)";

    // 資料庫物件
    private SQLiteDatabase db;

    // 建構子，一般的應用都不需要修改
    public redEnvelopeDetailDAO(Context context) {
        db = PreNotificationDBHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }

    // 新增參數指定的物件
    public redEnvelope_Detail insert(redEnvelope_Detail redEnvelope_detail) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(redenvelope_id_COLUMN, redEnvelope_detail.getRedenvelope_id());
        cv.put(maakkiid_Column, redEnvelope_detail.getMaakkiid());
        cv.put(receivedAmt_COLUMN, redEnvelope_detail.getReceivedAmt());
        cv.put(currency_COLUMN, redEnvelope_detail.getCurrency());
        cv.put(reply_COLUMN, redEnvelope_detail.getReply());
        cv.put(create_date_COLUMN, redEnvelope_detail.getCreateDate());
        // 提醒日期時間
        //cv.put(ALARMDATETIME_COLUMN, store.getAlarmDatetime());
        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);

        // 設定編號
        redEnvelope_detail.setId(id);
        // 回傳結果
        return redEnvelope_detail;
    }

    // 修改參數指定的物件
    public boolean update(redEnvelope_Detail redEnvelope_detail) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(redenvelope_id_COLUMN, redEnvelope_detail.getRedenvelope_id());
        cv.put(maakkiid_Column, redEnvelope_detail.getMaakkiid());
        cv.put(receivedAmt_COLUMN, redEnvelope_detail.getReceivedAmt());
        cv.put(currency_COLUMN, redEnvelope_detail.getCurrency());
        cv.put(reply_COLUMN, redEnvelope_detail.getReply());
        cv.put(create_date_COLUMN, redEnvelope_detail.getCreateDate());

        // 提醒日期時間
        //cv.put(ALARMDATETIME_COLUMN, store.getAlarmDatetime());

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = KEY_ID + "=" + redEnvelope_detail.getId();

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
    public List<redEnvelope_Detail> getAll() {
        List<redEnvelope_Detail> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    // 取得指定編號的資料物件
    public redEnvelope_Detail get(long id) {
        // 準備回傳結果用的物件
        redEnvelope_Detail redEnvelope_detail = null;
        // 使用編號為查詢條件
        String redEnvelope_Detailid = KEY_ID + "=" + id;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, redEnvelope_Detailid, null, null, null, null, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            redEnvelope_detail = getRecord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return redEnvelope_detail;
    }


    // 把Cursor目前的資料包裝為物件
    public redEnvelope_Detail getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        redEnvelope_Detail result = new redEnvelope_Detail();
        result.setId(cursor.getInt(0));
        result.setRedenvelope_id(cursor.getInt(1));
        result.setMaakkiid(cursor.getInt(2));
        result.setReceivedAmt(cursor.getDouble(3));
        result.setCurrency(cursor.getString(4));
        result.setReply(cursor.getString(5));
        result.setCreateDate(cursor.getLong(6));

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