package com.bluetask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
    private String posName = "";
    private String location = "";
    private String currentId=null;
    private List<LocationPair> list = new ArrayList<>();

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

        btnAddLocation.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectPreviousLocations();
                return true;
            }
        });

        receiveLocation(getIntent().getStringExtra("point"));
        currentId = getIntent().getStringExtra("id");
        if (currentId!=null) {
            Reminder currentReminder = dataSource.getReminderById(Integer.parseInt(currentId));
            TextView editDes = (TextView) findViewById(R.id.add_edittext_description);
            TextView editTitle = (TextView) findViewById(R.id.add_edittext_title);
            editTitle.setText(currentReminder.getName());
            editDes.setText(currentReminder.getDescription());
            final List<Position> positions = new ArrayList<>();
            positions.addAll(currentReminder.getPositionsList());
            for (Position currentPosition : positions){
                location = currentPosition.getGeo_data();
                posName = currentPosition.getTitle();
                addPosition();
            }
        }
    }

    private void selectPreviousLocations() {
        final ArrayAdapter<String> prevPosAdapter;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.bluetooth_list, null);
        builder.setView(convertView);
        builder.setTitle("Previous locations");
        ListView myListView = (ListView) convertView.findViewById(R.id.listView1);
        prevPosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        List<Reminder> reminders = dataSource.getAllReminders();
        final List<Position> positions = new ArrayList<>();
        for (Reminder currentReminder : reminders){
            positions.addAll(currentReminder.getPositionsList());
        }
        for (Position currentPosition : positions){
            prevPosAdapter.add(currentPosition.getTitle());
        }
        myListView.setAdapter(prevPosAdapter);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Position chosenPos = positions.get(position);
                location = chosenPos.getGeo_data();
                posName = chosenPos.getTitle();
                addPosition();
            }
        });
        builder.show();
    }
    private void addPosition(){
        TextView editText = (TextView) findViewById(R.id.add_location_description);
        String text = (String) editText.getText();
        if (text.equals("-")) {
            text = posName;
        } else {
            text = text +", "+ posName;
        }
        editText.setText(text);
        LocationPair pair = new LocationPair(posName,location);
        list.add(pair);
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
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                posName = input.getText().toString();
                addPosition();
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

