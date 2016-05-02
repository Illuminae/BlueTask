package com.bluetask;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import com.bluetask.bluetooth.ConnectThread;
import com.bluetask.bluetooth.ConnectedThread;
import com.bluetask.database.BlueTaskSQLiteOpenHelper;
import java.util.Set;
import java.util.UUID;


public class RemListAdapter extends CursorAdapter {
    public RemListAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    public ImageButton BluetoothBn;
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
        TextView remName = (TextView) view.findViewById(R.id.name);
        TextView remDescr = (TextView) view.findViewById(R.id.description);
        //TextView remDistance = (TextView) view.findViewById(R.id.distance);

        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndex(BlueTaskSQLiteOpenHelper.REMINDERS_COLUMN_NAME));
        String description = cursor.getString(cursor.getColumnIndex(BlueTaskSQLiteOpenHelper.REMINDERS_COLUMN_DESCR));
        final int remId = cursor.getInt(cursor.getColumnIndex("_id"));
        //int remDistance = cursor.getInt(cursor.getColumnIndexOrThrow());

        // Populate fields with extracted properties
        remName.setText(name);
        remDescr.setText(description);
        //remDistance.setText(String.valueOf(distance));

        ImageButton btn = (ImageButton) view.findViewById(R.id.blue_Button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBluetoothDialog(context, remId);
            }

        });
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
                Log.d("MAC ADD", chosenAdapter);
                BluetoothDevice device = myBluetoothAdapter.getRemoteDevice(chosenAdapter);
                //TODO: Pass on to ConnectedThread
                UUID DEFAULT_UUID = UUID.randomUUID();

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






/**
 * Created by benjaminchee on 23.04.16.
 */
/*public class RemListAdapter extends ArrayAdapter {
    private List remList = new ArrayList();

    public RemListAdapter(Context context, int resource) {
        super(context, resource);
        //TODO Auto-generated constructor stub
    }


    public void add(com.bluetask.database.Reminder object) {
        remList.add(object);
        super.add(object);
    }
    static class remHolder
    {
        TextView NAME;
        TextView DESCR;
        TextView DISTANCE;
    }
    @Override
    public int getCount() {
        //TODO Auto-generated method stub
        return this.remList.size();
    }
    @Override
    public Object getItem(int position) {
        //TODO Auto-generated method stub
        return this.remList.get(position);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        //TODO Create the cell (View) and populate it with an
        // element of the array
        View row;
        row = convertView;
        remHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_item, parent, false);
            holder = new remHolder();
            holder.NAME = (TextView) row.findViewById(R.id.name);
            holder.DESCR = (TextView) row.findViewById(R.id.description);
            holder.DISTANCE = (TextView) row.findViewById(R.id.distance);
            row.setTag(holder);
        } else {
            holder = (remHolder) row.getTag();

        }
        Reminder REM = getItem(position);
        holder.NAME.setText(REM.getName());
        holder.DESCR.setText(REM.getDescription());
        holder.DISTANCE.setText(REM.getDistance());


        return row;
    }
}


    String[] reminders;
    Context ctxt;
    LayoutInflater ReminderInflator;

    public RemListAdapter(String[] arr,Context c) {
        reminders = arr;
        ctxt = c;
        ReminderInflator = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
    }

    @Override
    public int getCount() {
        //TODO Auto-generated method stub
        return reminders.length;
    }

    @Override
    public Object getItem(int arg0) {
        //TODO Auto-generated method stub
        return reminders[arg0];
    }

    @Override
    public long getItemId(int arg0) {
        //TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        //TODO Create the cell (View) and populate it with an
        // element of the array
        if(arg1==null)
            arg1 = ReminderInflator.inflate(R.layout.list_item,arg2,false)
        TextView reminder = (TextView)arg1.findViewById(R.layout.list_item.title)
        reminder.setText(reminders[arg0]);

        return arg1;

    }*/



