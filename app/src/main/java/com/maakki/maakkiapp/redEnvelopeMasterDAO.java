package com.maakki.maakkiapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


// 資料功能類別
public class redEnvelopeMasterDAO {
    // 表格名稱
    public static final String TABLE_NAME = "redEnvelopeMaster";

    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "_id";

    public static final String donateAmt_COLUMN = "donateAmt";
    public static final String takenAmt_Column = "takenAmt";
    public static final String currency_Column = "currency";
    public static final String receiver_no_COLUMN = "receiver_no";
    public static final String isAverage_COLUMN = "isAverage";
    public static final String isAnonymous_COLUMN = "isAnonymous";
    public static final String isFinish_COLUMN = "isFinish";
    public static final String isFriendLimited_COLUMN = "IsFriendLimited";
    public static final String slogan_COLUMN = "slogan";
    public static final String picfile_COLUMN = "picfile";
    public static final String urllink_COLUMN = "urllink";
    public static final String iCreditPassword_COLUMN = "iCreditPassword";
    //日期時間
    public static final String create_date_COLUMN = "create_date";
    private Context context;
    // 使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    donateAmt_COLUMN + " REAL NOT NULL, " +
                    takenAmt_Column + " REAL NOT NULL, " +
                    currency_Column + " TEXT NOT NULL, " +
                    receiver_no_COLUMN + " INTEGER NOT NULL, " +
                    isAverage_COLUMN + " TEXT NOT NULL, " +
                    isAnonymous_COLUMN + " TEXT NOT NULL, " +
                    isFinish_COLUMN+ " TEXT NOT NULL, " +
                    isFriendLimited_COLUMN+ " TEXT NOT NULL, " +
                    slogan_COLUMN + " TEXT, " +
                    picfile_COLUMN + " TEXT, " +
                    urllink_COLUMN + " TEXT, " +
                    iCreditPassword_COLUMN+ " TEXT NOT NULL, " +
                    create_date_COLUMN + " REAL NOT NULL)";
    // 資料庫物件
    private SQLiteDatabase db;

    // 建構子，一般的應用都不需要修改
    public redEnvelopeMasterDAO(Context context) {
        db = PreNotificationDBHelper.getDatabase(context);
        this.context=context;
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }

    // 新增參數指定的物件
    public redEnvelope_Master insert(redEnvelope_Master redEnvelope_master) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(donateAmt_COLUMN, redEnvelope_master.getDonateAmt());
        cv.put(takenAmt_Column, redEnvelope_master.getTakenAmt());
        cv.put(currency_Column, redEnvelope_master.getCurrency());
        cv.put(receiver_no_COLUMN, redEnvelope_master.getReceiver_no());
        cv.put(isAverage_COLUMN, String.valueOf(redEnvelope_master.getIsAverage()));
        cv.put(isAnonymous_COLUMN, String.valueOf(redEnvelope_master.getIsAnonymous()));
        cv.put(isFinish_COLUMN, String.valueOf(redEnvelope_master.getIsFinish()));
        cv.put(isFriendLimited_COLUMN, String.valueOf(redEnvelope_master.getIsFriendLimited()));
        cv.put(picfile_COLUMN, redEnvelope_master.getPicfile());
        cv.put(urllink_COLUMN, redEnvelope_master.getUrllink());
        cv.put(iCreditPassword_COLUMN, redEnvelope_master.getiCreditPassword());
        cv.put(create_date_COLUMN, redEnvelope_master.getCreateDate());


        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);

        // 設定編號
        redEnvelope_master.setId(id);

        // 回傳結果
        return redEnvelope_master;
    }

    // 修改參數指定的物件
    public boolean update(redEnvelope_Master redEnvelope_master) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();
        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(donateAmt_COLUMN, redEnvelope_master.getDonateAmt());
        cv.put(takenAmt_Column, redEnvelope_master.getTakenAmt());
        cv.put(currency_Column, redEnvelope_master.getCurrency());
        cv.put(receiver_no_COLUMN, redEnvelope_master.getReceiver_no());
        cv.put(isAverage_COLUMN, String.valueOf(redEnvelope_master.getIsAverage()));
        cv.put(isAnonymous_COLUMN, String.valueOf(redEnvelope_master.getIsAnonymous()));
        cv.put(isFinish_COLUMN, String.valueOf(redEnvelope_master.getIsFinish()));
        cv.put(isFriendLimited_COLUMN, String.valueOf(redEnvelope_master.getIsFriendLimited()));
        cv.put(slogan_COLUMN, redEnvelope_master.getSlogan());
        cv.put(picfile_COLUMN, redEnvelope_master.getPicfile());
        cv.put(urllink_COLUMN, redEnvelope_master.getUrllink());
        cv.put(iCreditPassword_COLUMN, redEnvelope_master.getiCreditPassword());
        cv.put(create_date_COLUMN, redEnvelope_master.getCreateDate());

        // 提醒日期時間
        //cv.put(ALARMDATETIME_COLUMN, store.getAlarmDatetime());

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = KEY_ID + "=" + redEnvelope_master.getId();

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
    public List<redEnvelope_Master> getAll() {
        List<redEnvelope_Master> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    // 取得指定編號的資料物件
    public redEnvelope_Master get(long id) {
        // 準備回傳結果用的物件
        redEnvelope_Master redEnvelope_master = null;
        // 使用編號為查詢條件
        String redEnvelope_Masterid = KEY_ID + "=" + id;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, redEnvelope_Masterid, null, null, null, null, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            redEnvelope_master = getRecord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return redEnvelope_master;
    }

    // 取得指定編號的資料物件
    public redEnvelope_Master getNotFinishedRE(String cMemID) {
        // 準備回傳結果用的物件
        redEnvelope_Master result = null;
        FriendDAO fDAO=new FriendDAO(context);
        boolean isFriend=false;
        if(fDAO.getbyMemberID(Integer.parseInt(cMemID))!=null){
            if(fDAO.getbyMemberID(Integer.parseInt(cMemID)).getFriendType().equals("F")){
                isFriend=true;
            }
        }
        // 使用編號為查詢條件
        String query ="";
        if(isFriend){
            query=isFinish_COLUMN + "='false'" ;
        }else{
            query=isFinish_COLUMN + "='false'" + " AND "+ isFriendLimited_COLUMN +"='false'";
        }
        // 執行查詢
        Cursor cursor = db.query(
                TABLE_NAME, null, query, null, null, null, null, null);

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
    public redEnvelope_Master getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        redEnvelope_Master result = new redEnvelope_Master();
        result.setId(cursor.getInt(0));
        result.setDonateAmt(cursor.getDouble(1));
        result.setTakenAmt(cursor.getDouble(2));
        result.setCurrency(cursor.getString(3));
        result.setReceiver_no(cursor.getInt(4));
        result.setIsAverage(Boolean.parseBoolean(cursor.getString(5)));
        result.setIsAnonymous(Boolean.parseBoolean(cursor.getString(6)));
        result.setIsFinish(Boolean.parseBoolean(cursor.getString(7)));
        result.setIsIsFriendLimited(Boolean.parseBoolean(cursor.getString(8)));
        result.setSlogan(cursor.getString(9));
        result.setPicfile(cursor.getString(10));
        result.setUrllink(cursor.getString(11));
        result.setiCreditPassword(cursor.getString(12));
        result.setCreateDate(cursor.getLong(13));

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