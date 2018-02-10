package com.example.rodhuega.wacl;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by pillo on 03/02/2018.
 */

public class Alarm implements Serializable {
    // Falta toda la parte meteoreologica

    /**
     * Entero que representa la hora
     */
    private int hour;
    /**
     * Entero que representa los minutos
     */
    private int minute;
    /**
     * Boolean que dice si la alarma esta activada
     */
    private boolean enabled;
    /**
     * boolean que indica si la alarma es de justo la siguiente hora, o si hay que mirar el array de dias
     */
    private boolean repeat;
    /**
     * Array boolean de dias que sera de longitud 7 que indica a true si ese dia tiene que despertar
     */
    private boolean[] days;
    /**
     * En caso de que se quiera poner para una fecha determinada en vez de para dias de la semana
     */
    private Date dateToSound;

    /**
     * Int que representa una identificacion de la alarma
     */
    private int id;

    /**
     * Int que representa los minutos que sera pospuesta la alarma
     */
    private int postponeTime;

    /**
     * Uri que contiene la pista que se va a reproducir en la alarma.
     */
    private String RingtoneTrack;

    //Constructor para dias o siguiente hora
    public Alarm(int id,int hour, int minute,int postponeTime, String RingtoneTrack, boolean[] days) {
        this.id = id;
        this.enabled = true;
        this.hour =hour;
        this.minute=minute;
        this.postponeTime=postponeTime;
        this.RingtoneTrack=RingtoneTrack;
        this.days=days;
        this.repeat = isRepeatEnabled();
        this.dateToSound=null;
    }
    //Constructor para fecha
    public Alarm(int id,int hour, int minute, int postponeTime, Uri RingtoneTrack, Date dateToSound) {
        this.id = id;
        this.enabled = true;
        this.hour=hour;
        this.minute= minute;
        this.postponeTime=postponeTime;
        this.postponeTime=postponeTime;
        this.dateToSound=dateToSound;
        this.repeat=false;
        //quiza haga falta hacer new y pasar el array de days a false.
    }


    public boolean isRepeatEnabled() {
        boolean resultado = false;
        for(int i =0; i< days.length && !resultado; i++) {
            if(days[i]) {
                resultado=true;
            }
        }
        return resultado;
    }

    public boolean esBisiesto(int year) {
        if(((year%4==0) && (year%100!=0)) || (year%400==0)) {
            return true;
        }
        return false;
    }

    public int queDiaPonerAlarma(int diaActual, int year) {
        if( esBisiesto(year)|| diaActual==366) { //año no bisiesto o año bisiesto y estamos en el dia 366
            return  1;
        }else if(diaActual==365 && esBisiesto(year)){ //bisiesto y estamos en el dia 365
            return 366;
        }else {//dias habituales
            return diaActual+1;
        }
    }

    public void enableAlarmSound(AlarmManager alarmManager, Context ctx) {
        if(dateToSound!=null) {//Si es una fecha

        }else if(!repeat && dateToSound==null) {//en caso de que solo se ponga para un dia(fecha mas cercana)
            Calendar calendar = Calendar.getInstance();
            //Ver si se pone para hoy o para mañana
            int horaActual=calendar.get(Calendar.HOUR_OF_DAY);
            int minutoActual=calendar.get(Calendar.MINUTE);
            if(horaActual>hour || (horaActual==hour && minutoActual>minute)) {//Se pone para el dia siguiente, si no entra en el if, se pone automaticamente para hoy.
                int yearActual= calendar.get(Calendar.YEAR);
                int diaActual= calendar.get(Calendar.DAY_OF_YEAR);
                int diaAPoner=queDiaPonerAlarma(diaActual,yearActual);
                calendar.set(Calendar.DAY_OF_YEAR,diaAPoner);
            }
            calendar.set(Calendar.HOUR_OF_DAY,hour);
            calendar.set(Calendar.MINUTE,minute);
            Intent goToEnable = new Intent(ctx, AlarmOperations.class);
            goToEnable.putExtra("action",1);
            goToEnable.putExtra("alarmID", id);
            Log.e("miID",id+"");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,(int)id,goToEnable,PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        }else {

        }
    }

    public void turnOFFAlarmSound(Context ctx) {
        Intent goToDisable = new Intent(ctx, AlarmOperations.class);
        goToDisable.putExtra("action",2);
        goToDisable.putExtra("alarmID", id);
        ctx.sendBroadcast(goToDisable);
    }

    public void cancelAlarm(AlarmManager alarmManager, Context ctx) {
        Intent goToEnable = new Intent(ctx, AlarmOperations.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,(int)id,goToEnable,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    public void saveAlarm(String path) throws IOException {
        File f = new File(path);
        FileOutputStream f1 = new FileOutputStream(f);
        ObjectOutputStream f2 = new ObjectOutputStream(f1);
        f2.writeObject(this);
        f1.close();
        f2.close();
    }

    public static Alarm loadAlarm(String path) throws IOException, ClassNotFoundException {
        File f = new File(path);
        FileInputStream f1 = new FileInputStream(f);
        ObjectInputStream f2 = new ObjectInputStream(f1);
        Alarm resultado = (Alarm) f2.readObject();
        f1.close();
        f2.close();
        return resultado;
    }

    //Gets


    public boolean[] getDays() {
        return days;
    }

    public boolean getRepeat() {
        return repeat;
    }

    public Date getDateToSound() {
        return dateToSound;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public int getId() {
        return id;
    }

    public int getPostponeTime() {
        return postponeTime;
    }

    public String getRingtoneTrack() {
        return RingtoneTrack;
    }

    //Sets


    public void setDays(boolean[] days) {
        this.days = days;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public void setDateToSound(Date dateToSound) {
        this.dateToSound = dateToSound;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPostponeTime(int postponeTime) {
        this.postponeTime = postponeTime;
    }

    public void setRingtoneTrack(String ringtoneTrack) {
        RingtoneTrack = ringtoneTrack;
    }
}
