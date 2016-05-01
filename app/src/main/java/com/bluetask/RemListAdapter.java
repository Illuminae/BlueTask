package com.bluetask;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import com.bluetask.database.BlueTaskDataSource;
import com.bluetask.database.BlueTaskSQLiteOpenHelper;
import com.bluetask.database.Position;
import com.bluetask.database.Reminder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjaminchee on 23.04.16.
 */

public class RemListAdapter extends CursorAdapter {
    public RemListAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView remName = (TextView) view.findViewById(R.id.name);
        TextView remDescr = (TextView) view.findViewById(R.id.description);
        TextView remDistance = (TextView) view.findViewById(R.id.distance);

        // Extract properties from cursor
        int distance = cursor.getInt(cursor.getColumnIndex("_id"));
        String name = cursor.getString(cursor.getColumnIndex(BlueTaskSQLiteOpenHelper.REMINDERS_COLUMN_NAME));
        String description = cursor.getString(cursor.getColumnIndex(BlueTaskSQLiteOpenHelper.REMINDERS_COLUMN_DESCR));

       /* BlueTaskDataSource getPositionsForReminder();

                for(Position : List){
                distance = geo_data
                }
*/

        // Populate fields with extracted properties
        remName.setText(name);
        remDescr.setText(description);
        remDistance.setText(String.valueOf(distance));
    }


}
