package com.bluetask;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import com.bluetask.bluetooth.ConnectThread;
import com.bluetask.database.BlueTaskSQLiteOpenHelper;
import java.util.Set;


public class RemListAdapter extends CursorAdapter {
    public RemListAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    public ArrayAdapter<String> BTadapter;

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView remID = (TextView) view.findViewById(R.id.rem_ID);
        TextView remName = (TextView) view.findViewById(R.id.name);
        TextView remDescr = (TextView) view.findViewById(R.id.description);
        TextView remDistance = (TextView) view.findViewById(R.id.distance);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);

        // Extract properties from cursor
        final int remId = cursor.getInt(cursor.getColumnIndex("_id"));
        int distance = cursor.getInt(cursor.getColumnIndex("_id"));
        String name = cursor.getString(cursor.getColumnIndex(BlueTaskSQLiteOpenHelper.REMINDERS_COLUMN_NAME));
        String description = cursor.getString(cursor.getColumnIndex(BlueTaskSQLiteOpenHelper.REMINDERS_COLUMN_DESCR));

       /* BlueTaskDataSource getPositionsForReminder();

                for(Position : List){
                distance = geo_data
                }
        */

        // Populate fields with extracted properties
        remID.setText(String.valueOf(remId));
        remName.setText(name);
        remDescr.setText(description);
        remDistance.setText(String.valueOf(distance));

        ImageButton btn = (ImageButton) view.findViewById(R.id.blue_Button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBluetoothDialog(context, remId);
            }
        });
/*  Deleting reminder from database once checked

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    BlueTaskDataSource mDB = new BlueTaskDataSource(context);
                    mDB.open();
                    mDB.setReminderDone(remId);
                    mDB.close();
                }
            }
        });*/
    }

    private void getBluetoothDialog(Context c, final int reminderId) {
        final Context context = c;
        final BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.bluetooth_list, null);
        builder.setView(convertView);
        builder.setTitle("Paired Devices");
        ListView myListView = (ListView) convertView.findViewById(R.id.listView1);
        BTadapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        final Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
        // put each one to the adapter
        for (BluetoothDevice device : pairedDevices){
            BTadapter.add(device.getName() + "\n" + device.getAddress());
        }
        myListView.setAdapter(BTadapter);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String chosenAdapter = BTadapter.getItem(position);
                String[] chosenAdapterLines = chosenAdapter.split("\n");
                String adapterAddress = chosenAdapterLines[1];
                Log.d("MAC ADD + Name", chosenAdapter);
                Log.d("MAC ONLY", adapterAddress);
                BluetoothDevice device = myBluetoothAdapter.getRemoteDevice(adapterAddress);
                Log.d("Devicename", device.getName());
                //TODO: Pass on to ConnectedThread
                ConnectThread t = new ConnectThread(device, context, reminderId);
                t.start();
                //BluetoothSocket mSocket = device.createInsecureRfcommSocketToServiceRecord(DEFAULT_UUID);
                //ConnectedThread sendThread = new ConnectedThread(mSocket,context);
                //sendThread.start();
                //sendThread.write(reminderId);
                }
        });
        builder.show();

    }


/*    public void myClickHandler(View v, Context context) {
        LinearLayout vwParentRow = (LinearLayout) v.getParent();
        TextView name = (TextView) vwParentRow.getChildAt(1);
        BlueTaskDataSource mDB = new BlueTaskDataSource(context);
        List<Reminder> r = new ArrayList<>();
        r = mDB.getAllReminders();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        BluetoothDevice recipient = new BluetoothDevice();
        Thread sendThread = new ConnectedThread();
    }*/
}
