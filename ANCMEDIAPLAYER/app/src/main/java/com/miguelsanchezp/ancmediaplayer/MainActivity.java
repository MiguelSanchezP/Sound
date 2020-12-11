package com.miguelsanchezp.ancmediaplayer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button ANCButton;

    public static boolean ANCStatus = false;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
        ActivityCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED ||
        ActivityCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            String [] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.FOREGROUND_SERVICE, Manifest.permission.MODIFY_AUDIO_SETTINGS};
            ActivityCompat.requestPermissions(this, permissions, 1);
        }

        final Thread ANCT = new Thread(new Runnable() {
            @Override
            public void run() {
                ANC.performANC();
            }
        });

        ANCButton = findViewById(R.id.ancbutton);
        ANCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ANCStatus = !ANCStatus;
                if (ANCStatus) {
                    new Thread(ANCT).start();
                }else{
                    ANCT.interrupt();
                    export (ANC.getAnalysisTimes(), "analysis");
                    export (ANC.getTrackTimes(), "track");
                }
            }
        });
    }

    private void export (ArrayList<Long> times, String filename) {
        File file = new File (this.getFilesDir() + "/" + filename + ".txt");
        try {
            OutputStream os = new FileOutputStream(file);
            try {
                for (Long l : times) {
                    os.write(l.toString().getBytes());
                    os.write("\n".getBytes());
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}