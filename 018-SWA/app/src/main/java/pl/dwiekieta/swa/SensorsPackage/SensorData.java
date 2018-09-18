package pl.dwiekieta.swa.SensorsPackage;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.util.Log;

import java.util.Locale;

/**
 * Created by dwiek on 21.04.2018.
 */

public class SensorData implements SensorEventListener {

    private static final float NS2S = 1.0f / 1000000000.0f;
    private long Timestamp;
    private long Sampling;


    private float[] mGravs;
    private float[] mGeoMags;
    private float[] mOrientation;
    private float[] mInclination;
    private float[] mRotationMatrix;

    private float[] dGravs;
    private float[] dGeoMags;
    private float[] dOrientation;
    private float[] dInclination;
    private float[] dRotationMatrix;

    private float[] zGravs;
    private float[] zGeoMags;
    private float[] zOrientation;
    private float[] zInclination;
    private float[] zRotationMatrix;

    private float dAngle;

    private SensorManager sensorManager;
    private Sensor accSens, magSens;

    private boolean totalRun;
    private boolean isRun;
    private boolean isCalibrated;
    private boolean isFullActive;

    boolean Mutex;

    private void zeros(){
        while (!Mutex);
        Mutex = false;

        for(int i = 0; i < 3; ++i) {
            zGravs[i] = mGravs[i] = 0;
            zGeoMags[i] = mGeoMags[i] = 0;
            zOrientation[i] = mOrientation[i] = 0;
        }

        for(int i = 0; i < 9; ++i) {
            zInclination[i] = mInclination[i] = 0;
            zRotationMatrix[i] = mRotationMatrix[i] = 0;
            if(i==0||i==4||i==8)
                zRotationMatrix[i] = mRotationMatrix[i] = 1;
        }

        Mutex = true;
    }

    public SensorData(){
        Mutex = true;

        mGravs = new float[3];
        mGeoMags = new float[3];
        mOrientation = new float[3];
        mInclination = new float[9];
        mRotationMatrix = new float[9];

        dGravs = new float[3];
        dGeoMags = new float[3];
        dOrientation = new float[3];
        dInclination = new float[9];
        dRotationMatrix = new float[9];

        zGravs = new float[3];
        zGeoMags = new float[3];
        zOrientation = new float[3];
        zInclination = new float[9];
        zRotationMatrix = new float[9];

        zeros();
        deltaCalculate();

        isRun = false;
        isCalibrated = false;
        isFullActive = false;
    }

    public void init(Context context){
        while(!Mutex);
        Mutex = false;

        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        try {
            accSens = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magSens = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            pause();
        } catch (NullPointerException e) {
            Log.d("sensory","nullptr");
        }

        Mutex = true;
    }

    public void resume(){

        sensorManager.registerListener(this, accSens, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magSens, SensorManager.SENSOR_DELAY_UI);

        isRun = true;
    }

    public void systemResume(){
        if(totalRun)
            resume();

        totalRun = false;
    }

    public void pause(){

        sensorManager.unregisterListener(this);

        isRun = false;
    }

    public void systemPause(){
        if(isRun)
            totalRun = true;
        else
            totalRun = false;

        pause();
    }

    public void zeroPointSet(){
        while (!Mutex);
        Mutex = false;

        for(int i = 0; i < 3; ++i) {
            zGravs[i] = mGravs[i];
            zGeoMags[i] = mGeoMags[i];
            zOrientation[i] = mOrientation[i];
        }

        for(int i = 0; i < 9; ++i) {
            zInclination[i] = mInclination[i];
            zRotationMatrix[i] = mRotationMatrix[i];
        }

        if(isFullActive)
            isCalibrated = true;
        else
            isCalibrated = false;
        Mutex = true;
    }

    public void deltaCalculate(){
        while (!Mutex);
        Mutex = false;

        for(int i = 0; i < 3; ++i) {
            dGravs[i] = mGravs[i] - zGravs[i];
            dGeoMags[i] = mGeoMags[i] - zGeoMags[i];
            dOrientation[i] = mOrientation[i] - zOrientation[i];
        }

        for(int i = 0; i < 9; ++i) {
            dInclination[i] = mInclination[i] - zInclination[i];
            // dRotationMatrix[i] = mRotationMatrix[i] - zRotationMatrix[i];
        }
        for(int i = 0; i<3 ;i++)
        {
            for(int j=0; j<3;j++)
            {
                dRotationMatrix[3*i+j] = 0;
                for(int k=0; k<3;k++)
                    dRotationMatrix[3*i+j] +=  zRotationMatrix[3*k+i] * mRotationMatrix[3*k+j];
            }
        }
        if(dRotationMatrix[1]<0)
            dAngle = (float) Math.acos(dRotationMatrix[0])*180/(float)Math.PI;
        else
            dAngle = (float) - Math.acos(dRotationMatrix[0])*180/(float)Math.PI;
        Mutex = true;
    }

