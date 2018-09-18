package pl.dwiekieta.swa;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

import pl.dwiekieta.swa.CSVPackage.CSVFragment;
import pl.dwiekieta.swa.CSVPackage.CSVLogFragment;
import pl.dwiekieta.swa.CSVPackage.CSVManager;
import pl.dwiekieta.swa.SensorsPackage.SensorData;
import pl.dwiekieta.swa.SensorsPackage.SensorsFragment;
import pl.dwiekieta.swa.TimePackage.TimeClass;
import pl.dwiekieta.swa.TimePackage.TimeFragment;


public class MainActivity extends AppCompatActivity implements SensorsFragment.SensorListener,
        TimeFragment.TimerListener, CSVFragment.CSVListener, CSVLogFragment.CSVLogListener,
        MainFragment.MainListener, CapturingFragment.CapturingListener{

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    android.support.v7.widget.Toolbar toolbar;

    MainFragment mainFragment;
    CapturingFragment capturingFragment;
    CSVFragment csvFragment;
    CSVLogFragment csvLogFragment;
    SensorsFragment sensorsFragment;
    TimeFragment timeFragment;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    NavigationView navigationView;

    TimeClass timeClass;
    SensorData sensorData;
    CSVManager csvManager;

    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    BroadcastReceiver broadcastReceiver;
    final static private long ONE_SECOND = 1000;
    final static private long ONE_MINUTE = ONE_SECOND * 60;
    final static private long ONE_HOUR = ONE_MINUTE * 60;
    final static private long ONE_DAY = ONE_HOUR * 24;
    final static private long TODAY = System.currentTimeMillis() - System.currentTimeMillis()%ONE_DAY - 2*ONE_HOUR;
    long triggerUnixTime;
    boolean isReadyToCapturing = false;
    boolean isCapturing = false;

    private int timerCycles = 0;
    private int Cycles =1000/30;
    private int viewRefresh = Cycles/5;
    private enum CurrentView {main, capturing, csv, sensors, time}
    CurrentView currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        wakeLock.acquire();

        permission();

        //        STARTING AT SPECIFIC TIME IN FILE /StartDir/Start.txt  //
        setup();



        //reading time from file
        //long triggerUnixTime = TODAY + 21*ONE_HOUR + 15*ONE_MINUTE + 0*ONE_SECOND;
        /*
        long triggerUnixTime = 0;
        File RootPath = Environment.getExternalStorageDirectory();
        String directory = "StartDir";
        File StartDirectory = new File(RootPath.getAbsolutePath() + '/' + directory);
        if(!StartDirectory.exists())
            StartDirectory.mkdir();
        File StartFile = new File(StartDirectory,"Start.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(StartFile));
            String text = null;

            if((text = reader.readLine()) != null) {
                triggerUnixTime = Long.parseLong(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        /*
        ------ CLASS ------
         */
        csvManager = new CSVManager();

        sensorData = new SensorData();
        sensorData.init(this);

        timeClass = new TimeClass();

        /*
        ------ THREADS ------
         */
        timerThread().start();

        /*
        ------- TOOLBAR ------
         */
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        /*
        ------- FRAGMENTS ------
         */
        mainFragment = new MainFragment();
        capturingFragment = new CapturingFragment();
        csvFragment = new CSVFragment();
        csvLogFragment = new CSVLogFragment();
        sensorsFragment = new SensorsFragment();
        timeFragment = new TimeFragment();

        currentView = CurrentView.main;

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, mainFragment);
        fragmentTransaction.commit();
        getSupportActionBar().setTitle(R.string.title_home);

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.home_id:{
                        currentView = CurrentView.main;
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, mainFragment);
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle(R.string.title_home);
                        item.setChecked(true);
                    } break;
                    case R.id.csv_id:{
                        csvFragment.setLastCSVState(MainActivity.this, csvManager);

                        currentView = CurrentView.csv;
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, csvFragment);
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle(R.string.title_csv);
                        item.setChecked(true);
                    } break;
                    case R.id.sensors_id:{
                        currentView = CurrentView.sensors;
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, sensorsFragment);
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle(R.string.title_sensors);
                        item.setChecked(true);
                    } break;
                    case R.id.time_id:{
                        currentView = CurrentView.time;
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, timeFragment);
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle(R.string.title_time);
                        item.setChecked(true);
                    } break;

                }

                drawerLayout.closeDrawers();
                return true;
            }
        });
        //reading time from localhost
        triggerUnixTime = SensorsFragment.time;
        //triggerUnixTime = 1000;
        alarmManager.setExact( AlarmManager.RTC_WAKEUP, triggerUnixTime ,pendingIntent );
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);

        actionBarDrawerToggle.syncState();
    }

    @Override
    public final void onDestroy(){

        alarmManager.cancel(pendingIntent);

        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
        timerThread().interrupt();
    }

    private Thread timerThread(){
        return new Thread(){
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

                while(true){
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(isCapturing)
                                    capturingFragment.update(MainActivity.this,csvManager,sensorData,timeClass);

                                if(timerCycles == viewRefresh) {
                                    switch (currentView) {
                                        case main: {
                                            CSVStatusEstablish(csvManager);
                                            sensorStatusEstablish(sensorData);
                                            timeStatusEstablish(timeClass);

                                            mainFragment.setCapturingButtonClickable(isReadyToCapturing);
                                        } break;
                                        case capturing:{
                                            capturingFragment.setViewRefresh(csvManager,sensorData, timeClass);
                                            capturingFragment.capturingIndicator(isCapturing);
                                        } break;
                                        case csv:{
                                            CSVactiveModules(csvManager);
                                            csvFragment.modulesRefresh(csvManager, sensorData.getCalibratedStatus(), timeClass.getSynchronizedStatus());
                                            csvFragment.directoryButtonManager(MainActivity.this,csvManager);
                                        } break;
                                        case sensors: {
                                            if (sensorData.getRunStatus())
                                                sensorsFragment.update(sensorData);
                                            sensorsFragment.setCaptureButton(sensorData.getRunStatus());
                                        } break;
                                        case time: {
                                            if (timeClass.getRunStatus())
                                                timeFragment.update(timeClass);
                                            timeFragment.setCaptureButton(timeClass.getRunStatus());
                                        } break;
                                    }

                                    timerCycles = 0;
                                } timerCycles++;
                            }
                        });
                        Thread.sleep(Cycles);
                    } catch (InterruptedException e){
                        Log.d("Timer in MainActivity","Interrupted when data has been updating");
                    }
                }
            }
        };
    }
    public Long getTimeFromFile()
    {
        long time = 0;
        File RootPath = Environment.getExternalStorageDirectory();
        String directory = "StartDir";
        File StartDirectory = new File(RootPath.getAbsolutePath() + '/' + directory);
        if(!StartDirectory.exists())
            StartDirectory.mkdir();
        File StartFile = new File(StartDirectory,"Start.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(StartFile));
            String text = null;

            if((text = reader.readLine()) != null) {
                triggerUnixTime = Long.parseLong(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return time;
    }
    
    public void CSVStatusEstablish(CSVManager csvManager){
        boolean errorState = false;

        if(!csvManager.getDirectorySetStatus()){
            mainFragment.setErrorState(MainActivity.this, MainFragment.ConnectedClass.CSV,R.string.csvApp_PATH);
            return;
        }

        errorState |= csvManager.getSensorsModuleErrorStatus();
        errorState |= csvManager.getTimeModuleErrorStatus();

        if(errorState) {
            mainFragment.setErrorState(MainActivity.this, MainFragment.ConnectedClass.CSV, R.string.sensorsapp_STOPPED);
            isReadyToCapturing = false;
        }
        else {
            mainFragment.setNormalState(MainActivity.this, MainFragment.ConnectedClass.CSV, R.string.sensorsapp_OK);
            isReadyToCapturing = true;
        }
    }

    public void CSVactiveModules(CSVManager csvManager) {
        if (sensorData.getFullActiveStatus())
            csvManager.addModule(MainActivity.this, CSVManager.CapturingModule.SensorsModule);
        else
            csvManager.subModule(MainActivity.this, CSVManager.CapturingModule.SensorsModule);

        if (timeClass.getRunStatus())
            csvManager.addModule(MainActivity.this, CSVManager.CapturingModule.TimeModule);
        else
            csvManager.subModule(MainActivity.this, CSVManager.CapturingModule.TimeModule);

    }

    private void sensorStatusEstablish(SensorData sensorData){
        if(sensorData.getRunStatus()) {
            if (sensorData.getFullActiveStatus()) {
                if (sensorData.getCalibratedStatus())
                    mainFragment.setNormalState(MainActivity.this, MainFragment.ConnectedClass.SENSORS, R.string.sensorsapp_OK);
                else
                    mainFragment.setWarningState(MainActivity.this, MainFragment.ConnectedClass.SENSORS, R.string.sensorsapp_UNCALIBRATED);
            } else
                mainFragment.setErrorState(MainActivity.this, MainFragment.ConnectedClass.SENSORS, R.string.noData);
        }else
            mainFragment.setErrorState(MainActivity.this, MainFragment.ConnectedClass.SENSORS,R.string.sensorsapp_STOPPED);
    }

    private void timeStatusEstablish(TimeClass timeClass){
        if(timeClass.getRunStatus()) {
            if (timeClass.getSynchronizedStatus())
                mainFragment.setNormalState(MainActivity.this, MainFragment.ConnectedClass.TIME, R.string.sensorsapp_OK);
            else
                mainFragment.setWarningState(MainActivity.this, MainFragment.ConnectedClass.TIME, R.string.sensorsapp_UNSYNCHRONIZED);
        }else
            mainFragment.setErrorState(MainActivity.this, MainFragment.ConnectedClass.TIME,R.string.sensorsapp_STOPPED);
    }

    private void permission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"Brak pozwolenia", Toast.LENGTH_LONG).show();

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)){

            } else {
                int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        } else {
            Toast.makeText(this,"Pozwolenie", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public final void onResume() {
        super.onResume();
        sensorData.systemResume();

        switch(currentView){
            case main:{
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, mainFragment);
                fragmentTransaction.commit();
            } break;
            case csv:{
                csvFragment.setLastCSVState(MainActivity.this, csvManager);

                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, csvFragment);
                fragmentTransaction.commit();
            } break;
            case sensors:{
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, sensorsFragment);
                fragmentTransaction.commit();
            } break;
            case time:{
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, timeFragment);
                fragmentTransaction.commit();
            } break;
        }
    }

    @Override
    public final void onPause(){
        sensorData.systemPause();
        super.onPause();
    }

    /*
    ******* SensorListener **********
     */

    @Override
    public void onCaptureButton(View view) {
        if(sensorData.getRunStatus())
            sensorData.pause();
        else
            sensorData.resume();
    }

    @Override
    public void onZeroPointSet(View view){
        if(sensorData.getRunStatus())
            sensorData.zeroPointSet();
    }

    /*
    ******* TimeListener **************
     */

    @Override
    public void onCaptureTimeButton(View view){
        if(timeClass.getRunStatus())
            timeClass.pause();
        else
            timeClass.resume();
    }

    /*
    ****** CSVListener *********
     */
    @Override
    public void activeModules(View view) {
        boolean checked = ((Switch) view).isChecked();

        switch (view.getId()){
            case R.id.SensorsModule_PowerButton:{
                csvFragment.activeSensorsModule(MainActivity.this, csvManager, checked, sensorData.getCalibratedStatus());
            } break;
            case R.id.TimeModule_PowerButton:{
                csvFragment.activeTimeModule(MainActivity.this, csvManager, checked, timeClass.getSynchronizedStatus());
            } break;
        }
    }

    @Override
    public void sensorModuleSelector(View view) {
        csvManager.deactivateModule(MainActivity.this, CSVManager.CapturingModule.SensorsModule,
                CSVManager.SensorsModule.RAW.ordinal());
        csvManager.deactivateModule(MainActivity.this, CSVManager.CapturingModule.SensorsModule,
                CSVManager.SensorsModule.RELATIVE.ordinal());

        switch (view.getId()){
            case R.id.SensorsModule_RAWButton:{
                csvManager.activateModule(MainActivity.this, CSVManager.CapturingModule.SensorsModule,
                        CSVManager.SensorsModule.RAW.ordinal());
            } break;
            case R.id.SensorsModule_RelativeButton:{
                csvManager.activateModule(MainActivity.this, CSVManager.CapturingModule.SensorsModule,
                        CSVManager.SensorsModule.RELATIVE.ordinal());
            } break;
            case R.id.SensorsModule_FullButton:{
                csvManager.activateModule(MainActivity.this, CSVManager.CapturingModule.SensorsModule,
                        CSVManager.SensorsModule.RAW.ordinal());
                csvManager.activateModule(MainActivity.this, CSVManager.CapturingModule.SensorsModule,
                        CSVManager.SensorsModule.RELATIVE.ordinal());
            } break;
        }
    }

    @Override
    public void timeModuleSelector(View view) {
        csvManager.deactivateModule(MainActivity.this, CSVManager.CapturingModule.TimeModule,
                CSVManager.TimeModule.DEVICE.ordinal());
        csvManager.deactivateModule(MainActivity.this, CSVManager.CapturingModule.TimeModule,
                CSVManager.TimeModule.NETWORK.ordinal());

        switch (view.getId()){
            case R.id.TimeModule_DeviceButton:{
                csvManager.activateModule(MainActivity.this, CSVManager.CapturingModule.TimeModule,
                        CSVManager.TimeModule.DEVICE.ordinal());
            } break;
            case R.id.TimeModule_NetworkButton:{
                csvManager.activateModule(MainActivity.this, CSVManager.CapturingModule.TimeModule,
                        CSVManager.TimeModule.NETWORK.ordinal());
            } break;
            case R.id.TimeModule_FullButton:{
                csvManager.activateModule(MainActivity.this, CSVManager.CapturingModule.TimeModule,
                        CSVManager.TimeModule.DEVICE.ordinal());
                csvManager.activateModule(MainActivity.this, CSVManager.CapturingModule.TimeModule,
                        CSVManager.TimeModule.NETWORK.ordinal());
            } break;
        }
    }

    @Override
    public void displayCSVLog(View view) {
        csvLogFragment.setContext(MainActivity.this);
        csvLogFragment.setLastState(csvManager);

        currentView = CurrentView.main;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, csvLogFragment);
        fragmentTransaction.commit();
        getSupportActionBar().setTitle(R.string.title_csvLog);
    }

    /*
    ***** CSVLogListener ***************
     */

    @Override
    public void displayCSVManager(View view) {
        csvFragment.setLastCSVState(MainActivity.this,csvManager);

        currentView = CurrentView.main;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, csvFragment);
        fragmentTransaction.commit();
        getSupportActionBar().setTitle(R.string.title_csv);
    }

    /*
    **** Directory **************
     */
    @Override
    public void directoryButton(View view){
        csvManager.setDirectory(csvFragment.directoryButtonManager(MainActivity.this, csvManager));
    }

    /*
    ***** Capturing *********
     */

    @Override
    public void homeCapturingButton(View view) {
        csvManager.prepareToCapturing(String.valueOf(timeClass.getDeviceTimeInMilliseconds()));

        capturingFragment.setContext(MainActivity.this);

        currentView = CurrentView.capturing;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, capturingFragment);
        fragmentTransaction.commit();
        getSupportActionBar().setTitle(R.string.title_capturing);

    }

    @Override
    public void capturingButton(View view){
        if(isCapturing){
            csvManager.stopCapturing();

            currentView = CurrentView.main;
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, mainFragment);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle(R.string.title_home);

            isCapturing = false;
            return;
        }


        capturingFragment.capturingButtonManager(MainActivity.this, false);
        isCapturing = csvManager.startCapturing();
    }
    private void setup() {

        broadcastReceiver = new BroadcastReceiver() {

            @Override

            public void onReceive(Context c, Intent i) {
                if (isReadyToCapturing) {
                    if (isCapturing) {
                        csvManager.stopCapturing();

                        currentView = CurrentView.main;
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, mainFragment);
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle(R.string.title_home);

                        isCapturing = false;
                        return;
                    }


                    capturingFragment.capturingButtonManager(MainActivity.this, false);
                    isCapturing = csvManager.startCapturing();
                }
            }

        };

        registerReceiver(broadcastReceiver, new IntentFilter("a") );

        pendingIntent = PendingIntent.getBroadcast( this, 0, new Intent("a"),
                0 );

        alarmManager = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));

    }

}

