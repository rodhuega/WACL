package com.example.rodhuega.wacl;

import android.app.AlarmManager;
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
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;

/**
 * Created by pillo on 07/02/2018.
 */

public class RingtonePlayingService extends Service{

    MediaPlayer media_song;
    boolean isRunning;
    Notification.Builder notification;
    NotificationManager nm;

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

            notification = new Notification.Builder(getApplicationContext());
            notification.setAutoCancel(true);
            notification.setSmallIcon(R.mipmap.ic_launcher);
            notification.setTicker("WACL Notification");
            notification.setWhen(System.currentTimeMillis());
            notification.setContentTitle(getResources().getString(R.string.app_name));
            notification.setContentText(alarm.getHour()+":"+alarm.getMinute());
            //botones de notificacion
            //Accion de apagar la alarma
            Intent powerOffButton = new Intent(this,RingtonePlayingService.class);
            powerOffButton.putExtra("action",3);
            powerOffButton.putExtra("alarmID",alarmID);
            PendingIntent powerOffButtonPending = PendingIntent.getService(getApplicationContext(), alarmID, powerOffButton, PendingIntent.FLAG_ONE_SHOT);
            Notification.Action actionPowerOff =new Notification.Action(R.mipmap.ic_launcher,getResources().getString(R.string.turnOff_text),powerOffButtonPending);
            notification.addAction(actionPowerOff);
            //Accion de posponer
            Intent PostponeButton = new Intent(this,RingtonePlayingService.class);
            PostponeButton.putExtra("action",4);
            PostponeButton.putExtra("alarmID",alarmID);
            PendingIntent PostponeButtonPending = PendingIntent.getService(getApplicationContext(), alarmID, PostponeButton, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Action actionPostpone =new Notification.Action(R.mipmap.ic_launcher,getResources().getString(R.string.postpone_text),PostponeButtonPending);
            notification.addAction(actionPostpone);
            //
            Intent goToPowerOff = new Intent(getApplicationContext(), powerOffActivity.class);
            goToPowerOff.putExtra("alarmID",alarmID);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), alarmID, goToPowerOff, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setContentIntent(pendingIntent);
            nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(5163213, notification.build());
        }else if(action==2) {//Parar la alarma desde stio activity
            Log.e("Seguimiento","Entre id:" + alarmID);
            media_song.stop();
            media_song.reset();
        }else if(action==3) {//Parar alarma desde notificacion
            media_song.stop();
            media_song.reset();
            nm.cancelAll();
        }else if(action == 4) {//posponer
            media_song.stop();
            media_song.reset();
            nm.cancelAll();
            //Desactivo la alarma,
            alarm.disableAlarm(this);
            //cambiamos el valor de los minutos por el valor de posponer configurado en la alarma
            alarm.setMinute(alarm.getMinute()+alarm.getPostponeTime());
            //Activamos la alarma de nuevo
            alarm.enableAlarmSound((AlarmManager)getSystemService(ALARM_SERVICE),this.getApplicationContext());
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning=false;//puede que sobre
    }
}
