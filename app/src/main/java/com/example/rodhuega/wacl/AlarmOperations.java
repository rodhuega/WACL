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
import android.util.Log;

import java.io.IOException;

/**
 * Clase que se encarga de ejecutar el servicio encargado de gestionan la alarma cuando hace falta.
 */

public class AlarmOperations extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        //Recuperar datos
        int action=intent.getExtras().getInt("action");
        int alarmID = intent.getExtras().getInt("alarmID");
        int code =  intent.getExtras().getInt("code");
        Log.e("SeguimientoOP", ": action: " +action +", alarmID: "+ alarmID+", code: "+ code);
        //Crear intent y servicio,enviar esos datos e iniciar el servicio.
        Intent serviceIntent = new Intent(context,RingtonePlayingService.class);
        serviceIntent.putExtra("action",action);
        serviceIntent.putExtra("alarmID",alarmID);
        serviceIntent.putExtra("code",code);
        Log.e("DebugDificil","Fin de uso de AlarmOperations");
        context.startService(serviceIntent);

    }
}
