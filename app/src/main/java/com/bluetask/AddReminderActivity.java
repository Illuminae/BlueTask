package com.bluetask;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bluetask.database.BlueTaskDataSource;
import com.bluetask.database.Position;
import com.bluetask.database.Reminder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Erik on 08/04/2016.
 *
 */
public class AddReminderActivity extends AppCompatActivity{

    private BlueTaskDataSource dataSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        Toolbar toolbar = (Toolbar) findViewById(R.id.add_toolbar);
        toolbar.setTitle("Add new reminder");
        setSupportActionBar(toolbar);

        dataSource = new BlueTaskDataSource(this);
        dataSource.open();

        Button btnSave = (Button) findViewById(R.id.add_btn_save);
        Button btnCancel = (Button) findViewById(R.id.add_btn_cancel);
        Button btnAddLocation = (Button) findViewById(R.id.add_btn_addLocation);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = saveNewReminder();
                if(name != null) {
                    //createNotification(name);
                    finishWithResult(MainActivity.RESULT_SAVE);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithResult(MainActivity.RESULT_CANCEL);
            }
        });

        btnAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Add location!", Toast.LENGTH_SHORT).show();
            }
        });

        //populate location when started from MapsActivity
        String text = (String) getIntent().getStringExtra("point");
        TextView editText = (TextView) findViewById(R.id.add_location_description);
        editText.setText(text);

    }

    private void finishWithResult(int resultCode)  {
        setResult(resultCode);
        super.onBackPressed();
    }

    //TODO write function that saves field values to DB
    private String saveNewReminder(){
        EditText reminderTitleEditText = (EditText) findViewById(R.id.add_edittext_title);
        String reminderTitle = reminderTitleEditText.getText().toString();

        EditText reminderDescrEditText = (EditText) findViewById(R.id.add_edittext_description);
        String reminderDescr = reminderDescrEditText.getText().toString();

        TextView reminderLocationEditText = (TextView) findViewById((R.id.add_location_description));
        String locationCoordinates = reminderLocationEditText.getText().toString();

        EditText reminderRadiusEditText = (EditText) findViewById((R.id.add_edittext_radius));
        String radiusDescr = reminderRadiusEditText.getText().toString();

        if (reminderTitle.length() > 0){
            List<Position> remPositions = new ArrayList<>();
            // Get Radius from Edittext field and convert to int
            int radius;
            if (radiusDescr.length() == 0) {
                //Default radius 400m if no value was given
                radius = 400;
            } else {
                radius = Integer.parseInt(radiusDescr);
            }
            //TODO: Check if multiple locations are given!
            //For now check is done if Position empty, otherwise Position object is instantiated.
            if (locationCoordinates.length() == 0) {
                Position noPosition = null;
                remPositions.add(noPosition);
            } else {
                Position newPosition = new Position(reminderTitle, radius, locationCoordinates);
                remPositions.add(newPosition);
            }

            int time = (int) System.currentTimeMillis() % Integer.MAX_VALUE;
            Reminder newReminder = new Reminder(reminderTitle, reminderDescr, time, false, remPositions);
            dataSource.createReminder(newReminder);

            return reminderTitle;
        }

        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        dataSource.close();
    }
}
