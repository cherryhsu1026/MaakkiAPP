package com.maakki.maakkiapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ryan on 2017/7/20.
 */
public class PreNotificationDBHelper extends SQLiteOpenHelper {

    // 資料庫名稱
    public static final String DATABASE_NAME = "PreNotificationdata.db";
    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    public static final int VERSION = 1;
    // 資料庫物件，固定的欄位變數
    private static SQLiteDatabase database;

    // 建構子，在一般的應用都不需要修改
    /*public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                      int version) {
        super(context, name, factory, version);
    }*/
    public PreNotificationDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                                   int version) {
        super(context, name, factory, version);
    }

    // 需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new PreNotificationDBHelper(context, DATABASE_NAME,
                    null, VERSION).getWritableDatabase();
        }

        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 建立應用程式需要的表格
        // 待會再回來完成它
        db.execSQL(PreNotificationDAO.CREATE_TABLE);
        //db.execSQL(PrevNotificationDAO2.CREATE_TABLE);
        db.execSQL(FriendDAO.CREATE_TABLE);
        db.execSQL(redEnvelopeMasterDAO.CREATE_TABLE);
        db.execSQL(redEnvelopePrivateDAO.CREATE_TABLE);
        //db.execSQL(redEnvelopeDetailDAO.CREATE_TABLE);
        //db.execSQL(redEnvelopeReceivedDAO.CREATE_TABLE);
        db.execSQL(GroupMemberDAO.CREATE_TABLE);
        db.execSQL(GroupDAO.CREATE_TABLE);
        //db.execSQL(ChatDAO.CREATE_TABLE);
        db.execSQL(AllyDAO.CREATE_TABLE);
        db.execSQL(AllyMemberDAO.CREATE_TABLE);
        db.execSQL(AllySponsorRecordDAO.CREATE_TABLE);
        db.execSQL(ActivityScoreRecordDAO.CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 刪除原有的表格
        db.execSQL("DROP TABLE IF EXISTS " + PreNotificationDAO.TABLE_NAME);
        //db.execSQL("DROP TABLE IF EXISTS " + FriendDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + redEnvelopeMasterDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + redEnvelopePrivateDAO.TABLE_NAME);
        //db.execSQL("DROP TABLE IF EXISTS " + redEnvelopeDetailDAO.TABLE_NAME);
        //db.execSQL("DROP TABLE IF EXISTS " + redEnvelopeReceivedDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GroupMemberDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GroupDAO.TABLE_NAME);
        //db.execSQL("DROP TABLE IF EXISTS " + ChatDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AllyDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AllyMemberDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AllySponsorRecordDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ActivityScoreRecordDAO.TABLE_NAME);
        // 待會再回來完成它
        // 呼叫onCreate建立新版的表格
        onCreate(db);
    }
}
