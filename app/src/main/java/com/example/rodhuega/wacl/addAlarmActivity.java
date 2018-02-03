package com.example.rodhuega.wacl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

public class addAlarmActivity extends AppCompatActivity {

    private TimePicker alarmPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        //Cargar el TimePicker de seleccion de la alarma y cambiarle el estilo
        alarmPicker =  (TimePicker)findViewById(R.id.alarmPicker);
        alarmPicker.setIs24HourView(true);
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
    }

}
