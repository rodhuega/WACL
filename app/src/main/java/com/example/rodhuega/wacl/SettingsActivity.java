package com.example.rodhuega.wacl;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rodhuega.wacl.model.AlarmsAndSettings;
import com.example.rodhuega.wacl.model.LocationPS;
import com.example.rodhuega.wacl.model.Settings;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class SettingsActivity extends AppCompatActivity {

    private AlarmsAndSettings myAlarms;
    private Settings mySettings;
    private String alarmsSavedFilePath;
    private Spinner notificationPreSoundSpinner, postponeSpinner,ringtoneSpinner;
    private ArrayList<Integer> opcionesMinutos;
    private ArrayList<String>  ringtones;
    private ArrayAdapter<String> adapterRingtones;
    private TextView locationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Comprobar si dispongo de los permisos para poder acceder a seleccionar el archivo de audio. De no ser asi, procedo a perdirlos
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},AlarmsAndSettings.CONSTADDRINGTONE);
        }

        //Cargar la alarma
        alarmsSavedFilePath= this.getApplicationContext().getFilesDir().getPath().toString()+AlarmsAndSettings.NOMBREDELFICHERODECONF;
        myAlarms = MainActivity.loadAlarms(alarmsSavedFilePath);
        mySettings = myAlarms.getSettings();
        //adecuar la interfaz a lo configurado previamente.
        locationTextView= (TextView) findViewById(R.id.locationTextView);
        //En caso de que haya guardada alguna localizacion se muestra que es asi.
        if(myAlarms.getSettings().getDefaultLocation()!=null) {
            locationTextView.setText(myAlarms.getSettings().getDefaultLocation().getAddress());
        }
        //Spinners
        notificationPreSoundSpinner = (Spinner) findViewById(R.id.notificationPreSoundSpinner);
        postponeSpinner=(Spinner) findViewById(R.id.postponeSpinner);
        ringtoneSpinner=(Spinner) findViewById(R.id.ringtoneSpinner);
        //Configuracion de spinner que tienen como opciones minutos.
        opcionesMinutos = new ArrayList<Integer>();
        opcionesMinutos.add(1);opcionesMinutos.add(2);opcionesMinutos.add(3);opcionesMinutos.add(5);opcionesMinutos.add(10);opcionesMinutos.add(15);opcionesMinutos.add(30);
        ArrayAdapter<Integer> adapterMinutes = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item,opcionesMinutos);
        notificationPreSoundSpinner.setAdapter(adapterMinutes);
        postponeSpinner.setAdapter(adapterMinutes);
        notificationPreSoundSpinner.setSelection(opcionesMinutos.indexOf(mySettings.getTimeNotificacionPreAlarm()));
        postponeSpinner.setSelection(opcionesMinutos.indexOf(mySettings.getPostponeTime()));
        //Configuracion del spinner de tono.
        ringtones= mySettings.getRingtonesNames();
        adapterRingtones = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,ringtones);
        ringtoneSpinner.setAdapter(adapterRingtones);
        ringtoneSpinner.setSelection(ringtones.indexOf(mySettings.getRingtoneTrack().getName()));
    }


    /**
     * Metodo que se ejecuta cuando se pula el boton cancelar, no guarda ninguna configuracion. Descarta todos los valores.
     * @param view
     */
    public void cancelButtonOnClick(View view) {
        finish();
    }

    /**
     * Metodo que se ejecuta cuando se pulsa el boton guardar y guarda toda la configuracion por defecto
     * @param view
     */
    public void saveButtonOnClick(View view) {
        myAlarms.getSettings().setPostponeTime(Integer.parseInt(postponeSpinner.getSelectedItem().toString()));
        myAlarms.getSettings().setTimeNotificacionPreAlarm(Integer.parseInt(notificationPreSoundSpinner.getSelectedItem().toString()));
        myAlarms.getSettings().setRingtones(mySettings.getRingtones());
        myAlarms.getSettings().setRingtoneTrack(mySettings.searchRingtone(ringtoneSpinner.getSelectedItem().toString()));
        Log.e("WIP",ringtoneSpinner.getSelectedItem().toString());
        MainActivity.saveAlarms(myAlarms,alarmsSavedFilePath);
        Intent goToMain = new Intent(this.getApplicationContext(), MainActivity.class);
        startActivity(goToMain);
        finish();
    }

    /**
     * Metodo para seleccionar un lugar predeterminado para poder usar la metereologia ahi.
     * @param view
     */
    public void setLocationButtonOnClick(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this),AlarmsAndSettings.PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }



    //Metodos que se encargan de añadir un Ringtone

    /**
     * Metodo que abre un buscador de ficheros para buscar un .mp3 para reproducirlo como alarma
     * @param view
     */
    public void addRingtoneOnClick(View view) {
        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(AlarmsAndSettings.CONSTADDRINGTONE)
                .withFilter(Pattern.compile(".*\\.mp3$"))
                .withHiddenFiles(true)
                .start();
    }

    /**
     * Metodo que se ejecuta despues de haber seleccionado un fichero de audio o un lugar predeterminado
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AlarmsAndSettings.CONSTADDRINGTONE && resultCode == RESULT_OK) {//En caso de que sea por añadir un ringtone
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            String name = filePath.substring(filePath.lastIndexOf('/')+1);
            Uri uri = Uri.fromFile(new File(filePath));
            if(mySettings.addRingtone(name,uri.toString())) {//Si ese archivo no esta en el array, se añade y se actualiza la UI.
                ringtones.add(name);
                adapterRingtones.notifyDataSetChanged();
            }
        }else if (requestCode==AlarmsAndSettings.PLACE_PICKER && resultCode==RESULT_OK) {//En caso de que se use para buscar un lugar preteterminado
            Place place = PlacePicker.getPlace(data,this);
            locationTextView.setText(place.getAddress());
            myAlarms.getSettings().setDefaultLocation(new LocationPS(place.getLatLng().latitude,place.getLatLng().longitude,place.getAddress().toString()));
        }

    }

    /**
     * Metodo que se ejecuta despues de pedir permiso para seleccionar un archivo de audio
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==AlarmsAndSettings.CONSTADDRINGTONE) {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"Permission granted!", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this,"Permission denied!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
