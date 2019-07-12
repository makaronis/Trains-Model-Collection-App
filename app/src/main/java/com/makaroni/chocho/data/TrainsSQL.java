package com.makaroni.chocho.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TrainsSQL extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;
    public TrainsSQL(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TRAINS_TABLE =  "CREATE TABLE " + TrainsContract.TRAINS_TABLE_NAME + " ("
                + TrainsContract.TrainsEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + TrainsContract.TrainsEntry.COLUMN_ARTICLE + " INTEGER , "
                + TrainsContract.TrainsEntry.COLUMN_MANUFACTURER + " TEXT, "
                + TrainsContract.TrainsEntry.COLUMN_MODEL + " TEXT, "
                + TrainsContract.TrainsEntry.COLUMN_COMPANY + " TEXT, "
                + TrainsContract.TrainsEntry.COLUMN_NOTE + " TEXT, "
                + TrainsContract.TrainsEntry.COLUMN_TYPE + " TEXT NOT NULL, "
                + TrainsContract.TrainsEntry.COLUMN_SUBTYPE + " TEXT NOT NULL ) ;";
        // Execute the SQL statement
        String SQL_IMAGE_TABLE_CREATE =  "CREATE TABLE " + TrainsContract.IMAGE_TABLE_NAME + " ("
                + TrainsContract.TrainsEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT REFERENCES trains(_id) ON DELETE CASCADE , "
                + TrainsContract.TrainsEntry.COLUMN_IMAGE + " BLOB);";


        db.execSQL(SQL_CREATE_TRAINS_TABLE);
        db.execSQL(SQL_IMAGE_TABLE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
