package com.maakki.maakkiapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

// 資料功能類別
public class CategoryDAO {
    // 表格名稱
    public static final String TABLE_NAME = "Category";

    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "cat_id";

    // 其它表格欄位名稱
    public static final String CategoryName_COLUMN = "cat_name";
    public static final String Position_Column = "position";
    public static final String GroupID_Column = "group_id";
    public static final String Status_Column = "status";
    public static final String LASTMODIFY_COLUMN = "lastmodify";

    // 提醒日期時間
    public static final String ALARMDATETIME_COLUMN = "alarmdatetime";

    // 使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CategoryName_COLUMN + " TEXT NOT NULL, " +
                    Position_Column + " INTEGER NOT NULL, " +
                    GroupID_Column + " INTEGER NOT NULL, " +
                    Status_Column + " INTEGER NOT NULL, " +
                    LASTMODIFY_COLUMN + " INTEGER)";

    // 資料庫物件
    private SQLiteDatabase db;

    // 建構子，一般的應用都不需要修改
    public CategoryDAO(Context context) {
        db = MyDBHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }

    // 新增參數指定的物件
    public Category insert(Category category) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(CategoryName_COLUMN, category.getCatName());
        cv.put(Position_Column, category.getPosition());
        cv.put(GroupID_Column, category.getGroupId());
        cv.put(Status_Column, category.getStatus());
        cv.put(LASTMODIFY_COLUMN, category.getLastModify());

        // 提醒日期時間
        //cv.put(ALARMDATETIME_COLUMN, store.getAlarmDatetime());

        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);

        // 設定編號
        category.setCatId(id);
        // 回傳結果
        return category;
    }

    // 修改參數指定的物件
    public boolean update(Category category) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(CategoryName_COLUMN, category.getCatName());
        cv.put(Position_Column, category.getPosition());
        cv.put(GroupID_Column, category.getGroupId());
        cv.put(Status_Column, category.getStatus());
        cv.put(LASTMODIFY_COLUMN, category.getLastModify());

        // 提醒日期時間
        //cv.put(ALARMDATETIME_COLUMN, store.getAlarmDatetime());

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = KEY_ID + "=" + category.getCatId();

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
    public List<Category> getAll() {
        List<Category> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    public List<Category> getActivated() {
        List<Category> result = new ArrayList<>();
        //Cursor cursor = db.query(
        //        TABLE_NAME, null, null, null, null, null, null, null);
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + Position_Column + " ASC ", null);
        while (cursor.moveToNext()) {
            if (getRecord(cursor).getStatus() == 0) {
                result.add(getRecord(cursor));
            }
        }
        cursor.close();
        return result;
    }

    //已經是刪除的品項就不顯示了
    public List<Category> getDisplayedCategory() {
        List<Category> result = new ArrayList<>();
        //Cursor cursor = db.query(
        //        TABLE_NAME, null, null, null, null, null, null, null);
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + Position_Column + " ASC ", null);
        while (cursor.moveToNext()) {
            if (getRecord(cursor).getStatus() < 2) {
                result.add(getRecord(cursor));
            }
        }
        cursor.close();
        return result;
    }

    // 取得指定編號的資料物件
    public Category get(long id) {
        // 準備回傳結果用的物件
        Category category = null;
        // 使用編號為查詢條件
        String SELECTSTRING = KEY_ID + "=" + id;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, SELECTSTRING, null, null, null, null, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            category = getRecord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return category;
    }

    // 把Cursor目前的資料包裝為物件
    public Category getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        Category result = new Category();
        result.setCatId(cursor.getLong(0));
        result.setCatName(cursor.getString(1));
        result.setPosition(cursor.getInt(2));
        result.setGroupId(cursor.getInt(3));
        result.setStatus(cursor.getInt(4));
        result.setLastModify(cursor.getLong(5));

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