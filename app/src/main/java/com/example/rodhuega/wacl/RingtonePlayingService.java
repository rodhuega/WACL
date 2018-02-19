package com.example.rodhuega.wacl;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.rodhuega.wacl.model.Alarm;
import com.example.rodhuega.wacl.model.AlarmsAndSettings;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by pillo on 07/02/2018.
 */

public class RingtonePlayingService extends Service{

    MediaPlayer media_song;
    Notification.Builder notification;
    NotificationManager nm;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Log.i("LocalService", "Received start id " + startId + ": " + intent);
            //Recuperar el boolean debeDeSonar
            int action = intent.getExtras().getInt("action");
            int alarmID = intent.getExtras().getInt("alarmID");
            int code =  intent.getExtras().getInt("code");
            Log.e("Seguimineto", ": action: " + action + ", alarmID: " + alarmID+ ", code: " + code);
            //En prueba
            Alarm alarm = null;
            AlarmsAndSettings alarmsAndConfs = null;
            String alarmsSavedFilePath= this.getApplicationContext().getFilesDir().getPath().toString()+AlarmsAndSettings.NOMBREDELFICHERODECONF;
            alarmsAndConfs = AlarmsAndSettings.loadAlarms(alarmsSavedFilePath);
            alarm = alarmsAndConfs.searchAlarmID(alarmID);

            //Informacion extra para debugear
            String stringCode = code+"";
            char diaCode = stringCode.charAt(stringCode.length()-1);
            int dia = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

