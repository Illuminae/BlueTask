package com.bluetask;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add new reminder");
        setSupportActionBar(toolbar);

        dataSource = new BlueTaskDataSource(this);
        dataSource.open();

        Button btnSave = (Button) findViewById(R.id.add_btn_save);
        Button btnCancel = (Button) findViewById(R.id.add_button_cancel);

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
    }

    private void finishWithResult(int resultCode)  {
        setResult(resultCode);
        finish();
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
