package com.bluetask.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.bluetask.database.BlueTaskDataSource;
import com.bluetask.database.Reminder;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final BlueTaskDataSource mDB;


    public ConnectedThread(BluetoothSocket socket, Context context) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e("getStreamExc", e.toString());
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        mDB = new BlueTaskDataSource(context);
    }

    public void run() {
        Reminder r;

        // Keep listening to the InputStream until an exception occurs
        //while(true) {
        try {
            Log.d("Wait for input", "...");
            ObjectInputStream dis = new ObjectInputStream(mmInStream);
            // Read from the InputStream
            r = (Reminder) dis.readObject();
            Log.d("OBJECT", r.toString());
            // writing received Reminder to database
            mDB.createReminder(r);
            // Send the obtained bytes to the UI activity
            //mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
            dis.close();
            mmInStream.close();
            mmOutStream.close();
        } catch (IOException e) {
            Log.d("OutputStreamExc", e.toString());

        } catch (ClassNotFoundException e) {
            Log.e("ClassNotFoundExc", e.toString());
        } finally  {
            try {
                mmSocket.close();
                //break;
            } catch (IOException e) {
                Log.e("SocketCloseExc", e.toString());
            }
        }
        //}
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(int remId) {
        try {
            // Getting reminder object from database using its id and sending
            Reminder r = mDB.getReminderById(remId);
            ObjectOutputStream ois = new ObjectOutputStream(mmOutStream);
            ois.writeObject(r);
        } catch (IOException e) {
            Log.e("WriteExc", e.toString());
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
