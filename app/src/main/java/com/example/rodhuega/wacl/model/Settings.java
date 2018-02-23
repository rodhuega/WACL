package com.example.rodhuega.wacl.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Clase que contiene la configuracion por defecto de las alarmas
 */

public class Settings implements Serializable{
    private int postponeTime, timeNotificacionPreAlarm;
    private String ringtoneTrack;
    private ArrayList<String> ringtones;

    public Settings(int postponeTime, int timeNotificacionPreAlarm) {
        this.postponeTime=postponeTime;
        this.timeNotificacionPreAlarm=timeNotificacionPreAlarm;
        //Creamos el Array que tiene Ringtones, por defecto no hay ningun Ringtone, añadimos el basico del sistema y lo ponemos como seleccionado
        ringtones = new ArrayList<>(); ringtones.add(android.provider.Settings.System.DEFAULT_RINGTONE_URI.toString());
        this.ringtoneTrack=ringtones.get(0);
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

    public ArrayList<String> getRingtones() {
        return ringtones;
    }

    //Sets


    public void setRingtoneTrack(String ringtoneTrack) {
        this.ringtoneTrack = ringtoneTrack;
    }

    public void setPostponeTime(int postponeTime) {
        this.postponeTime = postponeTime;
    }

    public void setTimeNotificacionPreAlarm(int timeNotificacionPreAlarm) {
        this.timeNotificacionPreAlarm = timeNotificacionPreAlarm;
    }

    public void setRingtones(ArrayList<String> ringtones) {
        this.ringtones = ringtones;
    }
}
