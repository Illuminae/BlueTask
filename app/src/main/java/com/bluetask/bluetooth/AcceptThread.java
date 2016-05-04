package com.bluetask.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class AcceptThread extends Thread {
    private final BluetoothServerSocket mmServerSocket;
    private UUID DEFAULT_UUID;
    private Context context;


    public AcceptThread(Context c) {
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothAdapter BTadapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothServerSocket tmp = null;
        context = c;
        DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = BTadapter.listenUsingRfcommWithServiceRecord("BlueTask", DEFAULT_UUID);
        } catch (IOException e) {
            Log.d("ListenerExc", e.toString());
        }
        mmServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket = null;

        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.d("AcceptThreadExc", e.toString());
                break;
            }
            // If a connection was accepted
            if (socket != null) {
                // Do work to manage the connection (in a separate thread)
                Thread manageConnectedSocket = new ConnectedThread(socket, context);
                manageConnectedSocket.start();
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
