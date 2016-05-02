package com.bluetask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
    private String m_Text = "";
    private String location = "";
    private List<LocationPair> list = new ArrayList<LocationPair>();

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
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("activity","map");
                startActivityForResult(intent,100);
            }
        });
        receiveLocation(getIntent().getStringExtra("point"));
    }

    private void receiveLocation(String location){
        this.location=location;
        if (location != null) {
            getLocDescription();
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data) {
        if(resultCode == 100){
            receiveLocation((String)data.getExtras().get("point"));
        }
    }

    private void getLocDescription(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Give location name");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                TextView editText = (TextView) findViewById(R.id.add_location_description);
                String text = (String) editText.getText();
                if (text.equals("-")) {
                    text = m_Text;
                } else {
                    text = text +", "+ m_Text;
                }
                editText.setText(text);
                LocationPair pair = new LocationPair(m_Text,location);
                list.add(pair);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
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
            if (list.size() != 0) {
                for (LocationPair pair : list) {
                    Position newPosition = new Position(pair.getLocDesc(), radius, pair.getLocCoords());
                    remPositions.add(newPosition);
                }

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