    public final boolean getRunStatus(){
        return isRun;
    }

    public final boolean getCalibratedStatus(){
        return isCalibrated;
    }

    public final boolean getFullActiveStatus(){
        return isFullActive;
    }

    public final String get_mGravs(){
        return String.format(Locale.US,"%.5f %.5f %.5f", mGravs[0], mGravs[1], mGravs[2]);
    }

    public final String get_zGravs(){
        return String.format(Locale.US,"%.5f %.5f %.5f", zGravs[0], zGravs[1], zGravs[2]);
    }

    public final String get_dGravs(){
        return String.format(Locale.US,"%.5f %.5f %.5f", dGravs[0], dGravs[1], dGravs[2]);
    }

    public final String get_mGeoMags(){
        return String.format(Locale.US,"%.5f %.5f %.5f", mGeoMags[0], mGeoMags[1], mGeoMags[2]);
    }

    public final String get_zGeoMags(){
        return String.format(Locale.US,"%.5f %.5f %.5f", zGeoMags[0], zGeoMags[1], zGeoMags[2]);
    }

    public final String get_dGeoMags(){
        return String.format(Locale.US,"%.5f %.5f %.5f", dGeoMags[0], dGeoMags[1], dGeoMags[2]);
    }

    public final String get_mInclination(){
        String i1 = String.format(Locale.US,"%.5f %.5f %.5f", mInclination[0], mInclination[1], mInclination[2]);
        String i2 = String.format(Locale.US,"%.5f %.5f %.5f", mInclination[3], mInclination[4], mInclination[5]);
        String i3 = String.format(Locale.US,"%.5f %.5f %.5f", mInclination[6], mInclination[7], mInclination[8]);
        return String.format(Locale.US,"%s\n%s\n%s", i1, i2, i3);
    }

    public final String get_zInclination(){
        String i1 = String.format(Locale.US,"%.5f %.5f %.5f", zInclination[0], zInclination[1], zInclination[2]);
        String i2 = String.format(Locale.US,"%.5f %.5f %.5f", zInclination[3], zInclination[4], zInclination[5]);
        String i3 = String.format(Locale.US,"%.5f %.5f %.5f", zInclination[6], zInclination[7], zInclination[8]);
        return String.format(Locale.US,"%s\n%s\n%s", i1, i2, i3);
    }

    public final String get_dInclination(){
        String i1 = String.format(Locale.US,"%.5f %.5f %.5f", dInclination[0], dInclination[1], dInclination[2]);
        String i2 = String.format(Locale.US,"%.5f %.5f %.5f", dInclination[3], dInclination[4], dInclination[5]);
        String i3 = String.format(Locale.US,"%.5f %.5f %.5f", dInclination[6], dInclination[7], dInclination[8]);
        return String.format(Locale.US,"%s\n%s\n%s", i1, i2, i3);
    }

    public final String get_mRotationMatrix(){
        String r1 = String.format(Locale.US,"%.5f %.5f %.5f", mRotationMatrix[0], mRotationMatrix[1], mRotationMatrix[2]);
        String r2 = String.format(Locale.US,"%.5f %.5f %.5f", mRotationMatrix[3], mRotationMatrix[4], mRotationMatrix[5]);
        String r3 = String.format(Locale.US,"%.5f %.5f %.5f", mRotationMatrix[6], mRotationMatrix[7], mRotationMatrix[8]);
        return String.format(Locale.US,"%s\n%s\n%s", r1, r2, r3);
    }

    public final String get_zRotationMatrix(){
        String r1 = String.format(Locale.US,"%.5f %.5f %.5f", zRotationMatrix[0], zRotationMatrix[1], zRotationMatrix[2]);
        String r2 = String.format(Locale.US,"%.5f %.5f %.5f", zRotationMatrix[3], zRotationMatrix[4], zRotationMatrix[5]);
        String r3 = String.format(Locale.US,"%.5f %.5f %.5f", zRotationMatrix[6], zRotationMatrix[7], zRotationMatrix[8]);
        return String.format(Locale.US,"%s\n%s\n%s", r1, r2, r3);
    }

