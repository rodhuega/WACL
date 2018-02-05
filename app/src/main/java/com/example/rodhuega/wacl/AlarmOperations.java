package com.example.rodhuega.wacl;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;

/**
 * Created by pillo on 04/02/2018.
 */

public class AlarmOperations extends BroadcastReceiver{
    private MediaPlayer mediaSound;

    @Override
    public void onReceive(Context context, Intent intent) {
        int action=intent.getExtras().getInt("action");
        if(action==1) {
            mediaSound = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);
            mediaSound.start();

            Notification.Builder notification = new Notification.Builder(context);
            notification.setAutoCancel(true);
            notification.setSmallIcon(R.mipmap.ic_launcher);
            notification.setTicker("Nueva Notificacion");
            notification.setWhen(System.currentTimeMillis());
            notification.setContentTitle("Titulo");
            notification.setContentText("mi texto");
            Intent goToPowerOff = new Intent(context, powerOffActivity.class);
            //En prueba
            Alarm alarm = (Alarm) intent.getExtras().getSerializable("alarmObject");
            goToPowerOff.putExtra("alarmObject", alarm);
            //
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, goToPowerOff, 0);
            notification.setContentIntent(pendingIntent);
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(5163213, notification.build());
        }else if(action==2) {
            mediaSound.stop();
            mediaSound.reset();
        }

    }
}
