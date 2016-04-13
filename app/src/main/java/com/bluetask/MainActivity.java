package com.bluetask;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    public final static int REQUEST_ADD_REMINDER = 0;
    public final static int RESULT_SAVE = 1;
    public final static int RESULT_CANCEL = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addReminderIntent = new Intent(MainActivity.this, AddReminderActivity.class);
                // We require a result whether or not to update the List
                startActivityForResult(addReminderIntent, REQUEST_ADD_REMINDER);
            }
        });
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

        //Updates the displayed list when the Activity becomes visible
       // TODO write updateList()
       // updateList();
    }

    @Override
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

    @Override
    /**
     * this is called when an activity finishes which was called via
     * startActivityForResult
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //check the request code of the result
        if(requestCode == REQUEST_ADD_REMINDER)  {
            //check the result code of the result
            if(resultCode == RESULT_SAVE)  {
                //our new friend was stored to the database
                //thus we need to update our list
                //TODO: uncomment once updateList has been implemented
                //updateList();
            }
            else if(resultCode == RESULT_CANCEL)  {
                //nothing happened, we don't need to do anything
            }
        }
    }

}
