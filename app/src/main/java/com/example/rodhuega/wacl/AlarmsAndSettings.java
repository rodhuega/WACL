package com.example.rodhuega.wacl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by pillo on 03/02/2018.
 */

public class AlarmsAndSettings implements Serializable{
    //Muchos aspectos por cubrir
    private ArrayList<Alarm> alarms;

    //Contador de ids
    private long nID;

    //Constructor en caso de que sea la primera vez que se abre la app
    public AlarmsAndSettings() {
        nID=0;
        alarms = new ArrayList<Alarm>();
    }

    //Constructor para cargar la info de otras sesiones de uso de la app
    public AlarmsAndSettings(long nID,ArrayList<Alarm> alarms) {
        this.nID=nID;
        this.alarms=alarms;
    }


    /**
     * Metodo que añade una alarma a la configuracion
     * @param a, Alarm
     */
    public void addAlarm(Alarm a) {
        nID+=1;
        alarms.add(a);
    }

    /**
     * Metodo que elimina la alarma que coincide con la id que se le ha pasado
     * @param id, long
     */
    public void deleteAlarm(long id) {
        boolean isDeleted = false;
        for(int i = 0; i<alarms.size() && !isDeleted;i++) {
            if(alarms.get(i).getId()==id){
               isDeleted=true;
               alarms.remove(i);
            }
        }
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

    public static void saveAlarms(AlarmsAndSettings myAl, String path) throws IOException {
        File f = new File(path);
        FileOutputStream f1 = new FileOutputStream(f);
        ObjectOutputStream f2 = new ObjectOutputStream(f1);
        f2.writeObject(myAl);
        f1.close();
        f2.close();
    }

    //gets sets
    /**
     * Metodo get que devuelve todas las alarmas guardadas
     * @return alarms, ArrayList<Alarm>
     */
    public ArrayList<Alarm> getAlarms() {
        return alarms;
    }

    public long getnID() {
        return nID;
    }

    /**
     * Metodo set que cambia las alarmas de la configuracion
     * @param alarms, ArrayList<Alarm>
     */
    public void setAlarms(ArrayList<Alarm> alarms) {
        this.alarms = alarms;
    }

    public void setnID(long nID) {
        this.nID = nID;
    }
}