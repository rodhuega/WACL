package com.example.rodhuega.wacl;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.rodhuega.wacl.model.AlarmsAndSettings;
import com.example.rodhuega.wacl.model.Settings;

import java.io.IOException;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    private AlarmsAndSettings myAlarms;
    private Settings mySettings;
    private String alarmsSavedFilePath;
    private Spinner notificationPreSoundSpinner, postponeSpinner;
    private ArrayList<Integer> opcionesMinutos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Cargar la alarma
        alarmsSavedFilePath= this.getApplicationContext().getFilesDir().getPath().toString()+AlarmsAndSettings.NOMBREDELFICHERODECONF;
        myAlarms = MainActivity.loadAlarms(alarmsSavedFilePath);
        mySettings = myAlarms.getSettings();
        //adecuar la interfaz a lo configurado previamente.
        notificationPreSoundSpinner = (Spinner) findViewById(R.id.notificationPreSoundSpinner);
        postponeSpinner=(Spinner) findViewById(R.id.postponeSpinner);
        opcionesMinutos = new ArrayList<Integer>();
        opcionesMinutos.add(1);opcionesMinutos.add(2);opcionesMinutos.add(3);opcionesMinutos.add(5);opcionesMinutos.add(10);opcionesMinutos.add(15);opcionesMinutos.add(30);
        ArrayAdapter<Integer> adapterMinutes = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item,opcionesMinutos);
        notificationPreSoundSpinner.setAdapter(adapterMinutes);
        postponeSpinner.setAdapter(adapterMinutes);
        notificationPreSoundSpinner.setSelection(opcionesMinutos.indexOf(mySettings.getTimeNotificacionPreAlarm()));
        postponeSpinner.setSelection(opcionesMinutos.indexOf(mySettings.getPostponeTime()));
    }


    public void cancelButtonOnClick(View view) {
        finish();
    }

    public void saveButtonOnClick(View view) {
        myAlarms.getSettings().setPostponeTime(Integer.parseInt(postponeSpinner.getSelectedItem().toString()));
        myAlarms.getSettings().setTimeNotificacionPreAlarm(Integer.parseInt(notificationPreSoundSpinner.getSelectedItem().toString()));
        MainActivity.saveAlarms(myAlarms,alarmsSavedFilePath);
        Intent goToMain = new Intent(this.getApplicationContext(), MainActivity.class);
        startActivity(goToMain);
        finish();
    }


}
