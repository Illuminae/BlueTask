package com.bluetask.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Erik on 29/03/2016.
 *
 * The SQLiteOpenHelper class manages database creation and version management
 */
public class BlueTaskSQLiteOpenHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "bluetask.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_REMINDERS = "reminders";
    public static final String TABLE_REMINDERPOSITIONS = "rem_pos";
    public static final String TABLE_POSITIONS = "positions";
    public static final String REMINDERS_COLUMN_REM_ID = "rem_id";
    public static final String REMINDERS_COLUMN_DATE = "date";
    public static final String REMINDERS_COLUMN_NAME = "name";
    public static final String REMINDERS_COLUMN_DESCR = "description";
    public static final String REMINDERS_COLUMN_DONE = "done";
    public static final String REMINDERPOSITIONS_COLUMN_REM_ID = "rem_id";
    public static final String REMINDERPOSITIONS_COLUMN_POS_ID = "pos_id";
    public static final String POSITIONS_COLUMN_POS_ID = "pos_id";
    public static final String POSITIONS_COLUMN_POS_TITLE = "pos_title";
    public static final String POSITIONS_COLUMN_STREET = "street";
    public static final String POSITIONS_COLUMN_STR_NUM = "str_num";
    public static final String POSITIONS_COLUMN_ZIP = "zip";
    public static final String POSITIONS_COLUMN_CITY = "city";
    public static final String POSITIONS_COLUMN_GEO_DATA = "geo_data";

    private static final String DATABASE_CREATE =
            "CREATE TABLE " + TABLE_REMINDERS + "("
                + REMINDERS_COLUMN_REM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + REMINDERS_COLUMN_NAME + " TEXT NOT NULL, "
                + REMINDERS_COLUMN_DESCR + " TEXT NOT NULL, "
                + REMINDERS_COLUMN_DATE + " INTEGER NOT NULL, "
                + REMINDERS_COLUMN_DONE + " INTEGER NOT NULL"
                + "); "
                +
            "CREATE TABLE " + TABLE_POSITIONS + "("
                + POSITIONS_COLUMN_POS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + POSITIONS_COLUMN_POS_TITLE + " TEXT NOT NULL, "
                + POSITIONS_COLUMN_CITY + " TEXT, "
                + POSITIONS_COLUMN_ZIP + "INTEGER, "
                + POSITIONS_COLUMN_STREET + " TEXT, "
                + POSITIONS_COLUMN_STR_NUM + " TEXT, "
                + POSITIONS_COLUMN_GEO_DATA + " TEXT NOT NULL"
                + "); "
                +
            "CREATE TABLE " + TABLE_REMINDERPOSITIONS + "("
                + REMINDERPOSITIONS_COLUMN_REM_ID + "INTEGER, "
                + REMINDERPOSITIONS_COLUMN_POS_ID + "INTEGER, "
                + "FOREIGN KEY (" + REMINDERPOSITIONS_COLUMN_REM_ID + ") REFERENCES " + TABLE_REMINDERS + "(" + REMINDERS_COLUMN_REM_ID + "), "
                + "FOREIGN KEY (" + REMINDERPOSITIONS_COLUMN_POS_ID + ") REFERENCES " + TABLE_POSITIONS + "(" + POSITIONS_COLUMN_POS_ID + "), "
                + "PRIMARY KEY (" + REMINDERPOSITIONS_COLUMN_REM_ID + ", " + REMINDERPOSITIONS_COLUMN_POS_ID + ")"
                + ");";

    public BlueTaskSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database){
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERPOSITIONS + ", " + TABLE_POSITIONS + ", " + TABLE_REMINDERS);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db){
        db.setForeignKeyConstraintsEnabled(true);
    }
}
