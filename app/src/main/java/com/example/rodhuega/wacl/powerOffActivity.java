package com.example.rodhuega.wacl;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.rodhuega.wacl.model.Alarm;
import com.example.rodhuega.wacl.model.AlarmsAndSettings;

public class powerOffActivity extends AppCompatActivity {

    private int code,alarmID;
    private Intent goToActionService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_off);
        //Recibir datos
        Intent intent = getIntent();
        alarmID = intent.getIntExtra("alarmID",-10);
        code = intent.getIntExtra("code", Integer.MIN_VALUE);
        //Preparar intent del servicio que se va a enviar y enviar los datos
        goToActionService = new Intent(getApplicationContext(),RingtonePlayingService.class);
        goToActionService.putExtra("alarmID",alarmID);
        goToActionService.putExtra("code",code);
    }

    /**
     * Metodo que se activa cuando se presiona el boton de apagar y se va al servicio encargado, luego se cierra el activity
     * @param view, View
     */
    public void turnOffButtonOnClick(View view) {
        Log.e("WIP", "apagar alarma");
        goToActionService.putExtra("action",2);
        startService(goToActionService);
        finish();
    }

    /**
     * Metodo que se activa cuando se presiona el boton de posponer y se va al servicio encargado, luego se cierra el activity
     * @param view, View
     */
    public void postponeButtonOnClick(View view) {
        Log.e("WIP", "Posponer alarma");
        goToActionService.putExtra("action",4);
        startService(goToActionService);
        finish();
    }
}
