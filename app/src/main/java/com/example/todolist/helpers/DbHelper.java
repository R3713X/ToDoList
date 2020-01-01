package com.example.todolist.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.todolist.models.TodoListContract.SQL_CREATE_ENTRIES_LIST;
import static com.example.todolist.models.TodoListContract.SQL_CREATE_ENTRIES_TASK;
import static com.example.todolist.models.TodoListContract.SQL_DELETE_ENTRIES_LIST;
import static com.example.todolist.models.TodoListContract.SQL_DELETE_ENTRIES_TASK;


public class DbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "TodoTask1.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_TASK);
        db.execSQL(SQL_CREATE_ENTRIES_LIST);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES_LIST);
        db.execSQL(SQL_DELETE_ENTRIES_TASK);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
