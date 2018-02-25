package com.example.rodhuega.wacl;

import android.app.AlarmManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.rodhuega.wacl.model.Alarm;
import com.example.rodhuega.wacl.model.AlarmsAndSettings;
import com.example.rodhuega.wacl.model.Fecha;
import com.example.rodhuega.wacl.model.LocationPS;
import com.example.rodhuega.wacl.model.Ringtone;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

public class addAlarmActivity extends AppCompatActivity {

    /**
     * Fichero donde las alarmas son guardadas
     */
    public String alarmsSavedFilePath;

    private int option;

    private LocationPS locationForAlarm;

    private Alarm editAlarm;

    private TimePicker alarmPicker;

    private AlarmsAndSettings myAlarms;

    private DatePicker escogerFecha;
    private Switch daysOrDateSwitch;

    private Spinner notificationPreSoundSpinner, postponeSpinner,ringtoneSpinner;
    private ArrayList<Integer> opcionesMinutos;
    private ArrayList<String>  ringtones;
    private ArrayAdapter<String> adapterRingtones;
    private TextView locationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);


        locationTextView= (TextView) findViewById(R.id.locationTextView);
        //Recuperar el valor de option pasado, para saber si hay que editar o añadir una nueva
        option=this.getIntent().getExtras().getInt("optionAddAlarm");
        alarmsSavedFilePath= this.getApplicationContext().getFilesDir().getPath().toString()+AlarmsAndSettings.NOMBREDELFICHERODECONF;//directorio donde se guarda toda la configuracion
        //Cargar el TimePicker de seleccion de la alarma y cambiarle el estilo
        alarmPicker =  (TimePicker)findViewById(R.id.alarmPicker);
        alarmPicker.setIs24HourView(true);
        //Referencia de algunos Layouts
        final LinearLayout daysOrDateLayout = (LinearLayout) findViewById(R.id.daysOrDateLayout);//Layout que contiene una layout de fecha o de diversos dias
        final LinearLayout daysLayout = (LinearLayout) findViewById(R.id.daysLayout);//layout para marcar los diversos dias
        //Configuracion de datePicker
        escogerFecha = new DatePicker(this);
        Calendar calendariosParaFechaMin = Calendar.getInstance();
        if((calendariosParaFechaMin.get(Calendar.DAY_OF_YEAR)==365 && !Alarm.esBisiesto(calendariosParaFechaMin.get(Calendar.YEAR)))|| (calendariosParaFechaMin.get(Calendar.DAY_OF_YEAR)==366 && Alarm.esBisiesto(calendariosParaFechaMin.get(Calendar.YEAR))) ) {
            calendariosParaFechaMin.set(Calendar.YEAR,calendariosParaFechaMin.get(Calendar.YEAR)+1);
            calendariosParaFechaMin.set(Calendar.DAY_OF_YEAR,1);
        }else {
            calendariosParaFechaMin.set(Calendar.DAY_OF_YEAR,calendariosParaFechaMin.get(Calendar.DAY_OF_YEAR)+1);
        }
        escogerFecha.setMinDate(calendariosParaFechaMin.getTimeInMillis());
        //Configuracion de los spinner
        notificationPreSoundSpinner = (Spinner) findViewById(R.id.notificationPreSoundSpinner);
        postponeSpinner=(Spinner) findViewById(R.id.postponeSpinner);
        ringtoneSpinner=(Spinner) findViewById(R.id.ringtoneSpinner);
        opcionesMinutos = new ArrayList<Integer>();
        opcionesMinutos.add(1);opcionesMinutos.add(2);opcionesMinutos.add(3);opcionesMinutos.add(5);opcionesMinutos.add(10);opcionesMinutos.add(15);opcionesMinutos.add(30);
        ArrayAdapter<Integer> adapterMinutes = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item,opcionesMinutos);
        notificationPreSoundSpinner.setAdapter(adapterMinutes);
        postponeSpinner.setAdapter(adapterMinutes);

        //Switch que cambia segun si va a ser para fecha o para alarma normal o de varios dias
        daysOrDateSwitch = (Switch) findViewById(R.id.daysOrDateSwitch);
        daysOrDateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                daysOrDateLayout.removeAllViews();
                if(b) {//ponemos la view para seleccionar dias.
                    daysOrDateLayout.addView(daysLayout);
                }else {//ponemos la view para seleccionar fecha
                    daysOrDateLayout.addView(escogerFecha);
                }
            }
        });
        try {//Cargamos todas las alarmas
            myAlarms = AlarmsAndSettings.loadAlarms(alarmsSavedFilePath);
            //Mostrar por defecto configuracion
            notificationPreSoundSpinner.setSelection(opcionesMinutos.indexOf(myAlarms.getSettings().getTimeNotificacionPreAlarm()));
            postponeSpinner.setSelection(opcionesMinutos.indexOf(myAlarms.getSettings().getPostponeTime()));
            //Ringtone tono
            ringtones= myAlarms.getSettings().getRingtonesNames();
            adapterRingtones = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,ringtones);
            ringtoneSpinner.setAdapter(adapterRingtones);
            ringtoneSpinner.setSelection(ringtones.indexOf(myAlarms.getSettings().getRingtoneTrack().getName()));
            //Localizacion por defecto
            if(myAlarms.getSettings().getDefaultLocation()!=null) {
                locationTextView.setText(myAlarms.getSettings().getDefaultLocation().getAddress());
                locationForAlarm=myAlarms.getSettings().getDefaultLocation();
            }
            if (option == 2) {//caso de que editemos la alarma
                //Cargamos la alarma a editar.
                editAlarm = Alarm.loadAlarm(getApplicationContext().getFilesDir().getPath().toString() + AlarmsAndSettings.TEMPORALALARMFILE);
                //Mostrar hora anteriormente establecida
                alarmPicker.setHour(editAlarm.getHour());
                alarmPicker.setMinute(editAlarm.getMinute());
                //Mostrar la configuracion de spinner seleccionada
                postponeSpinner.setSelection(opcionesMinutos.indexOf(editAlarm.getPostponeTime()));
                notificationPreSoundSpinner.setSelection(opcionesMinutos.indexOf(editAlarm.getTimeNotificationPreAlarm()));
                ringtoneSpinner.setSelection(ringtones.indexOf(editAlarm.getRingtoneTrack().getName()));
                //Configurar para que muestre el lugar en el que estaba antes, Localizacion
                if(editAlarm.getLocation()!=null) {
                    locationTextView.setText(editAlarm.getLocation().getAddress());
                    locationForAlarm=editAlarm.getLocation();
                }
                //Configurar si es alarma de fecha o de dia/s
                if(editAlarm.getDateToSound()!=null) {//en caso de que la alarma a editar sea de tipo fecha. Mostramos en el calendario la fecha selecionada
                    daysOrDateSwitch.setChecked(false);
                    Calendar provisionalParaDayOfMont = Calendar.getInstance();
                    provisionalParaDayOfMont.set(Calendar.YEAR,editAlarm.getDateToSound().getAno());
                    provisionalParaDayOfMont.set(Calendar.DAY_OF_YEAR,editAlarm.getDateToSound().getDia());
                    escogerFecha.updateDate(editAlarm.getDateToSound().getAno(),provisionalParaDayOfMont.get(Calendar.MONTH),provisionalParaDayOfMont.get(Calendar.DAY_OF_MONTH));
                }else {//Alarma de varios dias.
                    ArrayToRepeatBox();
                }
            }
        }catch (IOException | ClassNotFoundException ioe) {
            Log.e("Error:", "error al cargar alarma");
        }
    }

    /**
     * Metodo que cancela la configuracion que se esta haciendo sobre la alarma
     * @param view, View
     */
    public void cancelButtonOnClick(View view) {
        finish();
    }

    /**
     * Metodo para seleccionar un lugar para poder usar la metereologia ahi.
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

    /**
     * Metodo que guarda la configuracion realizada sobre esa alarma y vuelve a MainActivity
     * @param view, View
     */
    public void SaveButtonOnClick(View view) {
        Log.e("PI", "presionado boton save");
        //habria que hacer destincion de si es por fecha o no////////////////////////////////////////

        try {
            Alarm newAlarm;
            //Conseguir el valor de los spinner
            int postponeTime = Integer.parseInt(postponeSpinner.getSelectedItem().toString());
            int timeNotificationPreAlarm = Integer.parseInt(notificationPreSoundSpinner.getSelectedItem().toString());
            Ringtone tono = myAlarms.getSettings().searchRingtone(ringtoneSpinner.getSelectedItem().toString());
            int idAUsar=option==1?myAlarms.getnID():editAlarm.getId();//saber si se va a usar una id nueva o una ya existente y asignar el valor
            if(!daysOrDateSwitch.isChecked()) {//caso en el que se pone una alarma de fecha
                //Conseguir el dayofYear escogido
                Calendar provisional = Calendar.getInstance();
                provisional.set(escogerFecha.getYear(),escogerFecha.getMonth(),escogerFecha.getDayOfMonth());
                //Crear fecha y alarma
                Fecha fechaASonar =new Fecha(escogerFecha.getYear(),provisional.get(Calendar.DAY_OF_YEAR),alarmPicker.getHour(),alarmPicker.getMinute());
                newAlarm= new Alarm(idAUsar,alarmPicker.getHour(),alarmPicker.getMinute(), postponeTime, timeNotificationPreAlarm,tono,fechaASonar,locationForAlarm);
            }else {//caso en el que se pone una fecha de varios dias o de un dia
                int [] repeatArray = repeatBoxToArray();
                newAlarm = new Alarm(idAUsar, alarmPicker.getHour(), alarmPicker.getMinute(), postponeTime, timeNotificationPreAlarm,tono, repeatArray,locationForAlarm);
            }
            if(option==1) {//caso en que añadimos la alarma
                myAlarms.addAlarm(newAlarm);
            }else {//caso en que ha sido editada y la sustituimos
                myAlarms.replaceAlarm(editAlarm.getId(),newAlarm);
            }
            //guardar la alarma, activarla e ir al mainActivity
            AlarmsAndSettings.saveAlarms(myAlarms,alarmsSavedFilePath);
            newAlarm.enableAlarmSound((AlarmManager)getSystemService(ALARM_SERVICE),this.getApplicationContext(),false,false);
            Intent goToMain = new Intent(this.getApplicationContext(), MainActivity.class);
            startActivity(goToMain);
        }catch (IOException ioe) {
            Log.e("ER","Fallo al guardar" + ioe.getMessage());
        }
    }

    /**
     * Metodo que pasa las CheckBox de los dias que estan activos a un array de int. Si la checkbox estaba activada pone un -1 en la posicion del dia, en caso contrario un 0
     * @return int[] resultado
     */
    public int [] repeatBoxToArray() {
        int[] resultado = new int[7];
        resultado[0]= (((CheckBox) findViewById(R.id.MBox)).isChecked()) ? -1 : 0;
        resultado[1]= (((CheckBox) findViewById(R.id.TBox)).isChecked()) ? -1 : 0;
        resultado[2]= (((CheckBox) findViewById(R.id.WBox)).isChecked()) ? -1 : 0;
        resultado[3]= (((CheckBox) findViewById(R.id.RBox)).isChecked()) ? -1 : 0;
        resultado[4]= (((CheckBox) findViewById(R.id.FBox)).isChecked()) ? -1 : 0;
        resultado[5]= (((CheckBox) findViewById(R.id.SBox)).isChecked()) ? -1 : 0;
        resultado[6]= (((CheckBox) findViewById(R.id.UBox)).isChecked()) ? -1 : 0;
        return resultado;
    }

    /**
     * Metodo que en caso de que se edite una alarma y sea de dia/s activa las Checkbox de los dias que estaban selecionados
     */
    public void ArrayToRepeatBox() {
        ((CheckBox) findViewById(R.id.MBox)).setChecked((editAlarm.getDays()[0] < 0));
        ((CheckBox) findViewById(R.id.TBox)).setChecked((editAlarm.getDays()[1] < 0));
        ((CheckBox) findViewById(R.id.WBox)).setChecked((editAlarm.getDays()[2] < 0));
        ((CheckBox) findViewById(R.id.RBox)).setChecked((editAlarm.getDays()[3] < 0));
        ((CheckBox) findViewById(R.id.FBox)).setChecked((editAlarm.getDays()[4] < 0));
        ((CheckBox) findViewById(R.id.SBox)).setChecked((editAlarm.getDays()[5] < 0));
        ((CheckBox) findViewById(R.id.UBox)).setChecked((editAlarm.getDays()[6] < 0));
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
     * Metodo que se ejecuta despues de haber seleccionado un fichero de audio.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AlarmsAndSettings.CONSTADDRINGTONE && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            String name = filePath.substring(filePath.lastIndexOf('/')+1);
            Uri uri = Uri.fromFile(new File(filePath));
            if(myAlarms.getSettings().addRingtone(name,uri.toString())) {//Si ese archivo no esta en el array, se añade y se actualiza la UI.
                ringtones.add(name);
                adapterRingtones.notifyDataSetChanged();
            }
        }else if (requestCode==AlarmsAndSettings.PLACE_PICKER && resultCode==RESULT_OK) {//En caso de que se use para buscar un lugar preteterminado
            Place place = PlacePicker.getPlace(data,this);
            locationTextView.setText(place.getAddress());
            locationForAlarm= new LocationPS(place.getLatLng().latitude,place.getLatLng().longitude,place.getAddress().toString());
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
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"Permission granted!", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this,"Permission denied!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}
