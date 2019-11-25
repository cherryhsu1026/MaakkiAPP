package com.maakki.maakkiapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

// 資料功能類別
public class CheckDetailDAO {
    // 表格名稱
    public static final String TABLE_NAME = "CheckDetail";

    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "detail_id";

    // 其它表格欄位名稱
    public static final String CheckId_COLUMN = "check_id";
    public static final String ItemId_COLUMN = "item_id";
    public static final String ItemName_COLUMN = "item_name";
    public static final String Qty_COLUMN = "qty";
    public static final String Price_COLUMN = "price";

    // 提醒日期時間
    public static final String ALARMDATETIME_COLUMN = "alarmdatetime";

    // 使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CheckId_COLUMN + " INTEGER NOT NULL, " +
                    ItemId_COLUMN + " INTEGER NOT NULL, " +
                    ItemName_COLUMN + " Text NOT NULL, " +
                    Qty_COLUMN + " INTEGER NOT NULL, " +
                    Price_COLUMN + " REAL NOT NULL)";


    // 資料庫物件//
    private SQLiteDatabase db;

    // 建構子，一般的應用都不需要修改
    public CheckDetailDAO(Context context) {
        db = MyDBHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }

    // 新增參數指定的物件
    public CheckDetail insert(CheckDetail checkdetail) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(CheckId_COLUMN, checkdetail.getCheckId());
        cv.put(ItemId_COLUMN, checkdetail.getItemId());
        cv.put(ItemName_COLUMN, checkdetail.getItemName());
        cv.put(Qty_COLUMN, checkdetail.getQty());
        cv.put(Price_COLUMN, checkdetail.getPrice());
        // 提醒日期時間
        //cv.put(ALARMDATETIME_COLUMN, store.getAlarmDatetime());

        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);

        // 設定編號
        checkdetail.setDetailId(id);
        // 回傳結果
        return checkdetail;
    }

    // 修改參數指定的物件
    public boolean update(CheckDetail checkdetail) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(CheckId_COLUMN, checkdetail.getCheckId());
        cv.put(ItemId_COLUMN, checkdetail.getItemId());
        cv.put(ItemName_COLUMN, checkdetail.getItemName());
        cv.put(Qty_COLUMN, checkdetail.getQty());
        cv.put(Price_COLUMN, checkdetail.getPrice());
        // 提醒日期時間
        //cv.put(ALARMDATETIME_COLUMN, store.getAlarmDatetime());

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = KEY_ID + "=" + checkdetail.getDetailId();

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

    public boolean deleteByCheckId(long check_id) {
        // 設定條件為編號，格式為「欄位名稱=資料」
        String where = CheckId_COLUMN + "=" + check_id;
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME, where, null) > 0;
    }

    public boolean deleteByItemId(long check_id, long id) {
        // 設定條件為編號，格式為「欄位名稱=資料」
        String where = ItemId_COLUMN + "=" + id + " AND " + CheckId_COLUMN + " = " + check_id;
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME, where, null) > 0;
    }

    public void deleteAll() {
        db.execSQL("delete from " + TABLE_NAME);
        db.close();
    }

    // 讀取所有記事資料
    public List<CheckDetail> getAll() {
        List<CheckDetail> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    public List<CheckDetail> getCheckDetailByCheckId(long check_id) {
        List<CheckDetail> result = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + CheckId_COLUMN + " = " + check_id, null);
        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }


    public CheckDetail get(long id) {
        // 準備回傳結果用的物件
        CheckDetail checkDetail = null;
        // 使用編號為查詢條件
        String SELECTSTRING = KEY_ID + "=" + id;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, SELECTSTRING, null, null, null, null, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            checkDetail = getRecord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return checkDetail;
    }

    // 把Cursor目前的資料包裝為物件
    public CheckDetail getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        CheckDetail result = new CheckDetail();
        result.setDetailId(cursor.getLong(0));
        result.setCheckId(cursor.getLong(1));
        result.setItemId(cursor.getLong(2));
        result.setItemName(cursor.getString(3));
        result.setQty(cursor.getInt(4));
        result.setPrice(cursor.getDouble(5));

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