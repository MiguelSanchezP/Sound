package com.miguelsanchezp.frequencyIdentifier;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int FFT = 1;
    private static final int DFT = 0;
    private static final int GAUSSIAN = 0;
    private static final int PARABOLIC = 1;

    private Button button;
    private TextView textView;
    private TextView textView2;
    private int N = 4096;
    private double duration = 0.1;
    private int audioSource = MediaRecorder.AudioSource.MIC;
    private int sampleRate = 44100;
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private final double MIC_LAPSE = 0.02;
    private Complex[] dft_data;
    private Complex[] fft_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
        || ActivityCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
            String [] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.FOREGROUND_SERVICE};
            ActivityCompat.requestPermissions(this, permissions, 1);
        }else{
            button = findViewById(R.id.button);
            textView = findViewById(R.id.textView);
            textView2 = findViewById(R.id.textView2);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    short[] values = export_recording(duration, audioSource, sampleRate, channelConfig, audioFormat);
                    long startTime = System.nanoTime();
                    dft_data = dft(values, N);
                    long endTime = System.nanoTime();
                    Log.d(TAG, "onClick: dft elapsed time: " + (endTime-startTime)/1000000);
                    analyse(dft_data, N, DFT, GAUSSIAN);
                    startTime = System.nanoTime();
                    fft_data = fft(values, N);
                    endTime = System.nanoTime();
                    Log.d(TAG, "onClick: fft elapsed time: " + (endTime-startTime)/1000000);
                    analyse(fft_data, N, FFT, GAUSSIAN);
                }
            });
        }
    }

    private short [] export_recording (double duration, int audioSource, int sampleRate, int channelConfig, int audioFormat) {
        int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        AudioRecord audioRecord = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, bufferSize);
        audioRecord.startRecording();
        short[] Buffer = new short[(int)(sampleRate*(duration+MIC_LAPSE))];
        audioRecord.read(Buffer, 0, (int)(sampleRate*(duration+MIC_LAPSE)));
        audioRecord.stop();
        short [] Buffer_def = new short[(int)(Buffer.length-MIC_LAPSE*sampleRate)];
        System.arraycopy(Buffer, (int) (MIC_LAPSE * sampleRate), Buffer_def, 0, Buffer.length - (int) (MIC_LAPSE * sampleRate));
        return Buffer_def;
    }

    private Complex[] dft (short [] values, int N) {
//        long start_time = System.nanoTime();
        Complex [] series = new Complex[N];
        Complex[] terms = new Complex[N];
        for (int k = 0; k<N; k++) {
            for (int n= 0; n <N; n++) {
                terms[n] = new Complex(values[n]*Math.cos(2*Math.PI*k*n/N), values[n]*Math.sin(-2*Math.PI*k*n/N));
            }
            double reals = 0.0;
            double imaginaries = 0.0;
            for (Complex t  : terms) {
                reals += t.getReal();
                imaginaries += t.getImaginary();
            }
            series[k] = new Complex (reals, imaginaries);
        }
//        long end_time = System.nanoTime();
//        long elapsed = end_time-start_time;
//        Log.d(TAG, "dft: elapsed time with DFT: " + elapsed/1000000);
        return series;
    }


    //MAKE FFT RECURSIVE UNTIL N=2
//    private Complex[] recursive_fft (short[] values, int N) {
//    }

    private Complex[] fft (short[] values, int N) {
        if (N==1) {
            return new Complex[] {new Complex(values[0], 0)};
        }
//        long start_time = System.nanoTime();
        Complex[] output = new Complex[N];

        short [] evens = new short[N/2];
        short[] odds = new short[N/2];
        for (int i = 0; i<N/2; i++) {
                evens[i] = values[i*2];
                odds[i] = values[i*2+1];
        }
        Complex [] FFT_evens = fft(evens, N/2);
        Complex [] FFT_odds = fft(odds, N/2);
//        Complex[] dft_evens = dft(evens, N/2);
//        Complex[] dft_odds = dft(odds, N/2);
        for (int k = 0; k<N/2; k++) {
            double root = -2*Math.PI*k/N;
            output [k] = Complex.add(FFT_evens[k], Complex.multiply(new Complex(Math.cos(root), Math.sin(root)), FFT_odds[k]));
            output [k+N/2] = Complex.subtract(FFT_evens[k], Complex.multiply(new Complex(Math.cos(root), Math.sin(root)), FFT_odds[k]));
//            output[k] = Complex.add(dft_evens[k], Complex.multiply(new Complex(Math.cos(2*Math.PI*k/N), Math.sin(-2*Math.PI*k/N)), dft_odds[k]));
        }
//        long end_time = System.nanoTime();
//        long elapsed = end_time - start_time;
//        Log.d(TAG, "fft: elapsed time with fft: " + elapsed / 1000000);
        return output;
    }

    private void analyse (Complex[] data, int N, int method, int interpolation) {
        double [] realValues = new double[N/2];
        double max = 0.0;
        int max_i = 0;
        int[] frequency_bins = new int[N/2];
        for (int i = 0; i<data.length/2; i++) {
            frequency_bins[i] = i * sampleRate / N;
            realValues[i] = Math.abs(data[i].getReal())/ N;
            if (realValues[i] > max) {
                max_i = i;
                max = realValues[i];
            }
        }
        double delta_m = 0;
        if (interpolation == GAUSSIAN) {
            if (max_i > 0) {
                delta_m = Math.log(realValues[max_i + 1] / realValues[max_i - 1]) / (2 * Math.log(Math.pow(realValues[max_i], 2) / (realValues[max_i + 1] * realValues[max_i - 1])));
            }
        }else if (interpolation == PARABOLIC){
            if (max_i > 0) {
                delta_m = (realValues[max_i + 1] - realValues[max_i - 1]) / (2 * (2 * realValues[max_i] - realValues[max_i - 1] - realValues[max_i + 1]));
            }
        }
        double frequency = ((double)sampleRate/N)*(max_i+delta_m);
//        Log.d(TAG, "analyze: thisvalueofmine max freq" + frequency_bins[max_i]);
        if (method == DFT) {
            export(frequency_bins, realValues, DFT);
            textView.setText(String.valueOf(frequency));
        } else if (method == FFT) {
            export(frequency_bins, realValues, FFT);
            textView2.setText(String.valueOf(frequency));
        }
    }

    private void export (int[] frequency_bins, double[] realValues, int method) {
        File file;
        File file2;
        if (method == FFT) {
            file = new File (this.getFilesDir() + "FFTfrequencies.txt");
            file2 = new File (this.getFilesDir() + "FFTvalues.txt");
        }else{
            file = new File (this.getFilesDir() + "DFTfrequencies.txt");
            file2 = new File (this.getFilesDir() + "DFTvalues.txt");
        }
        try {
            OutputStream os = new FileOutputStream(file);
            OutputStream os2 = new FileOutputStream(file2);
            try{
                for (int f : frequency_bins) {
                    os.write(String.valueOf(f).getBytes());
                    os.write("\n".getBytes());
                }

                for (double s : realValues) {
                    os2.write(String.valueOf(s).getBytes());
                    os2.write("\n".getBytes());
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}