package com.example.rodhuega.wacl.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Clase que representa todas las alarmas que hay y la configuracion por defecto de ellas. Tambien tiene metodos para cargar el fichero y guardarlo ademas de constantes de nombre de ficheros.
 */

public class AlarmsAndSettings implements Serializable{
    public static final String NOMBREDELFICHERODECONF = "/alarmsAndSettings144.alc";
    public static final String TEMPORALALARMFILE = "/temporalAlarmFile.alm";
    public static final int DAYSALARMCONST = -100;
    public static final int PRENOTCONST=-200;//si se trata de alarmas de un dia o de fecha llevan un 9 como ultimo digito
    public static final int CONSTADDRINGTONE = 8888;
    public static final String NOTIFICATION_CHANNEL_ID="WALC_notification_channel";
    public static final int PLACE_PICKER = 9999;
    public static final String OPENWEATHERMAPAPIKEY="89ba0a1a427d4e4cd9e83686d0c5ec9e";
    //Muchos aspectos por cubrir
    private ArrayList<Alarm> alarms;

    private Settings settings;

    //Contador de ids
    private int nID;

    //Constructor en caso de que sea la primera vez que se abre la app
    public AlarmsAndSettings() {
        nID=0;
        alarms = new ArrayList<Alarm>();
        settings= new Settings(1,1);
    }

    //Constructor para cargar la info de otras sesiones de uso de la app
    public AlarmsAndSettings(int nID,ArrayList<Alarm> alarms, Settings settings) {
        this.nID=nID;
        this.alarms=alarms;
        this.settings = settings;
    }


    /**
     * Metodo que añade una alarma a la configuracion
     * @param a, Alarm a añadir
     */
    public void addAlarm(Alarm a) {
        nID+=1;
        alarms.add(a);
    }

    /**
     * Metodo que elimina la alarma que coincide con la id que se le ha pasado
     * @param id, int de la alarma a eliminar
     */
    public void deleteAlarm(int id) {
        boolean isDeleted = false;
        for(int i = 0; i<alarms.size() && !isDeleted;i++) {
            if(alarms.get(i).getId()==id){
               isDeleted=true;
               alarms.remove(i);
            }
        }
    }

    /**
     * Metodo que busca una alarma con la id proporcionada
     * @param id, int
     * @return resultado, Alarm
     */
    public Alarm searchAlarmID(int id) {
        boolean encontrado = false;
        Alarm resultado = null;
        int i = 0;
        while(!encontrado && i<alarms.size()) {
            if(alarms.get(i).getId()==id) {
                resultado=alarms.get(i);
                encontrado=true;
            }
            i++;
        }
        return resultado;
    }

    /**
     *  /**
     * Metodo que se encarga de cargar el fichero que guarda las alarmas y la configuracion de ellas
     * @param path, String path
     * @return AlarmsAndSettings
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static AlarmsAndSettings loadAlarms(String path) throws IOException, ClassNotFoundException {
        File f = new File(path);
        if(f.exists()) {
            FileInputStream f1 = new FileInputStream(f);
            ObjectInputStream f2 = new ObjectInputStream(f1);
            AlarmsAndSettings resultado = (AlarmsAndSettings)f2.readObject();
            f1.close();
            f2.close();
            return resultado;
        }else {
            return new AlarmsAndSettings();
        }
    }

    /**
     * Metodo que se encarga de guardar todo en un fichero.
     * @param myAl, AlarmsAndSettigns que se va a guardar
     * @param path, String de la ruta donde se va a guardar
     * @throws IOException
     */
    public static void saveAlarms(AlarmsAndSettings myAl, String path) throws IOException {
        File f = new File(path);
        FileOutputStream f1 = new FileOutputStream(f);
        ObjectOutputStream f2 = new ObjectOutputStream(f1);
        f2.writeObject(myAl);
        f1.close();
        f2.close();
    }

    /**
     * Metodo que reemplaza una alarma por otra ya existente
     * @param id, int, id de la alarma que va a ser reemplazada
     * @param al, Alarm, nueva alarma
     */
    public void replaceAlarm(int id, Alarm al) {
        boolean encontrado=false;
        for (int i = 0; i < alarms.size() && !encontrado; i++) {
            if(alarms.get(i).getId()==id) {
                encontrado=true;
                alarms.set(i,al);
            }
        }
    }

    //gets

    public ArrayList<Alarm> getAlarms() {
        return alarms;
    }

    public int getnID() {
        return nID;
    }

    public Settings getSettings() {
        return settings;
    }

    //sets

    public void setAlarms(ArrayList<Alarm> alarms) {
        this.alarms = alarms;
    }

    public void setnID(int nID) {
        this.nID = nID;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}
