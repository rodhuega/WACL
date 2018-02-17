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
    private Fecha dateToSound;

    /**
     * Int que representa una identificacion de la alarma
     */
    private int id;

    /**
     * Int que representa los minutos que sera pospuesta la alarma
     */
    private int postponeTime;

    /**
     * Int que representa la hora despues de haber pospuesto la alarma
     */
    private int hourPostponeTime;

    /**
     * Int que representa los minutos despues de haber pospuesto la alarma
     */
    private int minutePostponeTime;

    /**
     * Int que representa cuanto tiempo antes sale la notificacion de que va a sonar la alarma. En caso de que su valor sea 0 no se muestra
     */
    private int timeNotificationPreAlarm;

    /**
     * Uri que contiene la pista que se va a reproducir en la alarma.
     */
    private String RingtoneTrack;

    //Constructor para dias o siguiente hora
    public Alarm(int id,int hour, int minute,int postponeTime,int timeNotificationPreAlarm, String RingtoneTrack, int[] days) {
        this.id = id;
        this.enabled = true;
        this.hour =hour;
        this.minute=minute;
        this.postponeTime=postponeTime;
        this.timeNotificationPreAlarm=timeNotificationPreAlarm;
        this.RingtoneTrack=RingtoneTrack;
        this.days=days;
        this.repeat = isRepeatEnabled();
        this.dateToSound=null;
        hourPostponeTime=hour;
        minutePostponeTime=minute;
    }
    //Constructor para fecha
    public Alarm(int id,int hour, int minute, int postponeTime,int timeNotificationPreAlarm,String RingtoneTrack, Fecha dateToSound) {
        this.id = id;
        this.enabled = true;
        this.hour=hour;
        this.minute= minute;
        this.postponeTime=postponeTime;
        this.timeNotificationPreAlarm=timeNotificationPreAlarm;
        this.RingtoneTrack=RingtoneTrack;
        this.dateToSound=dateToSound;
        this.repeat=false;
        hourPostponeTime=hour;
        minutePostponeTime=minute;
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

    public static boolean esBisiesto(int year) {
        if(((year%4==0) && (year%100!=0)) || (year%400==0)) {
            return true;
        }
        return false;
    }

    public  int diaDeLaSemanaEnElQueEstamosConMiSistema(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.MONDAY:return 0;
            case Calendar.TUESDAY:return 1;
            case Calendar.WEDNESDAY:return 2;
            case Calendar.THURSDAY:return 3;
            case Calendar.FRIDAY:return 4;
            case Calendar.SATURDAY:return 5;
            case Calendar.SUNDAY:return 6;
            default: return -1;
        }
    }

    public  int[] cuandoPonerLaAlarma(int hora, int minuto, int horaActual, int minutoActual,int diaActual, int yearActual,int dayOfWeek, int targetDay) {
        //Array que contiene que dia poner la alarma, posicion 0 del array dia, 1 año
        int[] resultado = new int[2];
        resultado[1]=yearActual;
        if(dayOfWeek==-1) {//caso en el que se pone para el dia inmediatamente siguiente, es decir, no es alarma que se repite cada semana
            if (horaActual>hora || (horaActual==hora && minutoActual>=minuto)) {//caso en el que se pone para el dia inmediatamente siguiente
                resultado[0]=diaActual+1;
            }else {//caso en el que se pone para el mismo dia
                resultado[0]=diaActual;
            }
        }else {//caso en que la alarma se repite todas las semanas
            dayOfWeek=diaDeLaSemanaEnElQueEstamosConMiSistema(dayOfWeek);
            int dentroDeCuando=targetDay-dayOfWeek;
            if(dentroDeCuando>0) {//el dia de la semana esta por venir
                resultado[0]= diaActual+dentroDeCuando;
            }else if(dentroDeCuando<0) {//el dia de la semana ya ha pasado
                resultado[0]=diaActual+7+dentroDeCuando;
            }else {//estas en ese dia de la semana
                resultado[0]=cuandoPonerLaAlarma(hora, minuto, horaActual,minutoActual,diaActual,yearActual,-1,-1)[0];
                if(resultado[0]!=diaActual) {
                    resultado[0]+=6;
                }
            }
        }
        //Casos de que empiece nuevo año o bisiesto
        if(resultado[0]>365 && !esBisiesto(yearActual)) {
            resultado[0]-=365;
        }else  if(resultado[0]>366 && esBisiesto(yearActual)) {
            resultado[0]-=366;
        }
        //si el dia se ha vuelto mas pequeño, significa que hemos cambiado de año y hay que incrementarlo, significa que hemos cambiado de año
        if(resultado[0]<diaActual) {
            resultado[1]=yearActual+1;
        }
        return resultado;
    }

    public void enableAlarmSound(AlarmManager alarmManager, Context ctx, boolean isAPostpone, boolean isRenableCode) {
        Calendar calendar = Calendar.getInstance();
        int horaActual=calendar.get(Calendar.HOUR_OF_DAY);
        int minutoActual=calendar.get(Calendar.MINUTE);
        int diaActual= calendar.get(Calendar.DAY_OF_YEAR);
        int yearActual= calendar.get(Calendar.YEAR);
        if(!isAPostpone) {//si es la alarma normal
            if (dateToSound != null) {//Si es una fecha,
                enableAlarmaOneTime(id, alarmManager, ctx, calendar, hour, minute, dateToSound.getDia(),dateToSound.getAno() , 1);
            } else if (!repeat && dateToSound == null) {//en caso de que solo se ponga para un dia(fecha mas cercana)
                //Ver si se pone para hoy o para mañana
                int[] cuandoPoner = cuandoPonerLaAlarma(hour, minute, horaActual, minutoActual, diaActual, yearActual, -1, -1);
                enableAlarmaOneTime(id, alarmManager, ctx, calendar, hour, minute, cuandoPoner[0], cuandoPoner[1], 1);

            } else if(!isRenableCode) {//en el caso de que se use alarma para diferentes dias de la semana
                //poner la alarma para esos dias con codigo que esta dentro del array Days
                for (int i = 0; i < days.length; i++) {
                    if (days[i] < 0) {//si ese dia esta habilitado, se pone la alarma para ese dia.
                        //WIP////////////////////////////////////
                        Calendar calendarProv = Calendar.getInstance();
                        int[] cuandoPoner = cuandoPonerLaAlarma(hour, minute, horaActual, minutoActual, diaActual, yearActual, calendarProv.get(Calendar.DAY_OF_WEEK), i);
                        enableAlarmaOneTime(days[i], alarmManager, ctx, calendar, hour, minute, cuandoPoner[0], cuandoPoner[1], 1);
                    }
                }
            }else if(!isAPostpone) {//si es para poner la alarma la siguiente semana
                int codeIndex = diaDeLaSemanaEnElQueEstamosConMiSistema(calendar.get(Calendar.DAY_OF_WEEK));
                int[] cuandoPoner = cuandoPonerLaAlarma(hour, minute, horaActual, minutoActual, diaActual, yearActual, calendar.get(Calendar.DAY_OF_WEEK), codeIndex);
                enableAlarmaOneTime(days[codeIndex], alarmManager, ctx, calendar, hour, minute, cuandoPoner[0], cuandoPoner[1], 1);
            }
            resetPostponeData();
        }else {//si es de posponer.
            setPostponeData(horaActual,minutoActual);
            Log.e("Datos posponer:", "Hora: " +hourPostponeTime+ " ,minuto: "+minutePostponeTime);
            int[] cuandoPoner = cuandoPonerLaAlarma(hourPostponeTime, minutePostponeTime, horaActual, minutoActual, diaActual, yearActual, -1, -1);
            if(repeat &&isRenableCode && isAPostpone) {
                //si es de varios dias a la semana saber que dia la pospongo.
                Calendar calendarProv = Calendar.getInstance();
                int codeIndex = diaDeLaSemanaEnElQueEstamosConMiSistema(calendarProv.get(Calendar.DAY_OF_WEEK));
                Log.e("DebugDificil", "Dia de la semana en el que estoy:"+codeIndex+"");//debug
                enableAlarmaOneTime(days[codeIndex], alarmManager, ctx, calendar, hourPostponeTime, minutePostponeTime, cuandoPoner[0], cuandoPoner[1], 1);
            }else {
                enableAlarmaOneTime(id, alarmManager, ctx, calendar, hourPostponeTime, minutePostponeTime, cuandoPoner[0], cuandoPoner[1], 1);
            }

        }
    }

    public void enableAlarmaOneTime(int code, AlarmManager alarmManager,Context ctx, Calendar calendar,int hora, int minuto, int diaAPoner, int yearAPoner, int action) {
        Log.e("DebugDificil","Dentro de enableAlarmOneTime");//Debug
        calendar.set(Calendar.YEAR,yearAPoner);
        calendar.set(Calendar.DAY_OF_YEAR,diaAPoner);
        calendar.set(Calendar.HOUR_OF_DAY,hora);
        calendar.set(Calendar.MINUTE,minuto);
        Intent goToEnable = new Intent(ctx, AlarmOperations.class);
        goToEnable.putExtra("action",action);
        goToEnable.putExtra("alarmID", id);
        goToEnable.putExtra("code", code);
        Log.e("miID",id+"");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,code,goToEnable,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        Log.e("DebugDificil","Fin de enableAlarmOneTime");
    }

    public void turnOFFAlarmSound(Context ctx, int code) {//ver casos
        Intent goToDisable = new Intent(ctx, AlarmOperations.class);
        goToDisable.putExtra("action",2);
        goToDisable.putExtra("alarmID", id);
        goToDisable.putExtra("code", code);
        ctx.sendBroadcast(goToDisable);
    }

    public void cancelAlarm(AlarmManager alarmManager, Context ctx) {//ver casos
        Intent goToEnable = new Intent(ctx, AlarmOperations.class);
        if(!repeat) {//alarma de fecha o de la hora siguiente mas cercana
            PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, id, goToEnable, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
        }else {//para alarma que se repite todas las semanas
            for (int i=0;i<days.length;i++) {
                if(days[i]<0) {//si ese dia esta habilitado, se desactiva la alarma
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, days[i], goToEnable, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(pendingIntent);
                }
            }
        }
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

    public void setPostponeData(int horaActual, int minutoActual) {
        minutePostponeTime=minutoActual+postponeTime;
        hourPostponeTime=horaActual;
        if(minutePostponeTime>=60) {
            minutePostponeTime-=60;
            hourPostponeTime++;
            if(hourPostponeTime>=24) {
                hourPostponeTime -= 24;
            }
        }
    }

    public void resetPostponeData() {
        hourPostponeTime=hour;
        minutePostponeTime=minute;
    }

    //Gets


    public int[] getDays() {
        return days;
    }

    public boolean getRepeat() {
        return repeat;
    }

    public Fecha getDateToSound() {
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

    public int getHourPostponeTime() {
        return hourPostponeTime;
    }

    public int getMinutePostponeTime() {
        return minutePostponeTime;
    }

    public int getTimeNotificationPreAlarm() {
        return timeNotificationPreAlarm;
    }

    //Sets


    public void setDays(int[] days) {
        this.days = days;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public void setDateToSound(Fecha dateToSound) {
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

    public void setHourPostponeTime(int hourPostponeTime) {
        this.hourPostponeTime = hourPostponeTime;
    }

    public void setMinutePostponeTime(int minutePostponeTime) {
        this.minutePostponeTime = minutePostponeTime;
    }

    public void setTimeNotificationPreAlarm(int timeNotificationPreAlarm) {
        this.timeNotificationPreAlarm = timeNotificationPreAlarm;
    }
}
