package com.maakki.maakkiapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

// 資料功能類別
public class OrderMasterDAO {
    // 表格名稱
    public static final String TABLE_NAME = "OrderMaster";

    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "order_id";

    // 其它表格欄位名稱
    public static final String IsTakeOut_COLUMN = "isTakeOut";
    public static final String TableId_COLUMN = "table_id";
    public static final String Status_COLUMN = "status";
    public static final String OrderDate_COLUMN = "order_date";

    // 提醒日期時間
    public static final String ALARMDATETIME_COLUMN = "alarmdatetime";

    // 使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    IsTakeOut_COLUMN + " INTEGER NOT NULL, " +
                    TableId_COLUMN + " INTEGER NOT NULL, " +

                    Status_COLUMN + " INTEGER NOT NULL, " +
                    OrderDate_COLUMN + " INTEGER NOT NULL)";


    // 資料庫物件//
    private SQLiteDatabase db;

    // 建構子，一般的應用都不需要修改
    public OrderMasterDAO(Context context) {
        db = MyDBHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }

    // 新增參數指定的物件
    public OrderMaster insert(OrderMaster orderMaster) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(IsTakeOut_COLUMN, orderMaster.getIsTakeOut());
        cv.put(TableId_COLUMN, orderMaster.getTableId());

        cv.put(Status_COLUMN, orderMaster.getStatus());
        cv.put(OrderDate_COLUMN, orderMaster.getOrderDate());

        // 提醒日期時間
        //cv.put(ALARMDATETIME_COLUMN, store.getAlarmDatetime());

        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);

        // 設定編號
        orderMaster.setOrderId(id);
        // 回傳結果
        return orderMaster;
    }

    // 修改參數指定的物件
    public boolean update(OrderMaster orderMaster) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(IsTakeOut_COLUMN, orderMaster.getIsTakeOut());
        cv.put(TableId_COLUMN, orderMaster.getTableId());
        cv.put(Status_COLUMN, orderMaster.getStatus());
        cv.put(OrderDate_COLUMN, orderMaster.getOrderDate());


        // 提醒日期時間
        //cv.put(ALARMDATETIME_COLUMN, store.getAlarmDatetime());

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = KEY_ID + "=" + orderMaster.getOrderId();

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

    public long getNextOrderId(long order_id) {
        // 準備回傳結果用的物件
        OrderMaster orderMaster = null;
        // 使用編號為查詢條件
        String SELECTSTRING = KEY_ID + ">" + order_id;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, SELECTSTRING, null, null, null, null, null);
        //Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ID +"=" +id, null);
        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            orderMaster = getRecord(result);
        } else {
            return 0;
        }
        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return orderMaster.getOrderId();
    }

    public long getPreviousOrderId(long order_id) {
        // 準備回傳結果用的物件
        OrderMaster orderMaster = null;
        // 使用編號為查詢條件
        String SELECTSTRING = KEY_ID + "<" + order_id;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, SELECTSTRING, null, null, null, null, null);
        //Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ID +"=" +id, null);
        // 如果有查詢結果
        if (result.moveToLast()) {
            // 讀取包裝一筆資料的物件
            orderMaster = getRecord(result);
        } else {
            return 0;
        }
        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return orderMaster.getOrderId();
    }

    public void deleteAll() {
        db.execSQL("delete from " + TABLE_NAME);
        db.close();
    }

    // 讀取所有記事資料
    public List<OrderMaster> getAll() {
        List<OrderMaster> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }


    public OrderMaster get(long id) {
        // 準備回傳結果用的物件
        OrderMaster orderMaster = null;
        // 使用編號為查詢條件
        String SELECTSTRING = KEY_ID + "=" + id;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, SELECTSTRING, null, null, null, null, null);
        //Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ID +"=" +id, null);
        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            orderMaster = getRecord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return orderMaster;
    }

    // 把Cursor目前的資料包裝為物件
    public OrderMaster getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        OrderMaster result = new OrderMaster();
        result.setOrderId(cursor.getLong(0));
        result.setIsTakeOut(cursor.getInt(1));
        result.setTableId(cursor.getInt(2));
        result.setStatus(cursor.getInt(3));
        result.setOrderDate(cursor.getLong(4));

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

    public void editTableNoByOrderId(long order_id, int isTakeOut, int table_id) {
        OrderMaster om = this.get(order_id);
        om.setIsTakeOut(isTakeOut);
        om.setTableId(table_id);
        this.update(om);
    }

}