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
                }
            }
        });
    }
}