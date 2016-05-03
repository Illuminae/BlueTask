package com.bluetask.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Looper;
import android.os.Handler;
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
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        mDB = new BlueTaskDataSource(context);
    }

    public void run() {
        Reminder r;
        //Handler mHandler = new Handler(Looper.getMainLooper());

        // Keep listening to the InputStream until an exception occurs

            try {
                ObjectInputStream dis = new ObjectInputStream(mmInStream);
                // Read from the InputStream
                r = (Reminder) dis.readObject();
                // writing received Reminder to database
                mDB.createReminder(r);

                // Send the obtained bytes to the UI activity
                //mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
            } catch (IOException e) {

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

    }

    /* Call this from the main activity to send data to the remote device */
    public void write(int remId) {
        try {
            // Getting reminder object from database using its id and sending
            Reminder r = mDB.getReminderById(remId);
            ObjectOutputStream ois = new ObjectOutputStream(mmOutStream);
            ois.writeObject(r);
        } catch (IOException e) { }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
