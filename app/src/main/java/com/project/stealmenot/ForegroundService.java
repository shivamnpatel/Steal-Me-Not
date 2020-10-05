package com.project.stealmenot;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class ForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static final String YES_ACTION = "YES_ACTION";
    public static final String NO_ACTION = "STOP_ACTION";
    public static final String MAYBE_ACTION= "MAYBE_ACTION";
    Notification notification;

    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, ProtectionModesActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Intent yesReceive = new Intent();
        yesReceive.setAction(YES_ACTION);
        PendingIntent pendingIntentYes = PendingIntent.getBroadcast(this, 0, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);
       // mBuilder.addAction(R.mipmap.ic_launcher_round, "Yes", pendingIntentYes);

//Maybe intent
        Intent maybeReceive = new Intent();
        maybeReceive.setAction(MAYBE_ACTION);
        PendingIntent pendingIntentMaybe = PendingIntent.getBroadcast(this, 12322, maybeReceive, PendingIntent.FLAG_UPDATE_CURRENT);
       // mBuilder.addAction(R.mipmap.ic_launcher_round, "Partly", pendingIntentMaybe);

//No intent
        Intent noReceive = new Intent();
        noReceive.setAction(NO_ACTION);
        PendingIntent pendingIntentNo = PendingIntent.getBroadcast(this, 125545, noReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        //mBuilder.addAction(R.mipmap.ic_launcher_round, "No", pendingIntentNo);


        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher_round, "yes", pendingIntentYes).build();


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText("Start or stop Protection Modes")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent)
                .addAction(action)
                .addAction(R.mipmap.ic_launcher_round, "OFF", pendingIntentNo)
                .addAction(R.mipmap.ic_launcher_round, "CLOSE", pendingIntentMaybe)
                .build();
        notification.flags = Notification.FLAG_NO_CLEAR|Notification.FLAG_ONGOING_EVENT;
        startForeground(1, notification);
        //do heavy work on a background thread
        //stopSelf();
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }


}
