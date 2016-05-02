package com.bluetask;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bluetask.bluetooth.AcceptThread;
import com.bluetask.database.BlueTaskDataSource;
import com.bluetask.database.BlueTaskSQLiteOpenHelper;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import static com.bluetask.R.id.ToDoList;

public class MainActivity extends AppCompatActivity {

    public final static int REQUEST_ADD_REMINDER = 0;
    public final static int RESULT_SAVE = 1;
    public final static int RESULT_CANCEL = 0;


    //the database (copied from serieslist example)
    private BlueTaskDataSource mDB;
    //items that should be shown by the list view are stored here (copied from serieslist example)
    private ArrayAdapter<String> mAdapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

     /*   //initializes and opens the database (copied from serieslist example)
        mDB = new BlueTaskDataSource(this);
        mDB.open();*/

  /*      //registers the list view for the context menu (copied from serieslist example)
        ListView listView = (ListView) findViewById(ToDoList);
        List remListAdapter = new List(getApplicationContext(),R.layout.list_item);
        listView.setAdapter(RemListAdapter);
        registerForContextMenu(listView);*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addReminderIntent = new Intent(MainActivity.this, AddReminderActivity.class);
                // We require a result whether or not to update the List
                startActivityForResult(addReminderIntent, REQUEST_ADD_REMINDER);
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        startBluetoothServer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();

        //Updates the displayed list when the Activity becomes visible
        updateList();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.bluetask/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //close the database when the activity is destroyed
        if (mDB != null) {
            mDB.close();
        }
    }


    @Override
    /**
     * this is executed when an item of the options menu is selected
    */
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_switch_maps) {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


   /* @Override
    /**
     * this is executed when an item of a context menu is selected

    public boolean onContextItemSelected(MenuItem item) {
        //get information on the related list item
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //check which context menu item was selected
        switch(item.getItemId())  {
            case R.id.action_delete:
                Reminder reminder = mAdapter.getItem(info.position);
                mDB.deleteReminder(reminder.id);
                updateList();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    */


    /**
     * delete all reminders from the database and update the adapter

    private void clearList() {
        mDB.clearReminders();
        updateList();
    }
*/
    /**TODO: Does this implement updateList?
     * Updates the list adapter and, thus, the UI element
     */
    private void updateList() {

        // BlueTaskSQLiteOpenHelper is a SQLiteOpenHelper class connecting to SQLite
        BlueTaskSQLiteOpenHelper handler = new BlueTaskSQLiteOpenHelper(this);
        // Get access to the underlying writeable database
        SQLiteDatabase mDB = handler.getWritableDatabase();
        // Query for items from the database and get a cursor back
        Cursor todoCursor = mDB.rawQuery("SELECT " + BlueTaskSQLiteOpenHelper.REMINDERS_COLUMN_REM_ID + " AS _id," +
                BlueTaskSQLiteOpenHelper.REMINDERS_COLUMN_NAME + ", " + BlueTaskSQLiteOpenHelper.REMINDERS_COLUMN_DESCR +
                " FROM " + BlueTaskSQLiteOpenHelper.TABLE_REMINDERS + ";", null);


        // Find ListView to populate
        ListView remItems = (ListView) findViewById(ToDoList);
        // Setup cursor adapter using cursor from last step
        RemListAdapter todoAdapter = new RemListAdapter(this, todoCursor);
        // Attach cursor adapter to the ListView
        remItems.setAdapter(todoAdapter);

        // Switch to new cursor and update contents of ListView
        //todoAdapter.changeCursor(newCursor);


        /*//Get all reminders from the database
        List<Reminder> allReminders = mDB.getAllReminders();

        List<String> allNames = new ArrayList<>();

        for (Reminder r: allReminders) {
            allNames.add(r.getName());
        }

        //update the adapter
        mAdapter = new ArrayAdapter<>(this, R.layout.list_item, allNames);
        ListView lv = (ListView) findViewById(ToDoList);
        lv.setAdapter(mAdapter);*/
    }


    @Override
    /**
     * this is called when an activity finishes which was called via
     * startActivityForResult
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //check the request code of the result
        if (requestCode == REQUEST_ADD_REMINDER) {
            //check the result code of the result
            if (resultCode == RESULT_SAVE) {
                //our new reminder was stored to the database
                //thus we need to update our list
                //TODO: uncomment once updateList has been implemented
                updateList();
            } else if (resultCode == RESULT_CANCEL) {
                //nothing happened, we don't need to do anything
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.bluetask/http/host/path")
           );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }



    private void startBluetoothServer(){
        Context c = getApplicationContext();
        Thread btThread = new AcceptThread(c);
        btThread.start();
    }


    }

