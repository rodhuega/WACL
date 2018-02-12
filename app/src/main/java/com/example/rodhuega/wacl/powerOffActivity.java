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
    private int code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_off);
        //Cargar alarma
        Intent intent = getIntent();
        int alarmID = intent.getIntExtra("alarmID",-10);
        code = intent.getIntExtra("code", Integer.MIN_VALUE);
        Log.e("Seguimineto", "alarmID: "+ alarmID);
        try {
            RunningAlarm = AlarmsAndSettings.loadAlarms(getApplicationContext().getFilesDir().getPath().toString()+AlarmsAndSettings.NOMBREDELFICHERODECONF).searchAlarmID(alarmID);
        }catch (IOException |ClassNotFoundException ioe) {
            Log.e("ERRRRRRRR", " ya tu sae");
        }
    }

    public void turnOffButtonOnClick(View view) {
        Log.e("WIP", "apagar alarma");
        RunningAlarm.turnOFFAlarmSound(this.getApplicationContext(),code);
        finish();
    }

    public void postponeButtonOnClick(View view) {
        Log.e("WIP", "Posponer alarma");
        //Desactivo la alarma,
        RunningAlarm.turnOFFAlarmSound(this,code);
        //Activamos la alarma de nuevo
        RunningAlarm.enableAlarmSound((AlarmManager)getSystemService(ALARM_SERVICE),this.getApplicationContext(),true);
        finish();
    }
}
