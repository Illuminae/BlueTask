package com.bluetask;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.*;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.widget.*;
import android.os.Vibrator;
import android.content.Context;

public class NotificationService extends Service {

    private ServiceHandler mServiceHandler;
    private Looper mServiceLooper;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            Context context = getApplicationContext();
            int count = 0;
            while (count <= 5) {
                    Location loc1 = new Location("");
                    double lat1 = 49.486849;
                    loc1.setLatitude(lat1);
                    double lon1 = 8.465879;
                    loc1.setLongitude(lon1);
                    Location loc2 = new Location("");
                    double lat2 = 49.487323;
                    loc2.setLatitude(lat2);
                    double lon2 = 8.464603;
                    loc2.setLongitude(lon2);
                    float distanceInMeters = loc1.distanceTo(loc2);

                    if (distanceInMeters <= 10000){
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(context)
                                        .setSmallIcon(R.drawable.notification_icon)
                                        .setContentTitle("Bluetask Alert")
                                        .setContentText("REMINDER to GET BANANAS");
// Creates an explicit intent for an Activity in your app
                        Intent resultIntent = new Intent(context, AddReminderActivity.class);

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


                    //    Toast.makeText(getApplicationContext(), "Is close to location", Toast.LENGTH_SHORT).show();
                    }

                /*    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // Restore interrupt status.
                    Thread.currentThread().interrupt();
                }*/
                count += 1;
                //Toast.makeText(getApplicationContext(), "Looping Lui", Toast.LENGTH_SHORT).show();
            }

            //}
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    public NotificationService() {
    }

    public void onCreate(){
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
/*        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("My notification")
                        .setContentText("Notification after destroy");
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, AddReminderActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
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
*/
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }
}
