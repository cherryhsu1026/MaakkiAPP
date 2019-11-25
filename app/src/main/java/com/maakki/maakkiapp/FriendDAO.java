package com.maakki.maakkiapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

// 資料功能類別
public class FriendDAO {
    // 表格名稱
    public static final String TABLE_NAME = "Friend";

    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "_id";

    // 其它表格欄位名稱
    public static final String MaakkiID_Column = "maakki_id";
    public static final String Memid_COLUMN = "member_id";
    public static final String Nickname_COLUMN = "nickname";
    public static final String isNotify_COLUMN = "isNotify";
    public static final String Picfile_COLUMN = "picfile";
    public static final String FriendType_COLUMN = "friend_type";
    public static final String LastMessageTime_COLUMN = "LastMessageTime";
    public static final String LastChatTime_COLUMN = "LastChatTime";

    // 提醒日期時間
    public static final String ALARMDATETIME_COLUMN = "alarmdatetime";

    // 使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MaakkiID_Column + " INTEGER NOT NULL, " +
                    Memid_COLUMN + " INTEGER NOT NULL, " +
                    Nickname_COLUMN +" TEXT NOT NULL, " +
                    isNotify_COLUMN +" TEXT, " +
                    Picfile_COLUMN + " TEXT NOT NULL, " +
                    FriendType_COLUMN + " TEXT NOT NULL," +
                    LastMessageTime_COLUMN + " REAL, " +
                    LastChatTime_COLUMN + " REAL) ";

    // 資料庫物件
    private SQLiteDatabase db;

    // 建構子，一般的應用都不需要修改
    public FriendDAO(Context context) {
        db = PreNotificationDBHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }

    // 新增參數指定的物件
    public Friend insert(Friend friend) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();
        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(Memid_COLUMN, friend.getMemid());
        cv.put(MaakkiID_Column, friend.getMaakkiid());
        cv.put(Nickname_COLUMN, friend.getNickName());
        cv.put(isNotify_COLUMN, String.valueOf(friend.getIsNotify()));
        cv.put(Picfile_COLUMN, friend.getPicfilePath());
        cv.put(FriendType_COLUMN, friend.getFriendType());
        cv.put(LastMessageTime_COLUMN, friend.getLastMessageTime());
        cv.put(LastChatTime_COLUMN, friend.getLastChatTime());

        long id = db.insert(TABLE_NAME, null, cv);

        // 設定編號
        friend.setId(id);
        // 回傳結果
        return friend;
    }

    // 修改參數指定的物件
    public boolean update(Friend friend) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();
        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(Memid_COLUMN, friend.getMemid());
        cv.put(MaakkiID_Column, friend.getMaakkiid());
        cv.put(Nickname_COLUMN, friend.getNickName());
        cv.put(isNotify_COLUMN, String.valueOf(friend.getIsNotify()));
        cv.put(Picfile_COLUMN, friend.getPicfilePath());
        cv.put(FriendType_COLUMN, friend.getFriendType());
        cv.put(LastMessageTime_COLUMN, friend.getLastMessageTime());
        cv.put(LastChatTime_COLUMN, friend.getLastChatTime());
        // 提醒日期時間
        //cv.put(ALARMDATETIME_COLUMN, store.getAlarmDatetime());

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = KEY_ID + "=" + friend.getId();

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

    // 讀取所有联络人資料
    public List<Friend> getAll() {
        List<Friend> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, LastMessageTime_COLUMN+" DESC", null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }
    // 讀取所有朋友資料
    public List<Friend> getList(String getType) {
        List<Friend> result = new ArrayList<>();
        String query = FriendType_COLUMN + " = '" +getType+"'"
                +" ORDER BY "+LastMessageTime_COLUMN+" DESC";
        // 執行查詢
        Cursor cursor = db.query(
                TABLE_NAME, null, query, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }
        cursor.close();
        return result;
    }

    // 讀取所有朋友的member_id
    public ArrayList getFriendMemberList() {
        ArrayList result = new ArrayList<>();
        String query = FriendType_COLUMN + " = 'F'"+
                " ORDER BY "+LastMessageTime_COLUMN+" DESC";
        // 執行查詢
        Cursor cursor = db.query(
                TABLE_NAME, null, query, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(String.valueOf(getRecord(cursor).getMemid()));
        }
        cursor.close();
        return result;
    }
    // 取得指定編號的資料物件
    public Friend get(long id) {
        // 準備回傳結果用的物件
        Friend friend = null;
        // 使用編號為查詢條件
        String MaakkiStore = KEY_ID + "=" + id;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, MaakkiStore, null, null, null, null, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            friend = getRecord(result);
        }
        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return friend;
    }

    // 取得指定編號的資料物件
    public Friend getbyMaakkiID(int id) {
        // 準備回傳結果用的物件
        Friend friend = null;
        // 使用編號為查詢條件
        String query = MaakkiID_Column + "=" + id;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, query, null, null, null, null, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            friend = getRecord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return friend;
    }

    public Friend getbyMemberID(int id){
        // 準備回傳結果用的物件
        Friend friend = null;
        // 使用編號為查詢條件
        String query = Memid_COLUMN + "=" + id;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, query, null, null, null, null, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            friend = getRecord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return friend;
    }
    // 把Cursor目前的資料包裝為物件
    public Friend getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        Friend result = new Friend();
        result.setId(cursor.getLong(0));
        result.setMaakkiid(cursor.getInt(1));
        result.setMemid(cursor.getInt(2));
        result.setNickName(cursor.getString(3));
        result.setIsNotify(Boolean.parseBoolean(cursor.getString(4)));
        result.setPicfilePath(cursor.getString(5));
        result.setFriendType(cursor.getString(6));
        result.setLastMessageTime(cursor.getLong(7));
        result.setLastChatTime(cursor.getLong(8));
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