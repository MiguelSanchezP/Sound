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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button ANCB;
    private Switch automaticS;
    private TextView phaseTV;
    private SeekBar phaseSB;
    private EditText frequencyET;
    private Button acceptB;
    private Button whiteNoiseB;

    public static boolean ANCStatus = false;
    public static boolean MANCStatus = false;
    public static boolean WNStatus = false;

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

        final Thread ANCM = new Thread(new Runnable() {
            @Override
            public void run() {
                MANC.play();
            }
        });

        final Thread WN = new Thread(new Runnable() {
            @Override
            public void run() {
                MANC.playWhiteNoise();
            }
        });

        ANCB = findViewById(R.id.ancbutton);
        automaticS = findViewById(R.id.automaticS);
        phaseTV = findViewById(R.id.phaseTV);
        phaseSB = findViewById(R.id.phaseSB);
        frequencyET = findViewById(R.id.frequencyET);
        acceptB = findViewById(R.id.acceptB);
        whiteNoiseB = findViewById(R.id.whiteNoiseB);
        ANCB.setEnabled(false);

        ANCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ANCStatus = !ANCStatus;
                if (ANCStatus) {
                    ANCB.setText(R.string.stop);
                    new Thread(ANCT).start();
                }else{
                    ANCT.interrupt();
                    ANCB.setText(R.string.ancbutton);
//                    export (ANC.getAnalysisTimes(), "analysis");
//                    export (ANC.getTrackTimes(), "track");
                }
            }
        });

        acceptB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MANCStatus = !MANCStatus;
                if (MANCStatus) {
                    MANC.setFrequency(Double.parseDouble(frequencyET.getText().toString()));
                    acceptB.setText(R.string.stop);
                    new Thread(ANCM).start();
                }else{
                    ANCM.interrupt();
                    acceptB.setText(R.string.acceptButton);
                }
            }
        });

        whiteNoiseB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WNStatus = !WNStatus;
                if (WNStatus) {
                    MANC.setFrequency(Double.parseDouble(frequencyET.getText().toString()));
                    whiteNoiseB.setText(R.string.stop);
                    new Thread(WN).start();
                }else{
                    WN.interrupt();
                    whiteNoiseB.setText(R.string.WhiteNoiseButton);
                }
            }
        });

        phaseSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double phase = 2*Math.PI*progress/100;
                phaseTV.setText(Double.toString(phase/Math.PI) + "π");
                MANC.setPhase(phase);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        automaticS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    phaseSB.setEnabled(false);
                    phaseTV.setEnabled(false);
                    frequencyET.setEnabled(false);
                    acceptB.setEnabled(false);
                    ANCB.setEnabled(true);
                }else{
                    phaseSB.setEnabled(true);
                    phaseTV.setEnabled(true);
                    frequencyET.setEnabled(true);
                    acceptB.setEnabled(true);
                    ANCB.setEnabled(false);
                    if (ANCStatus) {
                        ANCT.interrupt();
                        ANCStatus = false;
                        ANCB.setText(R.string.ancbutton);
                    }
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