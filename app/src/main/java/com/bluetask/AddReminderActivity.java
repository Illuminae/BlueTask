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
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        dataSource.close();
    }
}
