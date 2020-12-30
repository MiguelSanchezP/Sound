package com.miguelsanchezp.ancmediaplayer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class MANC {
    private static double frequency;
    private static double phase;
    private static final int sampleRate = 44100;
    private static final double time = 1;
    private static final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, 100*AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT), AudioTrack.MODE_STREAM);

    private static final Runnable generateFrequency = new Runnable() {
        @Override
        public void run() {
            short[] values = new short[(int)(sampleRate*time)];
            for (int i = 0; i<(int)(sampleRate*time); i++) {
//                values[i] = (short)(Math.sin(i*frequency*2*Math.PI/sampleRate)*32767);
                values[i] = (short)(32767);
            }
            while (true) {
                audioTrack.write(values, 0, values.length);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static void play () {
        audioTrack.play();
        audioTrack.setVolume(AudioTrack.getMaxVolume());
        new Thread(generateFrequency).start();
    }

    static void stop () {
        audioTrack.stop();
    }
    static void setFrequency (double frequency) {
        MANC.frequency = frequency;
    }

    static void setPhase (double phase) {
        MANC.phase = phase;
    }
}