    public final String get_dRotationMatrix(){
        String r1 = String.format(Locale.US,"%.5f %.5f %.5f", dRotationMatrix[0], dRotationMatrix[1], dRotationMatrix[2]);
        String r2 = String.format(Locale.US,"%.5f %.5f %.5f", dRotationMatrix[3], dRotationMatrix[4], dRotationMatrix[5]);
        String r3 = String.format(Locale.US,"%.5f %.5f %.5f", dRotationMatrix[6], dRotationMatrix[7], dRotationMatrix[8]);
        return String.format(Locale.US,"%s\n%s\n%s", r1, r2, r3);
    }

    public final String get_mOrientation(){
        return String.format(Locale.US,"%.5f %.5f %.5f", mOrientation[0], mOrientation[1], mOrientation[2]);
    }

    public final String get_zOrientation(){
        return String.format(Locale.US,"%.5f %.5f %.5f", zOrientation[0], zOrientation[1], zOrientation[2]);
    }

    public final String get_dOrientation(){
        return String.format(Locale.US,"%.5f %.5f %.5f", dOrientation[0], dOrientation[1], dOrientation[2]);
    }

    public final String get_dAngle(){
        return String.format(Locale.US,"%.5f", dAngle);
    }
    public final String get_sampling(){
        return String.format(Locale.US,"%d", Sampling);
    }
    public final String get_RAWSensors(){
        String gravity = String.format(Locale.US,"%.5f %.5f  %.5f", mGravs[0], mGravs[1], mGravs[2]);
        String magnetic = String.format(Locale.US,"%.5f %.5f %.5f", mGeoMags[0], mGeoMags[1], mGeoMags[2]);
        String inclination = String.format(Locale.US,"%.5f %.5f %.5f\n%.5f %.5f %.5f\n%.5f %.5f %.5f",
                mInclination[0], mInclination[1], mInclination[2], mInclination[3], mInclination[4],
                mInclination[5], mInclination[6], mInclination[7], mInclination[8]);
        String rotation = String.format(Locale.US, "%.5f %.5f %.5f\n%.5f %.5f %.5f\n%.5f %.5f %.5f",
                mRotationMatrix[0], mRotationMatrix[1], mRotationMatrix[2], mRotationMatrix[3],
                mRotationMatrix[4], mRotationMatrix[5], mRotationMatrix[6], mRotationMatrix[7],
                mRotationMatrix[8]);
        String orientation = String.format(Locale.US,"%.5f %.5f %.5f", mOrientation[0], mOrientation[1], mOrientation[2]);

        return ("\t\tGravity sensor:\n" +gravity + "\n\t\tMagnetic filed sensor:\n" + magnetic +
                "\n\t\tInclination sensor:\n" + inclination + "\nRotation sensor:\n" + rotation +
                "\n\t\tOrientation sensor:\n" + orientation);
    }

    public final String get_RelativeSensors(){
        String gravity = String.format(Locale.US,"%.5f %.5f %.5f", dGravs[0], dGravs[1], dGravs[2]);
        String magnetic = String.format(Locale.US,"%.5f %.5f %.5f", dGeoMags[0], dGeoMags[1], dGeoMags[2]);
        String inclination = String.format(Locale.US,"%.5f %.5f %.5f\n%.5f %.5f %.5f\n%.5f %.5f %.5f",
                dInclination[0], dInclination[1], dInclination[2], dInclination[3], dInclination[4],
                dInclination[5], dInclination[6], dInclination[7], dInclination[8]);
        String rotation = String.format(Locale.US,"%.5f %.5f %.5f\n%.5f %.5f %.5f\n%.5f %.5f %.5f",
                dRotationMatrix[0], dRotationMatrix[1], dRotationMatrix[2], dRotationMatrix[3],
                dRotationMatrix[4], dRotationMatrix[5], dRotationMatrix[6], dRotationMatrix[7],
                dRotationMatrix[8]);
        String orientation = String.format(Locale.US,"%.5f %.5f %.5f", dOrientation[0], dOrientation[1], dOrientation[2]);
        String angle = String.format(Locale.US,"%.5f", dAngle);
        return ("\t\tGravity sensor:\n" +gravity + "\n\t\tMagnetic filed sensor:\n" + magnetic +
                "\n\t\tInclination sensor:\n" + inclination + "\nRotation sensor:\n" + rotation +
               // "\n\t\tOrientation sensor:\n" + orientation +
                "\n\t\tAngle:\n" + angle );
    }

