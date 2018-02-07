package com.example.rodhuega.wacl;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;

public class powerOffActivity extends AppCompatActivity {
    private Alarm RunningAlarm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_off);
        //Cargar alarma
        Intent intent = getIntent();
        int alarmID = intent.getIntExtra("alarmID",-10);
        Log.e("Seguimineto", "alarmID: "+ alarmID);
        try {
            RunningAlarm = AlarmsAndSettings.loadAlarms(getApplicationContext().getFilesDir().getPath().toString()+AlarmsAndSettings.NOMBREDELFICHERODECONF).searchAlarmID(alarmID);
        }catch (IOException |ClassNotFoundException ioe) {
            Log.e("ERRRRRRRR", " ya tu sae");
        }
    }

    public void turnOffButtonOnClick(View view) {
        Log.e("WIP", "apagar alarma");
        RunningAlarm.disableAlarm(this.getApplicationContext());
        finish();
    }
}
