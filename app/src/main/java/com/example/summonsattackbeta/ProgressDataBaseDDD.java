package com.example.summonsattackbeta;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.NonNull;

import androidx.annotation.Nullable;

public class ProgressDataBaseDDD extends SQLiteOpenHelper {
    // Данные базы данных и таблиц
    static final String DATABASE_NAME = "progress.db";
    static final int DATABASE_VERSION = 1;
    static final String TABLE_NAME = "Sessions";

    // Название столбцов
    static final String COLUMN_ID = "_id";
    static final String COLUMN_DATE = "Date";
    static final String COLUMN_TIME = "Time";
    static final String COLUMN_SCORE = "Score";

    public ProgressDataBaseDDD(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_DATE + " TEXT, " +
            COLUMN_TIME + " INTEGER, " +
            COLUMN_SCORE + " INTEGER" +
                "); ";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
