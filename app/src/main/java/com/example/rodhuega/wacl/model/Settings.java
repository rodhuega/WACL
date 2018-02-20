package com.example.rodhuega.wacl.model;

import java.io.Serializable;

/**
 * Clase que contiene la configuracion por defecto de las alarmas
 */

public class Settings implements Serializable{
    private int postponeTime, timeNotificacionPreAlarm;
    private String ringtoneTrack;

    public Settings(int postponeTime, int timeNotificacionPreAlarm, String ringtoneTrack) {
        this.postponeTime=postponeTime;
        this.timeNotificacionPreAlarm=timeNotificacionPreAlarm;
        this.ringtoneTrack=ringtoneTrack;
    }

    //gets

    public int getPostponeTime() {
        return postponeTime;
    }

    public int getTimeNotificacionPreAlarm() {
        return timeNotificacionPreAlarm;
    }

    public String getRingtoneTrack() {
        return ringtoneTrack;
    }

    //Sets


    public void setRingtoneTrack(String ringtoneTrack) {
        ringtoneTrack = ringtoneTrack;
    }

    public void setPostponeTime(int postponeTime) {
        this.postponeTime = postponeTime;
    }

    public void setTimeNotificacionPreAlarm(int timeNotificacionPreAlarm) {
        this.timeNotificacionPreAlarm = timeNotificacionPreAlarm;
    }
}
