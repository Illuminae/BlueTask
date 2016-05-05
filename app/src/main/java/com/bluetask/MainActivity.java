package com.bluetask;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bluetask.bluetooth.AcceptThread;
import com.bluetask.database.BlueTaskDataSource;
import com.bluetask.database.Position;
import com.bluetask.database.Reminder;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import static com.bluetask.R.id.ToDoList;

public class MainActivity extends AppCompatActivity implements LocationListener {
    public final static int REQUEST_ADD_REMINDER = 0;
    public final static int RESULT_SAVE = 1;
    public final static int RESULT_CANCEL = 0;
    //the database (copied from serieslist example)
    private BlueTaskDataSource mDB;
    //items that should be shown by the list view are stored here (copied from serieslist example)
    private ArrayAdapter<String> mAdapter;
    private GoogleApiClient client;
    private BlueTaskDataSource dataSource;
    protected double currentLat = 0;
    protected double currentLong = 0;
    protected LocationManager locationManager;

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
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        // start Notification Service
        Intent intent = new Intent(this, NotificationService.class);
        startService(intent);
        try{startBluetoothServer();}catch (NullPointerException e){};
        //GET YOUR CURRENT LOCATION
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "permission not granted", Toast.LENGTH_SHORT).show();
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
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
        client.connect();
        //Updates the displayed list when the Activity becomes visible
        updateList();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
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

    private void updateList() {
        dataSource = new BlueTaskDataSource(this);
        dataSource.open();
        ArrayList<Reminder> reminders = new ArrayList<Reminder>((ArrayList<Reminder>)dataSource.getAllReminders());
        Map<Reminder,Float> map = new TreeMap<>();
        for (Reminder reminder : reminders) {
            map.put(reminder,distanceInMeters(reminder));
        }
        map = MapUtil.sortByValue(map);
        reminders = null;
        for (Map.Entry<Reminder, Float> entry : map.entrySet()){
            reminders.add(entry.getKey());
        }
        // Setup cursor adapter using cursor from last step
        RemListAdapter todoAdapter = new RemListAdapter(this,reminders);
        // Find ListView to populate
        ListView remItems = (ListView) findViewById(ToDoList);
        // Attach cursor adapter to the ListView
        remItems.setAdapter(todoAdapter);
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

    @Override
    public void onLocationChanged(Location location) {
        currentLat = location.getLatitude();
        currentLong = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public float distanceInMeters(Reminder r) {
        Location loc1 = new Location("");
        loc1.setLatitude(currentLat);
        loc1.setLongitude(currentLong);
        List<Position> positions = r.getPositionsList();
        ArrayList<Float> Min_distance_list = new ArrayList<>();
        for (Position p : positions) {
            String geoloc = p.getGeo_data();
            Log.d("Position Notification", p.getGeo_data());
            geoloc = geoloc.substring(10, geoloc.length() - 1);
            String[] separated = geoloc.split(",");
            String x = separated[0];
            String y = separated[1];
            double xdouble = Double.parseDouble(x);
            double ydouble = Double.parseDouble(y);
            Location loc2 = new Location("");
            loc2.setLatitude(xdouble);
            loc2.setLongitude(ydouble);
            float distanceInMeters = loc1.distanceTo(loc2) - p.getRadius();
            Min_distance_list.add(distanceInMeters);
        }
        return Collections.min(Min_distance_list);
    }

}

