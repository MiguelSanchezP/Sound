package com.miguelsanchezp.ancmediaplayer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Random;

public class MANC {
    private static double frequency;
    private static double phase;
    private static double oldPhase = 0;
    private static final int sampleRate = 22050;
    private static final double time = 0.25;
    private static final int bufferSize = (int)(sampleRate*time);
    private static final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,bufferSize, AudioTrack.MODE_STREAM);
    private static final MainActivity ma = new MainActivity();

    private static final Runnable generateFrequency = new Runnable() {
        @Override
        public void run() {
//            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            while (MainActivity.MANCStatus) {
                short[] values = frequencyVals();
//                short framesToSkip = (short)(sampleRate*phase/(Math.PI*frequency));
                audioTrack.write(values, 0, values.length);
                if (MainActivity.automaticSisChecked) {
                    ma.automaticPhaseChange();
//                    phase = phase+=0.01*Math.PI;
                }
            }
        }
    };

    private static final Runnable generateWhiteNoise = new Runnable() {
        @Override
        public void run() {
//            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            while (MainActivity.WNStatus) {
                byte[] values = WhiteNoiseVals();
                audioTrack.write(values, 0, values.length);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static void play () {
        audioTrack.play();
//        audioTrack.setVolume(AudioTrack.getMaxVolume());
        new Thread(generateFrequency).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static void playWhiteNoise () {
        audioTrack.play();
//        audioTrack.setVolume(AudioTrack.getMaxVolume());
        new Thread(generateWhiteNoise).start();
    }

    static void setFrequency (double frequency) {
        MANC.frequency = frequency;
    }

    static void setPhase (double phase) {
        MANC.phase = phase;
    }

    static double getPhase () {
        return phase;
    }

    private static short[] frequencyVals() {
        short[] values = new short[(int)(sampleRate*time)];
        int frames_to_skip = 0;
        if (oldPhase!=phase) {
            frames_to_skip = (int)(sampleRate*Math.abs(oldPhase-phase)/(Math.PI*frequency));
            oldPhase = phase;
        }
        for (int i = frames_to_skip; i<(int)(sampleRate*time); i++) {
            values[i] = (short) (Math.sin(i * frequency * 2 * Math.PI / sampleRate) * 32767);
        }
        return values;
    }

    private static byte[] WhiteNoiseVals() {
        byte[] values = new byte[(int)(sampleRate*time)];
        Random rand = new Random();
        rand.nextBytes(values);
        return values;
    }
}
