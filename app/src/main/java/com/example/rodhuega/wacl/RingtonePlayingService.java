package com.example.rodhuega.wacl;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

/**
 * Created by pillo on 07/02/2018.
 */

public class RingtonePlayingService extends Service{

    MediaPlayer media_song;
    boolean isRunning;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        //Recuperar el boolean debeDeSonar
        int action=intent.getExtras().getInt("action");
        int alarmID = intent.getExtras().getInt("alarmID");
        Log.e("Seguimineto", ": action: " +action +", alarmID: "+ alarmID);
        //En prueba
        Alarm alarm=null;
        try {
            alarm =AlarmsAndSettings.loadAlarms(getApplicationContext().getFilesDir().getPath().toString()+AlarmsAndSettings.NOMBREDELFICHERODECONF).searchAlarmID(alarmID);
        }catch (IOException |ClassNotFoundException e) {
            Log.e("OOOR", "XDDDD");
        }
        //

        //que hacer dependiendo de debeDeSonar y el estado del Ringtone
        if(action==1) {//En caso de que tenga que sonar
            //
            media_song=MediaPlayer.create(this,Settings.System.DEFAULT_RINGTONE_URI);
            media_song.start();

            Notification.Builder notification = new Notification.Builder(getApplicationContext());
            notification.setAutoCancel(true);
            notification.setSmallIcon(R.mipmap.ic_launcher);
            notification.setTicker("WACL Notification");
            notification.setWhen(System.currentTimeMillis());
            notification.setContentTitle(getResources().getString(R.string.app_name));
            notification.setContentText(alarm.getHour()+":"+alarm.getMinute());
            Intent goToPowerOff = new Intent(getApplicationContext(), powerOffActivity.class);
            goToPowerOff.putExtra("alarmID",alarmID);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, goToPowerOff, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setContentIntent(pendingIntent);
            NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(5163213, notification.build());
        }else if(action==2) {//Parar la alarma
            media_song.stop();
            media_song.reset();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning=false;//puede que sobre
    }
}
