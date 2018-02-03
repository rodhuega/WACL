package com.example.rodhuega.wacl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class MainActivity extends AppCompatActivity {
    /**
     * Fichero donde las alarmas son guardadas
     */
    private File alarmsSavedFile;

    /**
     * Objeto donde se guarda la configuracion de las alarmas y las alarmas
     */
    private AlarmsAndSettings confAndAlarms;

    //Contexto de la app
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //Boton que crea una nueva alarma
        context = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToAddAlarm = new Intent(context, addAlarmActivity.class);
                startActivity(goToAddAlarm);
            }
        });

        //Cargar todas las alarmas guardadas a traves de un fichero

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

    /**
     * Metodo que se encarga de cargar el fichero que guarda las alarmas y la configuracion de ellas
     * @param f, File
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void loadAlarms(File f) throws IOException, ClassNotFoundException {
        if(f.exists()) {
            FileInputStream f1 = new FileInputStream(f);
            ObjectInputStream f2 = new ObjectInputStream(f1);
            confAndAlarms = (AlarmsAndSettings)f2.readObject();
        }else {
            confAndAlarms = new AlarmsAndSettings();
        }
    }

    /**
     * Metodo que dibuja una alarma en la GUI
     * @param a, Alarm
     */
    private void drawAlarm(Alarm a) {
        //Por implementar
    }
}
