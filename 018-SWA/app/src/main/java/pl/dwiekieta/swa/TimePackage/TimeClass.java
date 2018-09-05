package pl.dwiekieta.swa.TimePackage;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeClass {
    private long deviceTimeInMilliseconds;
    private long networkTimeInMilliseconds;
    private long differentTimeInMilliseconds;

    private Calendar devDate;
    private Calendar netDate;

    private SimpleDateFormat sdf;

    private String deviceTimeString;
    private String networkTimeString;

    private boolean isRun;
    private boolean totalRun;
    private boolean isSynchronized;

    private boolean Mutex;

    public TimeClass(){
        Mutex = true;

        devDate = netDate = Calendar.getInstance();
        deviceTimeInMilliseconds = networkTimeInMilliseconds =  devDate.getTimeInMillis();
        differentTimeInMilliseconds = 0;

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        deviceTimeString = sdf.format(devDate.getTime());
        networkTimeString = sdf.format(netDate.getTime());

        isRun = false;
        totalRun = false;
        isSynchronized = false;

        timerThread().interrupt();
    }

    public void resume(){
        isRun = true;

        timerThread().start();
    }

    public void systemResume(){
        if(totalRun)
            resume();

        totalRun = false;
    }

    public void pause(){
        isRun = false;

        timerThread().interrupt();
    }

    public void systemPause(){
        if(isRun)
            totalRun = true;
        else
            totalRun = false;

        pause();
    }

    public void updateTime(){
        while(!Mutex);
        Mutex = false;

        devDate = Calendar.getInstance();
        deviceTimeInMilliseconds = devDate.getTimeInMillis();
        networkTimeInMilliseconds = deviceTimeInMilliseconds - differentTimeInMilliseconds;
        netDate.setTimeInMillis(networkTimeInMilliseconds);

        deviceTimeString = sdf.format(devDate.getTime());
        networkTimeString = sdf.format(netDate.getTime());

        Mutex = true;
    }

    public final boolean getRunStatus(){
        return isRun;
    }

    public final boolean getSynchronizedStatus(){
        return isSynchronized;
    }

    public final long getDeviceTimeInMilliseconds(){
        return deviceTimeInMilliseconds;
    }

    public final long getNetworkTimeInMilliseconds(){
        return networkTimeInMilliseconds;
    }

    public final long getDifferentTimeInMilliseconds(){
        return differentTimeInMilliseconds;
    }

    public final String getDeviceTimeString(){
        return deviceTimeString;
    }

    public final String getNetworkTimeString(){
        return networkTimeString;
    }

    public void setDifferentTimeInMilliseconds(long diff){
        while(!Mutex);
        Mutex = false;

        differentTimeInMilliseconds = diff;

        Mutex = false;
    }

    public final Calendar getDevDate(){
        return devDate;
    }

    public final Calendar getNetDate(){
        return netDate;
    }

    private Thread timerThread(){
        return new Thread(){
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

                while(true){
                    try {

                        updateTime();

                        Thread.sleep(10);
                    } catch (InterruptedException e){
                        Log.d("Timer in TimeClass","Interrupted when time has been updating");
                    }
                }
            }
        };
    }

}
