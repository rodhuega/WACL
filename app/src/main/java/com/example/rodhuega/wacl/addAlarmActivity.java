package com.example.rodhuega.wacl;

import android.app.AlarmManager;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TimePicker;

import java.io.IOException;
import java.util.Calendar;

public class addAlarmActivity extends AppCompatActivity {

    /**
     * Fichero donde las alarmas son guardadas
     */
    public String alarmsSavedFilePath;

    private int option;

    private Alarm editAlarm;

    private TimePicker alarmPicker;

    private AlarmsAndSettings myAlarms;

    private DatePicker escogerFecha;
    private Switch daysOrDateSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        option=this.getIntent().getExtras().getInt("optionAddAlarm");
        alarmsSavedFilePath= this.getApplicationContext().getFilesDir().getPath().toString()+AlarmsAndSettings.NOMBREDELFICHERODECONF;
        //Cargar el TimePicker de seleccion de la alarma y cambiarle el estilo
        alarmPicker =  (TimePicker)findViewById(R.id.alarmPicker);
        alarmPicker.setIs24HourView(true);
        //Layouts y configuracion de datePicker
        final LinearLayout daysOrDateLayout = (LinearLayout) findViewById(R.id.daysOrDateLayout);//Layout que contiene una layout de fecha o de diversos dias
        final LinearLayout daysLayout = (LinearLayout) findViewById(R.id.daysLayout);//layout para marcar los diversos dias
        escogerFecha = new DatePicker(this);
        //Switch que cambia segun si va a ser para fecha o para alarma normal o de varios dias
        daysOrDateSwitch = (Switch) findViewById(R.id.daysOrDateSwitch);
        daysOrDateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                daysOrDateLayout.removeAllViews();
                if(b) {//ponemos la view para seleccionar dias.
                    daysOrDateLayout.addView(daysLayout);
                }else {//ponemos la view para seleccionar fecha
                    daysOrDateLayout.addView(escogerFecha);
                }
            }
        });
        try {
            myAlarms = AlarmsAndSettings.loadAlarms(alarmsSavedFilePath);
            if (option == 2) {
                Log.e("WIP", "Desarrollemos");
                //Cargamos la alarma a editar.
                editAlarm = Alarm.loadAlarm(getApplicationContext().getFilesDir().getPath().toString() + AlarmsAndSettings.TEMPORALALARMFILE);
                alarmPicker.setHour(editAlarm.getHour());
                alarmPicker.setMinute(editAlarm.getMinute());
                if(editAlarm.getDateToSound()!=null) {//en caso de que la alarma a editar sea de tipo fecha.

                }else {//Alarma de varios dias.
                    ArrayToRepeatBox();
                }
            }
        }catch (IOException | ClassNotFoundException ioe) {
            Log.e("Error:", "error al cargar alarma");
        }
    }

    /**
     * Metodo que cancela la configuracion que se esta haciendo sobre la alarma
     * @param view, View
     */
    public void cancelButtonOnClick(View view) {
        finish();
    }

    /**
     * Metodo que guarda la configuracion realizada sobre esa alarma y vuelve a MainActivity
     * @param view, View
     */
    public void SaveButtonOnClick(View view) {
        Log.e("PI", "presionado boton save");
        //habria que hacer destincion de si es por fecha o no////////////////////////////////////////

        //Caso en el que no se usa una fecha.
        try {
            //Comrobar si se repite la alarma varios dias y que dias son.
            int [] repeatArray = repeatBoxToArray();
            Alarm newAlarm;

            int idAUsar=option==1?myAlarms.getnID():editAlarm.getId();
            if(!daysOrDateSwitch.isChecked()) {//caso en el que se pone una alarma de fecha
                //Conseguir el dayofYear escogido
                Calendar provisional = Calendar.getInstance();
                provisional.set(escogerFecha.getYear(),escogerFecha.getMonth(),escogerFecha.getDayOfMonth());
                //Crear fecha y alarma
                Fecha fechaASonar =new Fecha(escogerFecha.getYear(),provisional.get(Calendar.DAY_OF_YEAR),alarmPicker.getHour(),alarmPicker.getMinute());
                newAlarm= new Alarm(idAUsar,alarmPicker.getHour(),alarmPicker.getMinute(), 2, Settings.System.DEFAULT_RINGTONE_URI.toString(),fechaASonar);
            }else {//caso en el que se pone una fecha de varios dias o de un dia
                newAlarm = new Alarm(idAUsar, alarmPicker.getHour(), alarmPicker.getMinute(), 2, Settings.System.DEFAULT_RINGTONE_URI.toString(), repeatArray);
            }
            if(option==1) {//caso en que añadimos la alarma
                myAlarms.addAlarm(newAlarm);
            }else {//caso en que ha sido editada y la sustituimos
                myAlarms.replaceAlarm(editAlarm.getId(),newAlarm);
            }
            AlarmsAndSettings.saveAlarms(myAlarms,alarmsSavedFilePath);
            newAlarm.enableAlarmSound((AlarmManager)getSystemService(ALARM_SERVICE),this.getApplicationContext(),false,false);
            Intent goToMain = new Intent(this.getApplicationContext(), MainActivity.class);
            startActivity(goToMain);
        }catch (IOException ioe) {
            Log.e("ER","Fallo al guardar" + ioe.getMessage());
        }
    }

    public int [] repeatBoxToArray() {
        int[] resultado = new int[7];
        resultado[0]= (((CheckBox) findViewById(R.id.MBox)).isChecked()) ? -1 : 0;
        resultado[1]= (((CheckBox) findViewById(R.id.TBox)).isChecked()) ? -1 : 0;
        resultado[2]= (((CheckBox) findViewById(R.id.WBox)).isChecked()) ? -1 : 0;
        resultado[3]= (((CheckBox) findViewById(R.id.RBox)).isChecked()) ? -1 : 0;
        resultado[4]= (((CheckBox) findViewById(R.id.FBox)).isChecked()) ? -1 : 0;
        resultado[5]= (((CheckBox) findViewById(R.id.SBox)).isChecked()) ? -1 : 0;
        resultado[6]= (((CheckBox) findViewById(R.id.UBox)).isChecked()) ? -1 : 0;
        return resultado;
    }

    public void ArrayToRepeatBox() {
        ((CheckBox) findViewById(R.id.MBox)).setChecked((editAlarm.getDays()[0] < 0));
        ((CheckBox) findViewById(R.id.TBox)).setChecked((editAlarm.getDays()[1] < 0));
        ((CheckBox) findViewById(R.id.WBox)).setChecked((editAlarm.getDays()[2] < 0));
        ((CheckBox) findViewById(R.id.RBox)).setChecked((editAlarm.getDays()[3] < 0));
        ((CheckBox) findViewById(R.id.FBox)).setChecked((editAlarm.getDays()[4] < 0));
        ((CheckBox) findViewById(R.id.SBox)).setChecked((editAlarm.getDays()[5] < 0));
        ((CheckBox) findViewById(R.id.UBox)).setChecked((editAlarm.getDays()[6] < 0));
    }

}
