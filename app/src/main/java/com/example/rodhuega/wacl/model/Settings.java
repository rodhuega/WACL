package com.example.rodhuega.wacl.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Clase que contiene la configuracion por defecto de las alarmas
 */

public class Settings implements Serializable{
    private int postponeTime, timeNotificacionPreAlarm;
    private Ringtone ringtoneTrack;
    private ArrayList<Ringtone> ringtones;

    public Settings(int postponeTime, int timeNotificacionPreAlarm) {
        this.postponeTime=postponeTime;
        this.timeNotificacionPreAlarm=timeNotificacionPreAlarm;
        //Creamos el Array que tiene Ringtones, por defecto no hay ningun Ringtone, añadimos el basico del sistema y lo ponemos como seleccionado
        ringtones = new ArrayList<>(); ringtones.add(new Ringtone("Default",android.provider.Settings.System.DEFAULT_RINGTONE_URI.toString(),0));
        this.ringtoneTrack=ringtones.get(0);
    }


    //Metodos

    public Ringtone searchRingtone(String name) {
        Ringtone resultado=null;
        for(int i =0; i<ringtones.size()&& resultado==null;i++) {
            if(ringtones.get(i).getName().equals(name)) {
                resultado=ringtones.get(i);
            }
        }
        return resultado;
    }

    public ArrayList<String> getRingtonesNames() {
        ArrayList<String> resultado = new ArrayList<>();
        for (Ringtone ringtone:ringtones) {
            resultado.add(ringtone.getName());
        }
        return resultado;
    }

    public boolean addRingtone(String name, String uri) {
        boolean resultado = true;
        for(int i = 0; i<ringtones.size() && resultado;i++) {
            if(ringtones.get(i).getUri().equals(uri) ||ringtones.get(i).getName().equals(name)) {
                resultado=false;
            }
        }
        if(resultado) {//de no estar se añade el elemento
            ringtones.add(new Ringtone(name, uri, ringtones.size()));
        }
        return resultado;
    }

    //Gets

    public int getPostponeTime() {
        return postponeTime;
    }

    public int getTimeNotificacionPreAlarm() {
        return timeNotificacionPreAlarm;
    }

    public Ringtone getRingtoneTrack() {
        return ringtoneTrack;
    }

    public ArrayList<Ringtone> getRingtones() {
        return ringtones;
    }

    //Sets


    public void setRingtoneTrack(Ringtone ringtoneTrack) {
        this.ringtoneTrack = ringtoneTrack;
    }

    public void setPostponeTime(int postponeTime) {
        this.postponeTime = postponeTime;
    }

    public void setTimeNotificacionPreAlarm(int timeNotificacionPreAlarm) {
        this.timeNotificacionPreAlarm = timeNotificacionPreAlarm;
    }

    public void setRingtones(ArrayList<Ringtone> ringtones) {
        this.ringtones = ringtones;
    }
}
