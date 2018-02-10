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
     * Array int de dias que sera de longitud 7 que contiene un 0 si esta desactivado ese dia o un numero negativo si esta habilitado
     */
    private int[] days;
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
    public Alarm(int id,int hour, int minute,int postponeTime, String RingtoneTrack, int[] days) {
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
        for(int i =0; i< days.length; i++) {
            if(days[i]<0) {
                resultado=true;
                //Construimos un codigo que sera el requestCode que se le pasara al PendingIntent de ese dia. Luego lo guardamos en el esa posicion del array
                //Formato del codigo CONST,ID,PosicionDelDia, la posicion del dia va 0 si es lunes a 6 si es domingo
                days[i] = Integer.parseInt(AlarmsAndSettings.DAYSALARMCONST+""+id+""+i);
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

    public int[] cuandoPonerLaAlarma(int horaActual, int minutoActual,int diaActual, int yearActual,int targetDay) {
        //Array que contiene que dia poner la alarma, posicion 0 del array dia, 1 año
        int[] resultado = new int[4];
        if(targetDay==-1) {//caso en el que se pone para el dia inmediatamente siguiente, es decir, no es alarma que se repite cada semana
            if (horaActual>hour || (horaActual==hour && minutoActual>minute)) {//caso en el que se pone para el dia inmediatamente siguiente
                if( esBisiesto(yearActual)|| diaActual==366) { //año no bisiesto o año bisiesto y estamos en el dia 366
                    resultado[0]=1;
                }else if(diaActual==365 && esBisiesto(yearActual)){ //bisiesto y estamos en el dia 365
                    resultado[0]= 366;
                }else {//dias habituales
                    resultado[0]=diaActual+1;
                }
            }else {
                resultado[0]=diaActual;
            }
        }else {//caso en que la alarma se repite todas las semanas

        }
        if(resultado[0]<diaActual) {//si el dia se ha vuelto mas pequeño, significa que hemos cambiado de año y hay que incrementarlo
            resultado[1]=yearActual+1;
        }

        return resultado;
    }

    public void enableAlarmSound(AlarmManager alarmManager, Context ctx) {
        Calendar calendar = Calendar.getInstance();
        if(dateToSound!=null) {//Si es una fecha,
            // hace falta verificar que se ha metido una fecha futuroa y tal, si no se cumple quiza tirar un throw

        }else if(!repeat && dateToSound==null) {//en caso de que solo se ponga para un dia(fecha mas cercana)
            //Ver si se pone para hoy o para mañana
            int horaActual=calendar.get(Calendar.HOUR_OF_DAY);
            int minutoActual=calendar.get(Calendar.MINUTE);
            int diaActual= calendar.get(Calendar.DAY_OF_YEAR);
            int yearActual= calendar.get(Calendar.YEAR);
            int[] cuandoPoner=cuandoPonerLaAlarma(horaActual,minutoActual,diaActual,yearActual,-1);
            enableAlarmaOneTime(id,alarmManager,ctx,calendar,cuandoPoner[0],cuandoPoner[1],1);

        }else {//en el caso de que se use alarma para diferentes dias de la semana
            //poner la alarma para esos dias con codigo que esta dentro del array Days
            for (int day:days) {
                if(day<0) {//si ese dia esta habilitado, se pone la alarma para ese dia.
                    //WIP////////////////////////////////////
                    cuandoPonerLaAlarma();
                    enableAlarmaOneTime();
                }
            }
            //pending intent para renovar la alarma casa semana, con la id de la alarma
        }
    }

    public void enableAlarmaOneTime(int code, AlarmManager alarmManager,Context ctx, Calendar calendar, int diaAPoner, int yearActual, int action) {
        calendar.set(Calendar.YEAR,yearActual);
        calendar.set(Calendar.DAY_OF_YEAR,diaAPoner);
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        Intent goToEnable = new Intent(ctx, AlarmOperations.class);
        goToEnable.putExtra("action",action);
        goToEnable.putExtra("alarmID", id);
        Log.e("miID",id+"");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,code,goToEnable,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    }

    public void turnOFFAlarmSound(Context ctx) {//ver casos
        Intent goToDisable = new Intent(ctx, AlarmOperations.class);
        goToDisable.putExtra("action",2);
        goToDisable.putExtra("alarmID", id);
        ctx.sendBroadcast(goToDisable);
    }

    public void cancelAlarm(AlarmManager alarmManager, Context ctx) {//ver casos
        Intent goToEnable = new Intent(ctx, AlarmOperations.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,id,goToEnable,PendingIntent.FLAG_UPDATE_CURRENT);
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


    public int[] getDays() {
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


    public void setDays(int[] days) {
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
