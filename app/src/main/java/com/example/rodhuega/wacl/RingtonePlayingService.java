package com.example.rodhuega.wacl;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.example.rodhuega.wacl.model.Alarm;
import com.example.rodhuega.wacl.model.AlarmsAndSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by pillo on 07/02/2018.
 */

public class RingtonePlayingService extends Service{
    Object variableCondicion = new Object();
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
            if (action == 1 && weatherCondition(alarm,1)) {//La alarma va a sonar.
                //Parte que hace que suene la alarma
                media_song = MediaPlayer.create(this, Uri.parse(alarm.getRingtoneTrack().getUri()));
                media_song.start();
                //Notificacion de que esta sonando la alarma
                notification = new Notification.Builder(getApplicationContext());
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
                    notification = new Notification.Builder(getApplicationContext(),AlarmsAndSettings.NOTIFICATION_CHANNEL_ID);
                }
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
                powerOffButton.putExtra("action", 2);
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
            } else if (action == 2 ) {//Parar la alarma
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
                //Desactivo la alarma
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
            }else if(action== 5 && weatherCondition(alarm,6)) {//NotificacionPreAlarma
                notification = new Notification.Builder(getApplicationContext());
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
                    notification = new Notification.Builder(getApplicationContext(),AlarmsAndSettings.NOTIFICATION_CHANNEL_ID);
                }
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

    /**
     * Metodo que comprueba si la alarma tiene que sonar o no por las condiciones metereologicas configuradas por el usuario
     * @param alarm, Alarm a analizar
     * @param action, int accion que se esta produciendo en el servicio, por si es de tipo Sonar(1) y sea de las alarmas que suenan todas las semanas, reactivarla para la semana que viene
     * @return
     */
    public boolean weatherCondition(final Alarm alarm, int action) {
        final boolean[] resultado = new boolean[2];//posicion 1 se usa para decir si tiene que sonar la alarma o no, posicion 2 para  ver si se ha abado de esperar el json
        resultado[0]=true;resultado[1]=false;
        if(alarm.getLocation()!=null && alarm.getConditionalWeather()) {
            //Conseguir el estado metereologico de la posicion configurada
            final Context contexto = this;
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + alarm.getLocation().getLatitude() + "&lon=" + alarm.getLocation().getLongitude() + "&appid=" + AlarmsAndSettings.OPENWEATHERMAPAPIKEY;
                    Log.e("URL: ", url);
                    RequestFuture<JSONObject> future = RequestFuture.newFuture();
                    JsonObjectRequest request = new JsonObjectRequest(url, null, future, future);
                    RequestQueue queue = Volley.newRequestQueue(contexto);
                    queue.add(request);
                    try {
                        JSONObject response = future.get(10, TimeUnit.SECONDS);
                        try {
                            JSONArray weather = response.getJSONArray("weather");
                            JSONObject weatherObject = weather.getJSONObject(0);
                            int codeTime = weatherObject.getInt("id");
                            //Analizar si tiene que sonar o no la alarma
                            if (codeTime == 800 && !alarm.getWeatherEnabledSound()[0]) {//Despejado
                                resultado[0] = false;
                            } else if (codeTime > 800 && codeTime < 900 && !alarm.getWeatherEnabledSound()[1]) {//Nublado
                                resultado[0] = false;
                            } else if (codeTime >= 200 && codeTime < 600 && !alarm.getWeatherEnabledSound()[2]) {//tormenta/lloviendo/tronando
                                resultado[0] = false;
                            } else if (codeTime >= 600 && codeTime < 700 && !alarm.getWeatherEnabledSound()[3]) {//Nevando
                                resultado[0] = false;
                            }
                            Log.e("tiempo", "" + codeTime);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (InterruptedException | ExecutionException | TimeoutException e) {}
                }
            });t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        if(!resultado[0] && alarm.getRepeat() && action==1) {//en el caso de que no tenga que sonar y sea de las alarmas que suenan cada semana x dias, reactivarlo para el dia siguiente
            alarm.enableAlarmSound((AlarmManager) getSystemService(ALARM_SERVICE), this.getApplicationContext(),false,true);
        }


        return resultado[0];
    }
}


