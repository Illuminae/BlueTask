package com.bluetask.database;

import android.content.ContentValues;
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


    public void createReminder(Reminder reminder){

        ContentValues valuesRem = new ContentValues();
        ContentValues valuesPos = new ContentValues();
        ContentValues valuesRemPos = new ContentValues();
        //Converting boolean to int
        int isDone = reminder.isDone() ? 1 : 0;

        //Adding all Reminder info to the reminder table
        valuesRem.put(BlueTaskSQLiteOpenHelper.REMINDERS_COLUMN_NAME, reminder.getName());
        valuesRem.put(BlueTaskSQLiteOpenHelper.REMINDERS_COLUMN_DESCR, reminder.getDescription());
        valuesRem.put(BlueTaskSQLiteOpenHelper.REMINDERS_COLUMN_DATE, reminder.getDate());
        valuesRem.put(BlueTaskSQLiteOpenHelper.REMINDERS_COLUMN_DONE, isDone);
        int insertIDRem = (int) mDB.insert(BlueTaskSQLiteOpenHelper.TABLE_REMINDERS, null, valuesRem);

        //Getting all positions specified in the reminder and writing them to the position table
        List<Integer> insertIDPos = new ArrayList<>();
        for(Position pos : reminder.getPositionsList()){
            valuesPos.put(BlueTaskSQLiteOpenHelper.POSITIONS_COLUMN_POS_TITLE, pos.getTitle());
            valuesPos.put(BlueTaskSQLiteOpenHelper.POSITIONS_COLUMN_CITY, pos.getCity());
            valuesPos.put(BlueTaskSQLiteOpenHelper.POSITIONS_COLUMN_ZIP, pos.getZip());
            valuesPos.put(BlueTaskSQLiteOpenHelper.POSITIONS_COLUMN_STREET, pos.getStreet());
            valuesPos.put(BlueTaskSQLiteOpenHelper.POSITIONS_COLUMN_STR_NUM, pos.getStr_num());
            valuesPos.put(BlueTaskSQLiteOpenHelper.POSITIONS_COLUMN_GEO_DATA,pos.getGeo_data());
            insertIDPos.add((int) mDB.insert(BlueTaskSQLiteOpenHelper.TABLE_POSITIONS, null, valuesPos));
        }

        //Finally establishing mapping between the tables by entering the row IDs to the intersection table reminderPositions
        for(int i : insertIDPos){
            valuesRemPos.put(BlueTaskSQLiteOpenHelper.REMINDERPOSITIONS_COLUMN_POS_ID, i);
            valuesRemPos.put(BlueTaskSQLiteOpenHelper.REMINDERPOSITIONS_COLUMN_REM_ID, insertIDRem);
            int insertIDRemPos = (int) mDB.insert(BlueTaskSQLiteOpenHelper.TABLE_REMINDERPOSITIONS, null, valuesRemPos);
        }
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
