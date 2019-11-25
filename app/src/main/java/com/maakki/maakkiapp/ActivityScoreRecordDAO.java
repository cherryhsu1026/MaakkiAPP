package com.maakki.maakkiapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.media.Image;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;


// 資料功能類別
public class ActivityScoreRecordDAO {
    // 表格名稱
    public static final String TABLE_NAME = "ActivityScoreRecord";

    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "_id";

    public static final String Score_Column = "ActivityScore";
    public static final String ScoreType_Column = "ScoreType";
    public static final String Identifier_Column = "Identifier";
    public static final String CreateTime_COLUMN = "CreateTime";
    // 使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Score_Column + " INTEGER NOT NULL, " +
                    ScoreType_Column + " INTEGER NOT NULL, " +
                    Identifier_Column + " TEXT, " +
                    CreateTime_COLUMN + " INTEGER NOT NULL)";
    // 資料庫物件
    private SQLiteDatabase db;
    // 建構子，一般的應用都不需要修改
    public ActivityScoreRecordDAO(Context context) {
        db = PreNotificationDBHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }

    // 新增參數指定的物件
    public ActivityScoreRecord insert(ActivityScoreRecord activityscorerecord) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(Score_Column, activityscorerecord.getScore());
        cv.put(ScoreType_Column, activityscorerecord.getScoreType());
        cv.put(Identifier_Column, activityscorerecord.getIdentifier());
        cv.put(CreateTime_COLUMN, activityscorerecord.getScoreType());
        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);

        // 設定編號
        activityscorerecord.setId(id);

        // 回傳結果
        return activityscorerecord;
    }

    // 修改參數指定的物件
    public boolean update(ActivityScoreRecord activityscorerecord) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();
        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(Score_Column, activityscorerecord.getScore());
        cv.put(ScoreType_Column, activityscorerecord.getScoreType());
        cv.put(Identifier_Column, activityscorerecord.getIdentifier());
        cv.put(CreateTime_COLUMN, activityscorerecord.getScoreType());
        // 提醒日期時間
        //cv.put(ALARMDATETIME_COLUMN, store.getAlarmDatetime());

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = KEY_ID + "=" + activityscorerecord.getId();

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
    public List<ActivityScoreRecord> getAll() {
        List<ActivityScoreRecord> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    // 取得指定編號的資料物件
    public ActivityScoreRecord get(long id) {
        // 準備回傳結果用的物件
        ActivityScoreRecord activityscorerecord = null;
        // 使用編號為查詢條件
        String activityscorerecord_id = KEY_ID + "=" + id;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, activityscorerecord_id, null, null, null, null, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            activityscorerecord = getRecord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return activityscorerecord;
    }
    public int getTotalScore(){
        // 準備回傳結果用的物件
        int result = 0;
        // 使用編號為查詢條件
        Cursor cursor = db.rawQuery("SELECT SUM("+Score_Column+") FROM " + TABLE_NAME, null);
        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }

        // 關閉Cursor物件
        cursor.close();
        // 回傳結果
        return result;
    }


    // 把Cursor目前的資料包裝為物件
    public ActivityScoreRecord getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        ActivityScoreRecord result = new ActivityScoreRecord();
        result.setId(cursor.getLong(0));
        result.setScore(cursor.getInt(1));
        result.setScoreType(cursor.getInt(2));
        result.setIdentifier(cursor.getInt(3));
        result.setCreateTime(cursor.getLong(4));
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