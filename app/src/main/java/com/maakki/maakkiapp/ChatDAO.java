package com.maakki.maakkiapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

// 資料功能類別
public class ChatDAO {
    // 表格名稱
    public static final String TABLE_NAME = "Chat";

    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "_id";

    // 其它表格欄位名稱
    public static final String Senderid_COLUMN = "senderid";
    public static final String ReceiverId_COLUMN = "receiverid";
    public static final String ContentText_COLUMN = "contenttext";
    public static final String FilePath_COLUMN = "filepath";
    public static final String FileType_COLUMN = "filetype";
    public static final String UniqueGroupId_COLUMN = "UniqueGroupId";
    public static final String CreateDateTime_COLUMN = "createdatetime";

    // 提醒日期時間
    //public static final String ALARMDATETIME_COLUMN = "alarmdatetime";

    // 使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Senderid_COLUMN + " INTEGER NOT NULL, " +
                    ReceiverId_COLUMN + " INTEGER NOT NULL, " +
                    ContentText_COLUMN + " TEXT, " +
                    FilePath_COLUMN + " TEXT, " +
                    FileType_COLUMN + " INTEGER, " +
                    UniqueGroupId_COLUMN + " TEXT, " +
                    CreateDateTime_COLUMN + " REAL NOT NULL)";
                  //LASTMODIFY_COLUMN + " INTEGER NOT NULL)";

    // 資料庫物件//
    private SQLiteDatabase db;

    // 建構子，一般的應用都不需要修改
    public ChatDAO(Context context) {
        db = MyDBHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }

    // 新增參數指定的物件
    public Chat insert(Chat chat) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(Senderid_COLUMN, chat.getSenderid());
        cv.put(ReceiverId_COLUMN, chat.getReceiverid());
        cv.put(ContentText_COLUMN, chat.getContentText());
        cv.put(FilePath_COLUMN, chat.getFilePath());
        cv.put(FileType_COLUMN , chat.getFileType());
        cv.put(UniqueGroupId_COLUMN, chat.getUniqueGroupId());
        cv.put(CreateDateTime_COLUMN, chat.getCreateDate());

        // 提醒日期時間
        //cv.put(ALARMDATETIME_COLUMN, store.getAlarmDatetime());

        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);

        // 設定編號
        chat.setId(id);
        // 回傳結果
        return chat;
    }

    // 修改參數指定的物件
    public boolean update(Chat chat) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(Senderid_COLUMN, chat.getSenderid());
        cv.put(ReceiverId_COLUMN, chat.getReceiverid());
        cv.put(ContentText_COLUMN, chat.getContentText());
        cv.put(FilePath_COLUMN, chat.getFilePath());
        cv.put(FileType_COLUMN , chat.getFileType());
        cv.put(UniqueGroupId_COLUMN, chat.getUniqueGroupId());
        cv.put(CreateDateTime_COLUMN, chat.getCreateDate());


        // 提醒日期時間
        //cv.put(ALARMDATETIME_COLUMN, store.getAlarmDatetime());

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = KEY_ID + "=" + chat.getId();

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
    public List<Chat> getAll() {
        List<Chat> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    public Chat get(long id) {
        // 準備回傳結果用的物件
        Chat chat = null;
        // 使用編號為查詢條件
        String SELECTSTRING = KEY_ID + "=" + id;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, SELECTSTRING, null, null, null, null, null);
        //Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ID +"=" +id, null);
        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            chat = getRecord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return chat;
    }
    // 取得指定編號的資料物件
    public List<Chat> getbyContactID(int contactid) {
        // 準備回傳結果用的物件
        List<Chat> result = new ArrayList<>();
        // 使用編號為查詢條件
        String query = Senderid_COLUMN + "=" + contactid +" OR "+
                       ReceiverId_COLUMN + "=" + contactid;
        // 執行查詢
        Cursor cursor = db.query(
                TABLE_NAME, null, query, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    public int getCountByContactIDSinceLastGetin(String contactid,Long lastgetintime) {
        // 準備回傳結果用的物件
        List<Chat> result = new ArrayList<>();
        // 使用編號為查詢條件
        String query = Senderid_COLUMN + "=" + contactid + " AND "+
                CreateDateTime_COLUMN + " > "+lastgetintime;
        // 執行查詢
        Cursor cursor = db.query(
                TABLE_NAME, null, query, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result.size();
    }


    public Chat getLastChatbyContactID(int contactid) {
        // 準備回傳結果用的物件
        Chat chat = null;
        // 使用編號為查詢條件
        String query = Senderid_COLUMN + "=" + contactid +" ORDER BY "+
                CreateDateTime_COLUMN +" DESC";
        // 執行查詢
        Cursor cursor = db.query(
                TABLE_NAME, null, query, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            chat = getRecord(cursor);
        }

        cursor.close();
        return chat;
    }
    // 把Cursor目前的資料包裝為物件
    public Chat getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        Chat result = new Chat();
        result.setId(cursor.getLong(0));
        result.setSenderid(cursor.getInt(1));
        result.setReceiverid(cursor.getInt(2));
        result.setContentText(cursor.getString(3));
        result.setFilePath(cursor.getString(4));
        result.setFileType(cursor.getInt(5));
        result.setUniqueGroupId(cursor.getString(6));
        result.setCreateDate(cursor.getLong(7));
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

    public void editChatContentByChatid(long chatid, String content) {
        Chat chat = this.get(chatid);
        chat.setContentText(content);
        this.update(chat);
    }
}