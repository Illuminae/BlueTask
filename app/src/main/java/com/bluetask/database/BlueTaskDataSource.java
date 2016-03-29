package com.bluetask.database;

import android.database.sqlite.SQLiteDatabase;

import android.content.Context;


/**
 * Created by Erik on 29/03/2016.
 * 
 */
public class BlueTaskDataSource {

    private SQLiteDatabase mDB;
    private BlueTaskSQLiteOpenHelper mHelper;

    public BlueTaskDataSource(Context context){
        mHelper = new BlueTaskSQLiteOpenHelper(context);
    }

    // Create and/or open the database to be used for select/insert/update

    public void open(){
        mDB = mHelper.getWritableDatabase();
    }

    public void close(){
        mHelper.close();
    }




}
