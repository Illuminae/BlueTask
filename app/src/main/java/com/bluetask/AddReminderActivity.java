package com.bluetask;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        super.onBackPressed();
    }

    //TODO write function that saves field values to DB
    private String saveNewReminder(){
        EditText reminderTitleEditText = (EditText) findViewById(R.id.add_edittext_title);
        String reminderTitle = reminderTitleEditText.getText().toString();

        EditText reminderDescrEditText = (EditText) findViewById(R.id.add_edittext_description);
        String reminderDescr = reminderDescrEditText.getText().toString();

        if (reminderTitle.length() > 0){
            List<Position> remPositions = new ArrayList<>();
            //For testing purposes I am creating a fake position as adding Positions is not yet
            // possible in the AddReminder view
            Position testPosition = new Position("Zu Hause", "Mannheim",68159, "Holzstr.", "9", "49.494743, 8.463979");
            remPositions.add(testPosition);
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
