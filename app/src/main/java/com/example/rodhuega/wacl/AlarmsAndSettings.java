package com.example.rodhuega.wacl;

import java.util.ArrayList;

/**
 * Created by pillo on 03/02/2018.
 */

public class AlarmsAndSettings {
    //Muchos aspectos por cubrir
    private ArrayList<Alarm> alarms;

    //Constructor en caso de que sea la primera vez que se abre la app
    public AlarmsAndSettings() {
        alarms = new ArrayList<Alarm>();
    }

    //Constructor para cargar la info de otras sesiones de uso de la app
    public AlarmsAndSettings(ArrayList<Alarm> alarms) {
        this.alarms=alarms;
    }

    /**
     * Metodo que añade una alarma a la configuracion
     * @param a, Alarm
     */
    public void addAlarm(Alarm a) {
        alarms.add(a);
    }

    /**
     * Metodo get que devuelve todas las alarmas guardadas
     * @return alarms, ArrayList<Alarm>
     */
    public ArrayList<Alarm> getAlarms() {
        return alarms;
    }

    /**
     * Metodo set que cambia las alarmas de la configuracion
     * @param alarms, ArrayList<Alarm>
     */
    public void setAlarms(ArrayList<Alarm> alarms) {
        this.alarms = alarms;
    }
}
