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
    private LocationPS defaultLocation;
    /**
     * Atributo boolean que indica si por defecto se usa la alarma condicional de tiempo
     */
    private boolean conditionalWeather;

    /**
     * Atributo que tendra sus elementos a true si debe de sonar en esa condicion.
     * boolean[4], posiciones a true en caso de que deba de sonar bajo esa condicion. = despejado, 1 nublado, 2 tormenta/lloviendo/truenos, 3 nevando
     */
    private boolean[] weatherEnabledSound;

    public Settings(int postponeTime, int timeNotificacionPreAlarm) {
        this.postponeTime=postponeTime;
        this.timeNotificacionPreAlarm=timeNotificacionPreAlarm;
        //Creamos el Array que tiene Ringtones, por defecto no hay ningun Ringtone, añadimos el basico del sistema y lo ponemos como seleccionado
        ringtones = new ArrayList<>(); ringtones.add(new Ringtone("Default",android.provider.Settings.System.DEFAULT_RINGTONE_URI.toString(),0));
        this.ringtoneTrack=ringtones.get(0);
        defaultLocation=null;
        conditionalWeather=false;
        weatherEnabledSound= new boolean[4];
        for (boolean bool : weatherEnabledSound) {
            bool=true;
        }

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

    public LocationPS getDefaultLocation() {
        return defaultLocation;
    }

    public boolean[] getWeatherEnabledSound() {
        return weatherEnabledSound;
    }

    public boolean getConditionalWeather() {
        return conditionalWeather;
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

    public void setDefaultLocation(LocationPS defaultLocation) {
        this.defaultLocation = defaultLocation;
    }

    public void setWeatherEnabledSound(boolean[] weatherEnabledSound) {
        this.weatherEnabledSound = weatherEnabledSound;
    }

    public void setConditionalWeather(boolean conditionalWeather) {
        this.conditionalWeather = conditionalWeather;
        if(!conditionalWeather) {
            for (boolean bool: weatherEnabledSound) {
                bool=true;
            }
        }
    }
}
