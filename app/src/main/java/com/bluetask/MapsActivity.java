package com.bluetask;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bluetask.database.BlueTaskDataSource;
import com.bluetask.database.Position;
import com.bluetask.database.Reminder;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    private GoogleMap mMap;
    private String pActivity="";
    private BlueTaskDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        pActivity = intent.getStringExtra("activity");
        dataSource = new BlueTaskDataSource(this);
        dataSource.open();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_switch_main) {
            Intent intent = new Intent(MapsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(getApplicationContext(), "ACCESS_FINE_LOCATION not granted!", Toast.LENGTH_SHORT).show();
        }

        //add markers for each position
        List<Reminder> reminders = dataSource.getAllReminders();
        double latitude, longitude;
        final List<Position> positions = new ArrayList<>();
        for (Reminder currentReminder : reminders){
            positions.addAll(currentReminder.getPositionsList());
        }
        for (Position currentPosition : positions){
            String geoloc = currentPosition.getGeo_data();
            geoloc = geoloc.substring(10, geoloc.length() - 1);
            String[] separated = geoloc.split(",");
            latitude = Double.parseDouble(separated[0]);
            longitude = Double.parseDouble(separated[1]);
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(currentPosition.getTitle()));
        }



        // zoom in on map
        CameraPosition camPos = new CameraPosition.Builder()
                    .target(new LatLng(49.487765, 8.466285))
                    .zoom(14)
                    .tilt(0)
                    .build();
        CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
        mMap.moveCamera(camUpd3);

    }


    @Override
    public void onMapLongClick(LatLng point) {
        String text = point.toString();
        Intent intent = new Intent(MapsActivity.this, AddReminderActivity.class);
        intent.putExtra("point", text);
        if (Objects.equals(pActivity, new String("map"))){
            setResult(100, intent);
            super.onBackPressed();
        } else {
            startActivity(intent);
        }
        finish();

    }

}
