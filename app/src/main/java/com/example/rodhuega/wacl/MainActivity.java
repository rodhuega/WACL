package com.example.rodhuega.wacl;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class MainActivity extends AppCompatActivity {

    /**
     * Fichero donde las alarmas son guardadas
     */
    private String alarmsSavedFilePath;

    /**
     * Objeto donde se guarda la configuracion de las alarmas y las alarmas
     */
    private AlarmsAndSettings confAndAlarms;

    private LinearLayout alarmsLayout;

    //Contexto de la app
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        alarmsSavedFilePath= this.getApplicationContext().getFilesDir().getPath().toString()+AlarmsAndSettings.NOMBREDELFICHERODECONF;

        //Boton que crea una nueva alarma
        context = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAlarms();
                Intent goToAddAlarm = new Intent(context, addAlarmActivity.class);
                startActivity(goToAddAlarm);

            }
        });
        //Cargar todas las alarmas guardadas a traves de un fichero
        alarmsLayout = (LinearLayout) findViewById(R.id.alarmsLayout);
        loadAlarms();
        drawAllAlarms();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void drawAllAlarms() {
        for (Alarm al: confAndAlarms.getAlarms()) {
            drawAlarm(al);
        }
    }
    /**
     * Metodo que dibuja una alarma en la GUI
     * @param a, Alarm
     */
    private void drawAlarm(Alarm a) {
        //Alarma en final para poder usar en innerClass
        final Alarm finalAlarm = a;
        //Layout que contendra toda la alarma
        LinearLayout alarmLayout = new LinearLayout(this.getApplicationContext());
        alarmLayout.setOrientation(LinearLayout.HORIZONTAL);

        //////Parte de informacion
        //Layout que contiene informacion
        LinearLayout infoLayout = new LinearLayout(this.getApplicationContext());
        infoLayout.setOrientation(LinearLayout.VERTICAL);

        //Hora en la que sonara la alarma, se añade a infoLayout
        TextView time = new TextView(this.getApplicationContext());
        //Pasar la hora y los minutos a 2 digitos de longitud y añadirlo a la Activity
        String hourString =twoDigits(finalAlarm.getHour())+"";
        String minString =twoDigits(finalAlarm.getMinute())+"";
        time.setText(hourString+":"+minString);
        infoLayout.addView(time);
        //añadir dias que se repite o fecha, haria falta if/else
        if(finalAlarm.getRepeat()) {
            //Layout para almacenar esta informacion
            LinearLayout daysLayout = new LinearLayout(this.getApplicationContext());
            daysLayout.setOrientation(LinearLayout.HORIZONTAL);

            //Comprobacion de los distintos dias
            if(finalAlarm.getDays()[0]) {
                drawDay(getResources().getString(R.string.m_text),daysLayout);
            }
            if(finalAlarm.getDays()[1]) {
                drawDay(getResources().getString(R.string.t_text),daysLayout);
            }
            if(finalAlarm.getDays()[2]) {
                drawDay(getResources().getString(R.string.w_text),daysLayout);
            }
            if(finalAlarm.getDays()[3]) {
                drawDay(getResources().getString(R.string.r_text),daysLayout);
            }
            if(finalAlarm.getDays()[4]) {
                drawDay(getResources().getString(R.string.f_text),daysLayout);
            }
            if(finalAlarm.getDays()[5]) {
                drawDay(getResources().getString(R.string.s_text),daysLayout);
            }
            if(finalAlarm.getDays()[6]) {
                drawDay(getResources().getString(R.string.u_text),daysLayout);
            }
            //Añadir esa informacion al  container Grande de la alarma
            infoLayout.addView(daysLayout);
        }
        //añadir parte de la informacion metereologica//////////////////////////////////////////////////////////

        /////Parte de botones
        //Diferentes botones de accion de la alarma
        //Layout que contiene diferentes botones de la alarma
        LinearLayout ButtonsLayout = new LinearLayout(this.getApplicationContext());
        ButtonsLayout.setOrientation(LinearLayout.VERTICAL);
        //toggleButton de si esta activa o no.
        final Switch activeSwitch = new Switch(this.getApplicationContext());
        activeSwitch.setChecked(finalAlarm.getEnabled());
        final Context finalctx = this;
        activeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.e("WIP", "pulsado cambio de estado de activacion de la alarma");
                if(finalAlarm.getEnabled()) { //significa que se va a desactivar
                    finalAlarm.setEnabled(false);
                    //ir a metodo que apaga la alarma
                    saveAlarms();
                }else {//signifca que se va a activar
                    finalAlarm.setEnabled(true);
                    finalAlarm.enableAlarmSound((AlarmManager)getSystemService(ALARM_SERVICE),finalctx);
                    //ir a metodo que activa que suene la alarm
                    saveAlarms();
                }
                activeSwitch.setChecked(finalAlarm.getEnabled());
            }
        });
        ButtonsLayout.addView(activeSwitch);
        //Boton de Editar
        Button editButton = new Button(this.getApplicationContext());
        editButton.setText(getResources().getText(R.string.edit_text));
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("WIP", "pulsado edit de alarma");
            }
        });
        ButtonsLayout.addView(editButton);
        //Boton de Borrar
        Button deleteButton = new Button(this.getApplicationContext());
        deleteButton.setText(getResources().getText(R.string.delete_text));
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("WIP", "pulsado delete de alarma");
                ////////////faltaria hacer que no suene
                //borrar Alarma del arrayList
                confAndAlarms.deleteAlarm(finalAlarm.getId());
                ///Reguardar la informacion del fichero
                saveAlarms();
                //borrar y repintar la GUI
                alarmsLayout.removeAllViews();
                drawAllAlarms();
            }
        });
        ButtonsLayout.addView(deleteButton);

        //Añadir los diferentes layouts para que salgan en la gui
        alarmLayout.addView(infoLayout);
        alarmLayout.addView(ButtonsLayout);
        alarmsLayout.addView(alarmLayout);
    }

    private void drawDay(String dayLetter, LinearLayout container) {
        TextView letterDayTextView = new TextView(this.getApplicationContext());
        letterDayTextView.setText(dayLetter);
        container.addView(letterDayTextView);
    }


    /**
     * Metodo que se asegura que cualquier int tenga 2 digitos a la hora de ser representado
     * @param x, int
     * @return resultado, String
     */
    public static String twoDigits(int x) {
        String resultado =x+"";
        if(resultado.length()==1) {
            resultado="0"+resultado;
        }
        return resultado;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAlarms();
        alarmsLayout.removeAllViews();
        drawAllAlarms();
    }

    private void loadAlarms() {
        try {
            confAndAlarms = AlarmsAndSettings.loadAlarms(alarmsSavedFilePath);
        }catch (IOException | ClassNotFoundException ioe) {
            Log.e("ERRRRR", ioe.getMessage());
        }
    }

    private void saveAlarms() {
        try {
            AlarmsAndSettings.saveAlarms(confAndAlarms,alarmsSavedFilePath);
        }catch (IOException e) {
            Log.e("ER","Error mainActivity"+e.getMessage());
        }
    }
}
