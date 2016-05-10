package com.bluetask;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.*;
import android.os.Process;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.*;
import android.os.Vibrator;
import android.content.Context;

import com.bluetask.database.BlueTaskDataSource;
import com.bluetask.database.Position;
import com.bluetask.database.Reminder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationService extends Service implements LocationListener {

    private ServiceHandler mServiceHandler;
    private Looper mServiceLooper;
    protected LocationManager locationManager;
    protected Context context;
    protected double currentLat = 0;
    protected double currentLong = 0;

    private BlueTaskDataSource dataSource;


    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 30 seconds.
            int count = 0;
            while (count < 50) {
                Context context = getApplicationContext();
                dataSource = new BlueTaskDataSource(context);
                dataSource.open();
                List<Reminder> reminders = dataSource.getAllReminders();
                dataSource.getPositions();
                dataSource.getIntersectionTable();
                dataSource.close();
                Log.d("Service Status", "Database open, iterating reminders");
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (Reminder r : reminders) {
                    if (r.getPositionsList().isEmpty()) {
                        Log.d("Reminder w/o location", r.getName() + "has no positions");
                    } else {
                        if (distanceInMeters(r) < 0) {
                            Log.d("Distance for reminder", r.getName() + "is calculated as " + distanceInMeters(r));
                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(context)
                                            .setSmallIcon(R.drawable.notification_icon)
                                            .setContentTitle("Bluetask Alert")
                                            .setContentText(r.getName());
                            // Creates an explicit intent for an Activity in your app
                            Intent resultIntent = new Intent(context, AddReminderActivity.class);
                            resultIntent.putExtra("id", Integer.toString(r.getId()));
                            // The stack builder object will contain an artificial back stack for the
                            // started Activity.
                            // This ensures that navigating backward from the Activity leads out of
                            // your application to the Home screen.
                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                            // Adds the back stack for the Intent (but not the Intent itself)
                            stackBuilder.addParentStack(AddReminderActivity.class);
                            // Adds the Intent that starts the Activity to the top of the stack
                            stackBuilder.addNextIntent(resultIntent);
                            PendingIntent resultPendingIntent =
                                    stackBuilder.getPendingIntent(
                                            0,
                                            PendingIntent.FLAG_UPDATE_CURRENT
                                    );
                            mBuilder.setContentIntent(resultPendingIntent);
                            mBuilder.setOngoing(true);
                            Notification note = mBuilder.build();
                            note.defaults |= Notification.DEFAULT_VIBRATE;
                            note.defaults |= Notification.DEFAULT_SOUND;
                            NotificationManager mNotificationManager =
                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            // mId allows you to update the notification later on.
                            int mId = 0;
                            mNotificationManager.notify(mId, mBuilder.build());
                        }

                    }

                }
                stopSelf(msg.arg1);
            }
        }
    }


        public void onCreate() {
            HandlerThread thread = new HandlerThread("ServiceStartArguments",
                    Process.THREAD_PRIORITY_BACKGROUND);
            thread.start();

            // Get the HandlerThread's Looper and use it for our Handler
            mServiceLooper = thread.getLooper();
            mServiceHandler = new ServiceHandler(mServiceLooper);

            //GET YOUR CURRENT LOCATION
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "permission not granted", Toast.LENGTH_SHORT).show();
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
        }

        @Override
        public void onLocationChanged(Location location) {
            currentLat = location.getLatitude();
            currentLong = location.getLongitude();
            //Toast.makeText(this, Double.toString(currentLat), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("Latitude", "disable");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("Latitude", "enable");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("Latitude", "status");
        }

        public float distanceInMeters(Reminder r) {
            Location loc1 = new Location("");
            loc1.setLatitude(currentLat);
            loc1.setLongitude(currentLong);
            List<Position> positions = r.getPositionsList();
            ArrayList<Float> Min_distance_list = new ArrayList<>();
            for (Position p : positions) {
                String geoloc = p.getGeo_data();
                Log.d("Position Notification", p.getGeo_data());
                geoloc = geoloc.substring(10, geoloc.length() - 1);
                String[] separated = geoloc.split(",");
                String x = separated[0];
                String y = separated[1];
                double xdouble = Double.parseDouble(x);
                double ydouble = Double.parseDouble(y);
                Location loc2 = new Location("");
                loc2.setLatitude(xdouble);
                loc2.setLongitude(ydouble);

                float distanceInMeters = loc1.distanceTo(loc2) - p.getRadius();
                Min_distance_list.add(distanceInMeters);
            }
            return Collections.min(Min_distance_list);
        }


        public int onStartCommand(Intent intent, int flags, int startId) {
            Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
            Message msg = mServiceHandler.obtainMessage();
            msg.arg1 = startId;
            mServiceHandler.sendMessage(msg);
            Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
            // If we get killed, after returning from here, restart
            return START_STICKY;
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onDestroy() {
            Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        }
    }

