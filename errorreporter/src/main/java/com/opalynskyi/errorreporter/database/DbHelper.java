package com.opalynskyi.errorreporter.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    public DbHelper(Context context) {
        super(context, DbConstants.DB_NAME, null, DbConstants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DbConstants.TABLE_NAME + " ("
                + DbConstants.COLUMN_ID + " integer primary key autoincrement,"
                + DbConstants.COLUMN_TIMESTAMP + " integer,"
                + DbConstants.COLUMN_MESSAGE + " TEXT,"
                + DbConstants.COLUMN_TRACE + " TEXT,"
                + DbConstants.COLUMN_IS_FATAL + " integer,"
                + DbConstants.COLUMN_THREAD_NAME + " TEXT,"
                + DbConstants.COLUMN_IS_NEW + " integer"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_NAME);
    }
}