    public final String CSV_RAWSensors(){
        String gravity = String.format(Locale.US,"%.5f,%.5f,%.5f", mGravs[0], mGravs[1], mGravs[2]);
        String magnetic = String.format(Locale.US,"%.5f,%.5f,%.5f", mGeoMags[0], mGeoMags[1], mGeoMags[2]);
        String inclination = String.format(Locale.US,"%.5f,%.5f,%.5f,%.5f,%.5f,%.5f,%.5f,%.5f,%.5f",
                mInclination[0], mInclination[1], mInclination[2], mInclination[3], mInclination[4],
                mInclination[5], mInclination[6], mInclination[7], mInclination[8]);
        String rotation = String.format(Locale.US,"%.5f,%.5f,%.5f,%.5f,%.5f,%.5f,%.5f,%.5f,%.5f",
                mRotationMatrix[0], mRotationMatrix[1], mRotationMatrix[2], mRotationMatrix[3],
                mRotationMatrix[4], mRotationMatrix[5], mRotationMatrix[6], mRotationMatrix[7],
                mRotationMatrix[8]);
        String orientation = String.format(Locale.US,"%.5f,%.5f,%.5f", mOrientation[0], mOrientation[1], mOrientation[2]);
        String timestamp = Long.toString(Timestamp);
        return (timestamp + ',' + gravity + ',' + magnetic + ',' + inclination + ',' + rotation + ',' + orientation );
    }

    public final String CSV_RelativeSensors(){
        String gravity = String.format(Locale.US,"%.5f,%.5f,%.5f", dGravs[0], dGravs[1], dGravs[2]);
        String magnetic = String.format(Locale.US,"%.5f,%.5f,%.5f", dGeoMags[0], dGeoMags[1], dGeoMags[2]);
        String inclination = String.format(Locale.US,"%.5f,%.5f,%.5f,%.5f,%.5f,%.5f,%.5f,%.5f,%.5f",
                dInclination[0], dInclination[1], dInclination[2], dInclination[3], dInclination[4],
                dInclination[5], dInclination[6], dInclination[7], dInclination[8]);
        String rotation = String.format(Locale.US,"%.5f,%.5f,%.5f,%.5f,%.5f,%.5f,%.5f,%.5f,%.5f",
                dRotationMatrix[0], dRotationMatrix[1], dRotationMatrix[2], dRotationMatrix[3],
                dRotationMatrix[4], dRotationMatrix[5], dRotationMatrix[6], dRotationMatrix[7],
                dRotationMatrix[8]);
        String orientation = String.format(Locale.US,"%.5f,%.5f,%.5f", dOrientation[0], dOrientation[1], dOrientation[2]);
        String angle = String.format(Locale.US,"%.5f", dAngle);
        String timestamp = Long.toString(Timestamp);
        return (timestamp + ',' + angle + ',' + gravity + ',' + magnetic + ',' + inclination + ',' + rotation + ',' + orientation  );
    }

    public void set_mGravs(float[] nGravs){
        while (!Mutex)
        Mutex = false;

        assert nGravs != null : "nGravs nullptr";

        for(int i = 0; i < 3; ++i)
            mGravs[i] = nGravs[i];

        Mutex = true;
    }

    public void set_mGeoMags(float[] nGeoMags){
        while (!Mutex);
        Mutex = false;

        assert nGeoMags != null : "nGeoMags nullptr";

        for(int i = 0; i < 3; ++i)
            mGeoMags[i] = nGeoMags[i];

        Mutex = true;
    }

    public void initListeners(){
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_FASTEST);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        // Reading frequency
        Sampling = (System.currentTimeMillis() - Timestamp);
        Timestamp = System.currentTimeMillis();
        switch(event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                this.set_mGravs(event.values);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                this.set_mGeoMags(event.values);
                break;
        }

        sensorManager.getInclination(mInclination);
        sensorManager.getRotationMatrix(mRotationMatrix,mInclination,mGravs,mGeoMags);
        sensorManager.getOrientation(mRotationMatrix,mOrientation);

        if((mGeoMags[0] == 0) && (mGeoMags[1] == 0) && (mGeoMags[2] == 0))
            isFullActive = false;
        else
            isFullActive = true;

        this.deltaCalculate();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
