package com.example.rodhuega.wacl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class powerOffActivity extends AppCompatActivity {
    private Alarm RunningAlarm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_off);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            RunningAlarm = (Alarm) bundle.getSerializable("alarmObject");
            Log.e("ENTREEEEE","XDDDDDDDDD");
        }
    }

    public void turnOffButtonOnClick(View view) {
        Log.e("WIP", "apagar alarma");
        RunningAlarm.disableAlarm(this.getApplicationContext());
    }
}
