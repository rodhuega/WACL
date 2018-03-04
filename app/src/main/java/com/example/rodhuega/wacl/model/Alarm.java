package com.example.rodhuega.wacl.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.rodhuega.wacl.AlarmOperations;

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
    private Ringtone RingtoneTrack;

    /**
     * Atributo que contiene la localizacion en la que se va a usar la alarma condicional, null en caso de no usarse
     */
    private LocationPS location;

    /**
     * Atributo boolean que indica si la alarma condicional esta activada
     */
    private boolean conditionalWeather;

    /**
     * Atributo que tendra sus elementos a true si debe de sonar en esa condicion.
     * boolean[4], posiciones a true en caso de que deba de sonar bajo esa condicion. = despejado, 1 nublado, 2 tormenta/lloviendo/truenos, 3 nevando
     */
    private boolean[] weatherEnabledSound;

    //Constructor para dias o siguiente hora
    public Alarm(int id,int hour, int minute,int postponeTime,int timeNotificationPreAlarm, Ringtone RingtoneTrack, int[] days,LocationPS location, boolean[] weatherEnabledSound) {
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
        //Tema para metereologia
        this.location=location;
        this.weatherEnabledSound=weatherEnabledSound;
        conditionalWeather=isConditionalWeatherEnabled();
    }
    //Constructor para fecha
    public Alarm(int id,int hour, int minute, int postponeTime,int timeNotificationPreAlarm,Ringtone RingtoneTrack, Fecha dateToSound,LocationPS location, boolean[] weatherEnabledSound) {
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
        //Tema para metereologia
        this.location=location;
        this.weatherEnabledSound=weatherEnabledSound;
        conditionalWeather=isConditionalWeatherEnabled();
        //quiza haga falta hacer new y pasar el array de days a false.
    }

    /**
     * Metodo que comprueba si se trata de una alarma de varios dias a la semana o no.
     * @return resultado, boolean true en caso afirmativo
     */
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

    /**
     * Metodo que mira si se ha activado la alarma condicional
     * @return
     */
    public boolean isConditionalWeatherEnabled() {
        boolean resultado = false;
        for(int i =0;i<weatherEnabledSound.length&&!resultado;i++) {
            if(!weatherEnabledSound[i]) {
                resultado=true;
            }
        }
        return resultado;
    }

    /**
     * Metodo usado para saber si un año es bisiesto
     * @param year, int del año a comprobar
     * @return resultado, boolean true en caso afirmativo
     */
    public static boolean esBisiesto(int year) {
        if(((year%4==0) && (year%100!=0)) || (year%400==0)) {
            return true;
        }
        return false;
    }

    /**
     * Metodo que pasa del sistema de represencaion de la Clase Calendar a mi sistema de representacion
     * @param dayOfWeek, int dia de representacion en clase Calendar
     * @return int, valor en mi sistema de representacion
     */
    public static int diaDeLaSemanaEnElQueEstamosConMiSistema(int dayOfWeek) {
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

    /**
     * Metodo que se encarga de calcular cuando hay que poner una alarma de un dia o de varios dias ala semana
     * @param hora, int hora que tiene sonar
     * @param minuto, int minuto que tiene que sonar
     * @param horaActual, int, hora actual
     * @param minutoActual, int, minuto actual
     * @param diaActual, int dia actual
     * @param yearActual, int año actual
     * @param dayOfWeek, int dia que es en la semana, -1 si day igual
     * @param targetDay, int dia que tiene que sonar, -1 si da igual
     * @return resultado, int[2] posicion 0 dia, posicion 1 año
     */
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

    /**
     * Metodo que activa la alarma y su PreNotificacion
     * @param alarmManager, AlarmManager
     * @param ctx, Context
     * @param isAPostpone, boolean true si es que se pospone
     * @param isRenableCode, boolean true si es que reactiva la alarma para la semana que viene.
     */
    public void enableAlarmSound(AlarmManager alarmManager, Context ctx, boolean isAPostpone, boolean isRenableCode) {
        Calendar calendar = Calendar.getInstance();
        int horaActual=calendar.get(Calendar.HOUR_OF_DAY);
        int minutoActual=calendar.get(Calendar.MINUTE);
        int diaActual= calendar.get(Calendar.DAY_OF_YEAR);
        int yearActual= calendar.get(Calendar.YEAR);
        //Distintos casos
        if(!isAPostpone) {//si es la alarma normal
            if (dateToSound != null) {//Si es una fecha
                Calendar calendarProv = Calendar.getInstance();
                //Notificacion PreAlarma
                int preSoundCode = Integer.parseInt(AlarmsAndSettings.PRENOTCONST+""+id+""+9);
                int [] cuandoPonerNotificacion = getPresoundTime(dateToSound.getAno(),dateToSound.getDia(),hour,minute);
                enableAlarmaOneTime(preSoundCode, alarmManager, ctx, calendarProv, cuandoPonerNotificacion[2], cuandoPonerNotificacion[3], cuandoPonerNotificacion[1], cuandoPonerNotificacion[0], 5);
                //Alarm
                enableAlarmaOneTime(id, alarmManager, ctx, calendar, hour, minute, dateToSound.getDia(),dateToSound.getAno() , 1);
            } else if (!repeat && dateToSound == null) {//en caso de que solo se ponga para un dia(fecha mas cercana)
                Calendar calendarProv = Calendar.getInstance();
                //Ver si se pone para hoy o para mañana
                int[] cuandoPoner = cuandoPonerLaAlarma(hour, minute, horaActual, minutoActual, diaActual, yearActual, -1, -1);
                //Notificacion
                int preSoundCode =Integer.parseInt(AlarmsAndSettings.PRENOTCONST+""+id+""+9);
                int [] cuandoPonerNotificacion = getPresoundTime(cuandoPoner[1],cuandoPoner[0],hour,minute);
                enableAlarmaOneTime(preSoundCode, alarmManager, ctx, calendarProv, cuandoPonerNotificacion[2], cuandoPonerNotificacion[3], cuandoPonerNotificacion[1], cuandoPonerNotificacion[0], 5);
                //Activar Alarma
                enableAlarmaOneTime(id, alarmManager, ctx, calendar, hour, minute, cuandoPoner[0], cuandoPoner[1], 1);
            } else if(!isRenableCode) {//en el caso de que se use alarma para diferentes dias de la semana
                //poner la alarma para esos dias con codigo que esta dentro del array Days
                for (int i = 0; i < days.length; i++) {
                    if (days[i] < 0) {//si ese dia esta habilitado, se pone la alarma para ese dia.
                        Calendar calendarProv = Calendar.getInstance();
                        int[] cuandoPoner = cuandoPonerLaAlarma(hour, minute, horaActual, minutoActual, diaActual, yearActual, calendarProv.get(Calendar.DAY_OF_WEEK), i);
                        //Notificacion
                        int preSoundCode =Integer.parseInt(AlarmsAndSettings.PRENOTCONST+""+id+""+i);
                        int [] cuandoPonerNotificacion = getPresoundTime(cuandoPoner[1],cuandoPoner[0],hour,minute);
                        enableAlarmaOneTime(preSoundCode, alarmManager, ctx, calendarProv, cuandoPonerNotificacion[2], cuandoPonerNotificacion[3], cuandoPonerNotificacion[1], cuandoPonerNotificacion[0], 5);
                        //Alarma
                        enableAlarmaOneTime(days[i], alarmManager, ctx, calendarProv, hour, minute, cuandoPoner[0], cuandoPoner[1], 1);
                    }
                }
            }else if(!isAPostpone) {//si es para poner la alarma la siguiente semana
                Calendar calendarProv = Calendar.getInstance();
                int codeIndex = diaDeLaSemanaEnElQueEstamosConMiSistema(calendar.get(Calendar.DAY_OF_WEEK));//Conseguir que dia se va a volver a activar
                int[] cuandoPoner = cuandoPonerLaAlarma(hour, minute, horaActual, minutoActual, diaActual, yearActual, calendar.get(Calendar.DAY_OF_WEEK), codeIndex);
                //Notificacion
                int preSoundCode =Integer.parseInt("-2"+(codeIndex+"").substring(2));
                int [] cuandoPonerNotificacion = getPresoundTime(cuandoPoner[1],cuandoPoner[0],hour,minute);
                enableAlarmaOneTime(preSoundCode, alarmManager, ctx, calendarProv, cuandoPonerNotificacion[2], cuandoPonerNotificacion[3], cuandoPonerNotificacion[1], cuandoPonerNotificacion[0], 5);
                //Alarma
                enableAlarmaOneTime(days[codeIndex], alarmManager, ctx, calendarProv, hour, minute, cuandoPoner[0], cuandoPoner[1], 1);
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

    /**
     * Metodo que hace que se produzca una accion cuando se programa, como que suene la alarma o que se envie la notificacion de que va a sonar en breve
     * @param code, int, code identificativo de la alarma
     * @param alarmManager, AlarmManager
     * @param ctx, Context
     * @param calendar, Calendar
     * @param hora, int hora a la que se programa
     * @param minuto, int minuto al que se programa
     * @param diaAPoner, int dia que se programa
     * @param yearAPoner, int año que se programa
     * @param action, int accion que se va a producir cuando se programe
     */
    public void enableAlarmaOneTime(int code, AlarmManager alarmManager,Context ctx, Calendar calendar,int hora, int minuto, int diaAPoner, int yearAPoner, int action) {
        Log.e("DebugDificil","Dentro de enableAlarmOneTime");//Debug
        //Cuando tiene que producirse la accion
        calendar.set(Calendar.YEAR,yearAPoner);
        calendar.set(Calendar.DAY_OF_YEAR,diaAPoner);
        calendar.set(Calendar.HOUR_OF_DAY,hora);
        calendar.set(Calendar.MINUTE,minuto);
        //Creacion del intent y envio de datos.
        Intent goToEnable = new Intent(ctx, AlarmOperations.class);
        goToEnable.putExtra("action",action);
        goToEnable.putExtra("alarmID", id);
        goToEnable.putExtra("code", code);
        Log.e("miID",id+"");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,code,goToEnable,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        Log.e("DebugDificil","Fin de enableAlarmOneTime");
    }

    /**
     * Metodo que apaga una alarma
     * @param ctx, Context
     * @param code, int codigo identificativo de la alarma
     */
    public void turnOFFAlarmSound(Context ctx, int code) {///Actualmente carece de utilidad
        Intent goToDisable = new Intent(ctx, AlarmOperations.class);
        goToDisable.putExtra("action",2);
        goToDisable.putExtra("alarmID", id);
        goToDisable.putExtra("code", code);
        ctx.sendBroadcast(goToDisable);
    }

    /**
     * Metodo que se encarga de cancelar una alarma para que no suene
     * @param alarmManager
     * @param ctx
     * @param isOnlyForToday, boolean que solo se usa si es una alarma de varios dias a la semana. Se pone a true si es para apagarla desde preNotificacion, en cualquier otro caso a false, tambien a false en caso de no usarse
     * @param day, int. Parametro que solo se usa si isOnlyForToday es true. En caso de que se use lleva el dia en mi sistema de refencia. En caso de no usarse un -1
     */
    public void cancelAlarm(AlarmManager alarmManager, Context ctx, boolean isOnlyForToday, int day) {//ver casos
        Intent goToEnable = new Intent(ctx, AlarmOperations.class);
        if(!repeat) {//alarma de fecha o de la hora siguiente mas cercana
            PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, id, goToEnable, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
        }else {//para alarma que se repite todas las semanas
            if(!isOnlyForToday) {
                for (int i = 0; i < days.length; i++) {
                    if (days[i] < 0) {//si ese dia esta habilitado, se desactiva la alarma
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, days[i], goToEnable, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.cancel(pendingIntent);
                    }
                }
            }else {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, days[day], goToEnable, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);
            }
        }
    }

    /**
     * Metodo que guarda una alarma en un fichero
     * @param path, String ruta donde se va a guardar.
     * @throws IOException
     */
    public void saveAlarm(String path) throws IOException {
        File f = new File(path);
        FileOutputStream f1 = new FileOutputStream(f);
        ObjectOutputStream f2 = new ObjectOutputStream(f1);
        f2.writeObject(this);
        f1.close();
        f2.close();
    }

    /**
     * Metodo que carga una alarma de un fichero
     * @param path, String, ruta donde esta guardado el fichero
     * @return resultado, Alarm.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Alarm loadAlarm(String path) throws IOException, ClassNotFoundException {
        File f = new File(path);
        FileInputStream f1 = new FileInputStream(f);
        ObjectInputStream f2 = new ObjectInputStream(f1);
        Alarm resultado = (Alarm) f2.readObject();
        f1.close();
        f2.close();
        return resultado;
    }

    /**
     * Metodo que pone los valores adecuados  en caso de que se posponga la alarma
     * @param horaActual, int hora actual
     * @param minutoActual, int minuto actual
     */
    public void setPostponeData(int horaActual, int minutoActual) {
        //Inicializacion de los valores normales
        minutePostponeTime=minutoActual+postponeTime;
        hourPostponeTime=horaActual;
        if(minutePostponeTime>=60) {//si se pasa a la siguiente hora//Casos especiales
            minutePostponeTime-=60;
            hourPostponeTime++;
            if(hourPostponeTime>=24) {//si se pasa al siguiente dia.
                hourPostponeTime -= 24;
            }
        }
    }

    /**
     * Metodo que resetea los valores de cuando se va a posponer la alarma para la siguiente vez que suene.
     */
    public void resetPostponeData() {
        hourPostponeTime=hour;
        minutePostponeTime=minute;
    }

    /**
     * Metodo que devuelve un array de dimension 4 que indica año, dia, hora y minuto en la que saldra la notificacion
     * @param ano, int año que va a sonar la alarma
     * @param dia, int dia que va a sonar la alarma
     * @param hora, int hora que va a sonar la alarma
     * @param minuto, int minuto que va a sonar la alarma
     * @return resultado, int[4]
     */
    public int[] getPresoundTime(int ano, int dia, int hora,int minuto) {
        //Inicializacion de valores normales.
        int[] resultado = new int[4];
        resultado[0]=ano;
        resultado[1]=dia;
        resultado[2]=hora;
        resultado[3] = minuto - timeNotificationPreAlarm;
        if(resultado[3]<0) {//si hay que pasar a la hora de antes.//Casos excepcionales
            resultado[3]+=60;
            resultado[2]-=-1;
            if(resultado[2]<0) {//si hay que pasar al dia de antes
                resultado[2]+=24;
                resultado[1]-=1;
                if(resultado[1]<1) {//si hay que pasar al año anterior
                    resultado[0]-=1;
                    if(esBisiesto(resultado[0])) {//en caso de que sea bisiesto, el dia es el 366
                        resultado[1]=366;
                    }else {//en caso de no ser bisiesto el dia es el 365
                        resultado[1]=365;
                    }
                }
            }
        }
        return resultado;
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

    public Ringtone getRingtoneTrack() {
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

    public LocationPS getLocation() {
        return location;
    }

    public boolean[] getWeatherEnabledSound() {
        return weatherEnabledSound;
    }

    public boolean getConditionalWeather() {
        return conditionalWeather;
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

    public void setRingtoneTrack(Ringtone ringtoneTrack) {
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

    public void setLocation(LocationPS location) {
        this.location = location;
    }

    public void setConditionalWeather(boolean conditionalWeather) {
        this.conditionalWeather = conditionalWeather;
        if(!conditionalWeather) {
            for (boolean bool: weatherEnabledSound) {
                bool=true;
            }
        }
    }

    public void setWeatherEnabledSound(boolean[] weatherEnabledSound) {
        this.weatherEnabledSound = weatherEnabledSound;
    }
}
