package com.example.rodhuega.wacl;

import java.io.Serializable;

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


    //Constructor para dias o siguiente hora
    public Alarm(int hour, int minute, boolean repeat, boolean[] days) {
        this.enabled = true;
    }
    //Constructor para fecha
    public Alarm(int hour, int minute, Date dateToSound) {
        this.enabled = true;
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
}
