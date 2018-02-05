package com.example.rodhuega.wacl;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

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
    private long id;

    //Constructor para dias o siguiente hora
    public Alarm(long id,int hour, int minute, boolean[] days) {
        this.id = id;
        this.enabled = true;
        this.hour =hour;
        this.minute=minute;
        this.days=days;
        this.repeat = isRepeatEnabled();
        this.dateToSound=null;
    }
    //Constructor para fecha
    public Alarm(long id,int hour, int minute, Date dateToSound) {
        this.id = id;
        this.enabled = true;
        this.hour=hour;
        this.minute= minute;
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


    public void enableAlarmSound(AlarmManager am, Context ctx) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        AlarmManager alarmManager = am;
        Intent goToEnable = new Intent(ctx, AlarmOperations.class);
        goToEnable.putExtra("action",1);
        goToEnable.putExtra("alarmObject", this);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,0,goToEnable,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    }

    public void disableAlarm(Context ctx) {
        Intent goToDisable = new Intent(ctx, AlarmOperations.class);
        goToDisable.putExtra("action",1);
        goToDisable.putExtra("alarmObject", this);
        ctx.startActivity(goToDisable);
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

    public long getId() {
        return id;
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

    public void setId(long id) {
        this.id = id;
    }
}
