package com.example.rodhuega.wacl;

import android.app.AlarmManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;

public class powerOffActivity extends AppCompatActivity {
    private AlarmsAndSettings confAndAlarms;
    private Alarm RunningAlarm;
    private int code,alarmID;
    private Intent goToActionService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_off);
        //Cargar alarma
        Intent intent = getIntent();
        alarmID = intent.getIntExtra("alarmID",-10);
        code = intent.getIntExtra("code", Integer.MIN_VALUE);
        goToActionService = new Intent(getApplicationContext(),RingtonePlayingService.class);
        goToActionService.putExtra("alarmID",alarmID);
        goToActionService.putExtra("code",code);
    }

    public void turnOffButtonOnClick(View view) {
        Log.e("WIP", "apagar alarma");
        goToActionService.putExtra("action",2);
        startService(goToActionService);
        finish();
    }

    public void postponeButtonOnClick(View view) {
        Log.e("WIP", "Posponer alarma");
        goToActionService.putExtra("action",4);
        startService(goToActionService);
        finish();
    }
}
