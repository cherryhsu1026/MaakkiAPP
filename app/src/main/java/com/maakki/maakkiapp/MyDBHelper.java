package com.maakki.maakkiapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {

    // 資料庫名稱
    public static final String DATABASE_NAME = "mydata.db";
    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    public static final int VERSION = 1;
    // 資料庫物件，固定的欄位變數
    private static SQLiteDatabase database;

    // 建構子，在一般的應用都不需要修改
    public MyDBHelper(Context context, String name, CursorFactory factory,
                      int version) {
        super(context, name, factory, version);
    }

    // 需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new MyDBHelper(context, DATABASE_NAME,
                    null, VERSION).getWritableDatabase();
        }

        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 建立應用程式需要的表格
        // 待會再回來完成它
        db.execSQL(StoreDAO.CREATE_TABLE);
        db.execSQL(CategoryDAO.CREATE_TABLE);
        db.execSQL(ItemDAO.CREATE_TABLE);
        db.execSQL(OrderMasterDAO.CREATE_TABLE);
        db.execSQL(OrderDetailDAO.CREATE_TABLE);
        db.execSQL(CheckMasterDAO.CREATE_TABLE);
        db.execSQL(CheckDetailDAO.CREATE_TABLE);
        db.execSQL(ChatDAO.CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 刪除原有的表格
        db.execSQL("DROP TABLE IF EXISTS " + StoreDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CategoryDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ItemDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + OrderMasterDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + OrderDetailDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CheckMasterDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CheckDetailDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ChatDAO.TABLE_NAME);

        // 待會再回來完成它

        // 呼叫onCreate建立新版的表格
        onCreate(db);
    }

}
