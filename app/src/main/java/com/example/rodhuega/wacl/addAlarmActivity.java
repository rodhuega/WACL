package com.example.rodhuega.wacl;

import android.app.AlarmManager;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TimePicker;

import java.io.File;
import java.io.IOException;

public class addAlarmActivity extends AppCompatActivity {

    /**
     * Fichero donde las alarmas son guardadas
     */
    public String alarmsSavedFilePath;

    private int option;

    private Alarm editAlarm;

    private TimePicker alarmPicker;

    private AlarmsAndSettings myAlarms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        option=this.getIntent().getExtras().getInt("optionAddAlarm");
        alarmsSavedFilePath= this.getApplicationContext().getFilesDir().getPath().toString()+AlarmsAndSettings.NOMBREDELFICHERODECONF;
        //Cargar el TimePicker de seleccion de la alarma y cambiarle el estilo
        alarmPicker =  (TimePicker)findViewById(R.id.alarmPicker);
        alarmPicker.setIs24HourView(true);
        try {
            myAlarms = AlarmsAndSettings.loadAlarms(alarmsSavedFilePath);
            if (option == 2) {
                Log.e("WIP", "Desarrollemos");
                //Cargamos la alarma a editar.
                editAlarm = Alarm.loadAlarm(getApplicationContext().getFilesDir().getPath().toString() + AlarmsAndSettings.TEMPORALALARMFILE);
                alarmPicker.setHour(editAlarm.getHour());
                alarmPicker.setMinute(editAlarm.getMinute());
                ArrayToRepeatBox();
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
            boolean [] repeatArray = repeatBoxToArray();
            Alarm newAlarm;
            if(option==1) {
                newAlarm = new Alarm(myAlarms.getnID(), alarmPicker.getHour(), alarmPicker.getMinute(), 1, Settings.System.DEFAULT_RINGTONE_URI.toString(), repeatArray);
                myAlarms.addAlarm(newAlarm);
            }else {
                newAlarm = new Alarm(editAlarm.getId(), alarmPicker.getHour(), alarmPicker.getMinute(), 1, Settings.System.DEFAULT_RINGTONE_URI.toString(), repeatArray);
                myAlarms.replaceAlarm(editAlarm.getId(),newAlarm);
            }
            AlarmsAndSettings.saveAlarms(myAlarms,alarmsSavedFilePath);
            newAlarm.enableAlarmSound((AlarmManager)getSystemService(ALARM_SERVICE),this.getApplicationContext());
            Intent goToMain = new Intent(this.getApplicationContext(), MainActivity.class);
            startActivity(goToMain);
        }catch (IOException ioe) {
            Log.e("ER","Fallo al guardar" + ioe.getMessage());
        }
    }

    public boolean [] repeatBoxToArray() {
        boolean[] resultado = new boolean[7];
        resultado[0]= ((CheckBox) findViewById(R.id.MBox)).isChecked();
        resultado[1]= ((CheckBox) findViewById(R.id.TBox)).isChecked();
        resultado[2]= ((CheckBox) findViewById(R.id.WBox)).isChecked();
        resultado[3]= ((CheckBox) findViewById(R.id.RBox)).isChecked();
        resultado[4]= ((CheckBox) findViewById(R.id.FBox)).isChecked();
        resultado[5]= ((CheckBox) findViewById(R.id.SBox)).isChecked();
        resultado[6]= ((CheckBox) findViewById(R.id.UBox)).isChecked();
        return resultado;
    }

    public void ArrayToRepeatBox() {
        ((CheckBox) findViewById(R.id.MBox)).setChecked(editAlarm.getDays()[0]);
        ((CheckBox) findViewById(R.id.TBox)).setChecked(editAlarm.getDays()[1]);
        ((CheckBox) findViewById(R.id.WBox)).setChecked(editAlarm.getDays()[2]);
        ((CheckBox) findViewById(R.id.RBox)).setChecked(editAlarm.getDays()[3]);
        ((CheckBox) findViewById(R.id.FBox)).setChecked(editAlarm.getDays()[4]);
        ((CheckBox) findViewById(R.id.SBox)).setChecked(editAlarm.getDays()[5]);
        ((CheckBox) findViewById(R.id.UBox)).setChecked(editAlarm.getDays()[6]);
    }

}
