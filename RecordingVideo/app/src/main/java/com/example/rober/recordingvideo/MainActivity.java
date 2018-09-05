package com.example.rober.recordingvideo;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;

public class MainActivity extends AppCompatActivity {
    private static int VIDEO_REQUEST = 101;
    private long unixTime;
    private Uri videoUri = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    private boolean checkMountedExternalStorage(){
        String state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state))
            return true;

        return false;
    }
    public void captureVideo(View view)
    {

        // Reading time
        unixTime = System.currentTimeMillis() / 1000L;

        // Recording video
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if(videoIntent.resolveActivity(getPackageManager())!=null)
        {
            startActivityForResult(videoIntent, VIDEO_REQUEST);

        }


    }

    public void playVideo(View view) {
        String sdCard = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
        String path = sdCard+"/SWAFiles/";
        Toast.makeText(this,"Location: " + path, Toast.LENGTH_LONG).show();

        File targetLocation = new File(path);
        File time = new File(targetLocation,Long.toString(unixTime));

        Toast.makeText(this,"targetLocation: " + time, Toast.LENGTH_LONG).show();
        Toast.makeText(this,"Unix time: " + unixTime, Toast.LENGTH_LONG).show();

        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.Time);
        textView.setText("Unix time of start: "+Long.toString(unixTime));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode==VIDEO_REQUEST && resultCode==RESULT_OK)
        {
            videoUri = data.getData();
        }
    }
}
