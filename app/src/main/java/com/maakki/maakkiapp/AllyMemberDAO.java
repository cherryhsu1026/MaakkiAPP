package com.maakki.maakkiapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


// 資料功能類別
public class AllyMemberDAO {
    // 表格名稱
    public static final String TABLE_NAME = "ally_member";

    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "_id";

    public static final String UniqueAllyId_Column = "UniqueAllyId";
    public static final String Maakki_id_Column = "Maakki_id";
    public static final String Member_status_Column = "Member_status";
    public static final String Order_COLUMN = "DonateOrder";
    public static final String IsSkip_COLUMN = "IsSkip";
    public static final String LastCheckTime_COLUMN = "LastCheckTime";
    // 使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    UniqueAllyId_Column + " TEXT NOT NULL, " +
                    Maakki_id_Column + " INTEGER NOT NULL, " +
                    Member_status_Column + " INTEGER NOT NULL, " +
                    Order_COLUMN + " INTEGER NOT NULL, " +
                    IsSkip_COLUMN + " TEXT NOT NULL, " +
                    LastCheckTime_COLUMN + " INTEGER NOT NULL)";

    // 資料庫物件
    private SQLiteDatabase db;
    // 建構子，一般的應用都不需要修改
    public AllyMemberDAO(Context context) {
        db = PreNotificationDBHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }

    // 新增參數指定的物件
    public AllyMember insert(AllyMember allyMember) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();
        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(UniqueAllyId_Column, allyMember.getUniqueAllyId());
        cv.put(Maakki_id_Column, allyMember.getMaakki_id());
        cv.put(Member_status_Column, allyMember.getMember_status());
        cv.put(Order_COLUMN, allyMember.getOrder());
        cv.put(IsSkip_COLUMN, String.valueOf(allyMember.getIsSkip()));
        cv.put(LastCheckTime_COLUMN, allyMember.getLastCheckTime());
        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);

        // 設定編號
        allyMember.setId(id);

        // 回傳結果
        return allyMember;
    }

    // 修改參數指定的物件
    public boolean update(AllyMember allyMember) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();
        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(UniqueAllyId_Column, allyMember.getUniqueAllyId());
        cv.put(Maakki_id_Column, allyMember.getMaakki_id());
        cv.put(Member_status_Column, allyMember.getMember_status());
        cv.put(Order_COLUMN, allyMember.getOrder());
        cv.put(IsSkip_COLUMN, String.valueOf(allyMember.getIsSkip()));
        cv.put(LastCheckTime_COLUMN, allyMember.getLastCheckTime());
        // 提醒日期時間
        //cv.put(ALARMDATETIME_COLUMN, store.getAlarmDatetime());

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = KEY_ID + "=" + allyMember.getId();

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
    public List<AllyMember> getAll() {
        List<AllyMember> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    // 取得指定編號的資料物件
    public AllyMember get(long id) {
        // 準備回傳結果用的物件
        AllyMember allyMember = null;
        // 使用編號為查詢條件
        String ally_id = KEY_ID + "=" + id;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, ally_id, null, null, null, null, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            allyMember = getRecord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return allyMember;
    }


    // 把Cursor目前的資料包裝為物件
    public AllyMember getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        AllyMember result = new AllyMember();
        result.setId(cursor.getLong(0));
        result.setUniqueAllyId(cursor.getString(1));
        result.setMaakki_id(cursor.getInt(2));
        result.setMember_status(cursor.getInt(3));
        result.setOredr(cursor.getInt(4));
        result.setIsSkip(Boolean.parseBoolean(cursor.getString(5)));
        result.setLastCheckTime(cursor.getLong(6));
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