            //que hacer dependiendo de debeDeSonar y el estado del Ringtone sadsaw
            if (action == 1 ) {//En caso de que tenga que sonar//&& alarm.getEnabled()
                //Parte que hace que suene la alarma
                media_song = MediaPlayer.create(this, Uri.parse(alarm.getRingtoneTrack()));
                media_song.start();
                //Notificacion de que esta sonando la alarma
                notification = new Notification.Builder(getApplicationContext());
                notification.setAutoCancel(true);
                notification.setOngoing(true);
                notification.setSmallIcon(R.mipmap.ic_launcher);
                notification.setTicker("WACL Notification");
                notification.setWhen(System.currentTimeMillis());
                notification.setContentTitle(getResources().getString(R.string.app_name));
                notification.setContentText(MainActivity.twoDigits(alarm.getHour()) + ":" + MainActivity.twoDigits(alarm.getMinute())+"Dia: "+ diaCode);
                //botones de notificacion
                //Accion de apagar la alarma
                Intent powerOffButton = new Intent(this, RingtonePlayingService.class);
                powerOffButton.putExtra("action", 3);
                powerOffButton.putExtra("alarmID", alarmID);
                powerOffButton.putExtra("code",code);
                PendingIntent powerOffButtonPending = PendingIntent.getService(getApplicationContext(), code, powerOffButton, PendingIntent.FLAG_ONE_SHOT);
                Notification.Action actionPowerOff = new Notification.Action(R.mipmap.ic_launcher, getResources().getString(R.string.turnOff_text), powerOffButtonPending);
                notification.addAction(actionPowerOff);
                //Accion de posponer
                Intent PostponeButton = new Intent(this, RingtonePlayingService.class);
                PostponeButton.putExtra("action", 4);
                PostponeButton.putExtra("alarmID", alarmID);
                PostponeButton.putExtra("code",code);
                PendingIntent PostponeButtonPending = PendingIntent.getService(getApplicationContext(), code, PostponeButton, PendingIntent.FLAG_UPDATE_CURRENT);
                Notification.Action actionPostpone = new Notification.Action(R.mipmap.ic_launcher, getResources().getString(R.string.postpone_text), PostponeButtonPending);
                notification.addAction(actionPostpone);
                //Si se pulsa en la notificacion va a una activity en la que se pospone o apaga la alarma
                Intent goToPowerOff = new Intent(getApplicationContext(), powerOffActivity.class);
                goToPowerOff.putExtra("alarmID", alarmID);
                goToPowerOff.putExtra("code",code);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), code, goToPowerOff, PendingIntent.FLAG_UPDATE_CURRENT);
                notification.setContentIntent(pendingIntent);
                nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                nm.notify(5163213, notification.build());
            } else if (action == 2 || action==3) {//Parar la alarma desde stio activity
                Log.e("Seguimiento", "Entre id:" + alarmID);
                media_song.stop();
                media_song.reset();
                if(!alarm.getRepeat()) {//en caso de que no sea una alarma del tipo que se repite cada semana
                    alarm.setEnabled(false);
                }else {//Poner la alarma para la semana siguiente
                    alarm.enableAlarmSound((AlarmManager) getSystemService(ALARM_SERVICE), this.getApplicationContext(),false,true);
                }
                nm.cancelAll();
            } else if (action == 4) {//posponer
                nm.cancelAll();
                //Desactivo la alarma,
                //alarm.turnOFFAlarmSound(this,code);
                media_song.stop();
                media_song.reset();
                alarm.setEnabled(true);
                //Activamos la alarma de nuevo con isAPostpone true para que cambie los valores al tiempo estipulado que queremos
                if(!alarm.getRepeat()) {
                    alarm.enableAlarmSound((AlarmManager) getSystemService(ALARM_SERVICE), this.getApplicationContext(), true, false);
                }else {//alarmas que de dias a la semana
                    Log.e("DebugDificil","Dentro de else if action 4");//Debug
                    alarm.enableAlarmSound((AlarmManager) getSystemService(ALARM_SERVICE), this.getApplicationContext(), true, true);
                }
            }else if(action== 5) {//NotificacionPreAlarma
                notification = new Notification.Builder(getApplicationContext());
                notification.setAutoCancel(true);
                notification.setSmallIcon(R.mipmap.ic_launcher);
                notification.setTicker("WACL Notification");
                notification.setWhen(System.currentTimeMillis());
                notification.setContentTitle(getResources().getString(R.string.app_name));
                notification.setContentText(getResources().getString(R.string.alarmWillSound_text)+" "+MainActivity.twoDigits(alarm.getHour()) + ":" + MainActivity.twoDigits(alarm.getMinute())+"Dia: "+ diaCode);
                //botones de notificacion
                //Accion de apagar la alarma
                Intent powerOffButton = new Intent(this, RingtonePlayingService.class);
                powerOffButton.putExtra("action", 6);
                powerOffButton.putExtra("alarmID", alarmID);
                powerOffButton.putExtra("code",code);
                PendingIntent powerOffButtonPending = PendingIntent.getService(getApplicationContext(), code, powerOffButton, PendingIntent.FLAG_ONE_SHOT);
                Notification.Action actionPowerOff = new Notification.Action(R.mipmap.ic_launcher, getResources().getString(R.string.turnOff_text), powerOffButtonPending);
                notification.addAction(actionPowerOff);
                nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                nm.notify(5163219, notification.build());
            }else if(action==6) {//Apagar alarma antes de que suene, desde notificacionPreSound.
                //si es de las que se repite todas las semanas hacer que se ponga para la semana que viene.
                if(!alarm.getRepeat()) {//en caso de que no sea una alarma del tipo que se repite cada semana
                    //cancelar la alarma
                    alarm.cancelAlarm((AlarmManager) getSystemService(ALARM_SERVICE),this.getApplicationContext(),false,-1);
                    alarm.setEnabled(false);
                }else {//Poner la alarma para la semana siguiente
                    alarm.cancelAlarm((AlarmManager) getSystemService(ALARM_SERVICE),this.getApplicationContext(),true,Alarm.diaDeLaSemanaEnElQueEstamosConMiSistema(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)));
                    alarm.enableAlarmSound((AlarmManager) getSystemService(ALARM_SERVICE), this.getApplicationContext(),false,true);
                }
                nm.cancelAll();
            }
            alarmsAndConfs.replaceAlarm(alarm.getId(),alarm);
            AlarmsAndSettings.saveAlarms(alarmsAndConfs,alarmsSavedFilePath);
        }catch (IOException |ClassNotFoundException e) {
            Log.e("OOOR", "XDDDD");
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
