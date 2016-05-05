package com.bluetask;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import com.bluetask.bluetooth.ConnectThread;
import com.bluetask.database.Reminder;
import java.util.List;
import java.util.Set;

public class RemListAdapter extends ArrayAdapter<Reminder> {
    public RemListAdapter(Context context, List<Reminder> reminders) {
        super(context, 0, reminders);
    }

    public ArrayAdapter<String> BTadapter;
    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    public View getView(int position, View view, ViewGroup parent) {
        // Find fields to populate in inflated template
        TextView remID = (TextView) view.findViewById(R.id.rem_ID);
        TextView remName = (TextView) view.findViewById(R.id.name);
        TextView remDescr = (TextView) view.findViewById(R.id.description);
        TextView remDistance = (TextView) view.findViewById(R.id.distance);

        if (view == null) {
            LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        // Get the data item for this position
        final Reminder reminder = getItem(position);

        // Populate fields with extracted properties
        remID.setText(reminder.getId());
        remName.setText(reminder.getName());
        remDescr.setText(reminder.getDescription());

        ImageButton btn = (ImageButton) view.findViewById(R.id.blue_Button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBluetoothDialog(getContext(), reminder.getId());
            }

        });

        return view;
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
            }
        });
        builder.show();

    }
}
