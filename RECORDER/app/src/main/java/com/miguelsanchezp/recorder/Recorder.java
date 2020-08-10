package com.miguelsanchezp.recorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static android.content.ContentValues.TAG;

public class Recorder {

    public Recorder() {
    }

    void record() {
        double duration = 10;
        int audioSource = AudioSource.MIC;
        int sampleRate = 44100;
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        Log.d(TAG, "record: min buffer size " + bufferSize);
        AudioRecord audioRecord = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, bufferSize);
        audioRecord.startRecording();
        short[] Buffer = new short[(int)(sampleRate*duration)];
        int vals = audioRecord.read(Buffer, 0, (int)(sampleRate*duration));
        audioRecord.stop();
        Log.d(TAG, "record: printed " + vals);
        export(Buffer);
        Log.d(TAG, "record: successfully exported");
        audioRecord.release();
    }

    void export(short[] Buffer) {
        File file = new File (MainActivity.pathname + "file.txt");
        try {
            OutputStream os = new FileOutputStream(file);
            try{
                for (short s : Buffer) {
                    os.write(String.valueOf(s).getBytes());
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
