package com.miguelsanchezp.recorder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    Button button;
    static String pathname;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
        ActivityCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.FOREGROUND_SERVICE};
            ActivityCompat.requestPermissions (this, permissions, 1);
        }else{
            pathname = this.getFilesDir().getAbsolutePath()+"/";
            button = findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Recorder recorder = new Recorder();
                    recorder.record();
                }
            });
        }
    }
}