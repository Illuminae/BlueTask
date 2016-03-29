package com.bluetask.database;

import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;


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

    public List<Reminder> getAllReminders(){

        // Select * FROM TABLE reminders
        Cursor cursor = mDB.rawQuery("SELECT * FROM " + BlueTaskSQLiteOpenHelper.TABLE_REMINDERS + ";", null);

        List<Reminder> allReminders = new ArrayList<>();
        boolean next = cursor.moveToFirst();
        while(next){
            int id = cursor.getColumnIndex(BlueTaskSQLiteOpenHelper.REMINDERS_COLUMN_REM_ID);
            String name = cursor.getString(cursor.getColumnIndex(BlueTaskSQLiteOpenHelper.REMINDERS_COLUMN_NAME));
            String descr = cursor.getString(cursor.getColumnIndex(BlueTaskSQLiteOpenHelper.REMINDERS_COLUMN_DESCR));
            int date = cursor.getColumnIndex(BlueTaskSQLiteOpenHelper.REMINDERS_COLUMN_DATE);
            boolean done;

            // Simplified if-statement
            done = cursor.getColumnIndex(BlueTaskSQLiteOpenHelper.REMINDERS_COLUMN_DONE) == 1;
            //TO DO: Write and Query function that gets all Positions for the Reminder and adds it to the constructor call

            List<Position> reminderPositions = getPositionsForReminder(id);
            allReminders.add(new Reminder(id, name, descr, date, done, reminderPositions));
            next = cursor.moveToNext();
        }

        return allReminders;

    }

    public List<Position> getPositionsForReminder(int rem_id){

        // SELECT * FROM TABLE TABLE positions JOIN TABLE REMINDERPOSITIONS WHERE REM_ID = X

        String reminder_id = Integer.toString(rem_id);
        Cursor cursor = mDB.rawQuery("SELECT * FROM " + BlueTaskSQLiteOpenHelper.TABLE_POSITIONS + " JOIN " + BlueTaskSQLiteOpenHelper.TABLE_REMINDERPOSITIONS
                + " WHERE " + BlueTaskSQLiteOpenHelper.REMINDERPOSITIONS_COLUMN_REM_ID + " = " + reminder_id + ";", null);

        List<Position> reminderPositions = new ArrayList<>();
        boolean next = cursor.moveToFirst();
        while(next){
            int id = cursor.getColumnIndex(BlueTaskSQLiteOpenHelper.POSITIONS_COLUMN_POS_ID);
            String title = cursor.getString(cursor.getColumnIndex(BlueTaskSQLiteOpenHelper.POSITIONS_COLUMN_POS_TITLE));
            String city = cursor.getString(cursor.getColumnIndex(BlueTaskSQLiteOpenHelper.POSITIONS_COLUMN_CITY));
            int zip = cursor.getColumnIndex(BlueTaskSQLiteOpenHelper.POSITIONS_COLUMN_ZIP);
            String street = cursor.getString(cursor.getColumnIndex(BlueTaskSQLiteOpenHelper.POSITIONS_COLUMN_STREET));
            String str_num = cursor.getString(cursor.getColumnIndex(BlueTaskSQLiteOpenHelper.POSITIONS_COLUMN_STR_NUM));
            String geo_data = cursor.getString(cursor.getColumnIndex(BlueTaskSQLiteOpenHelper.POSITIONS_COLUMN_GEO_DATA));

            reminderPositions.add(new Position(id, title, city, zip, street, str_num, geo_data));
            next = cursor.moveToNext();
        }

        return reminderPositions;
    }


}
