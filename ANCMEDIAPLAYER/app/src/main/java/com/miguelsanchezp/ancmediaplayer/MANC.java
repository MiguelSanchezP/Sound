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
    private static final int sampleRate = 44100;
    private static final double time = 1;
    private static final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,100*44100, AudioTrack.MODE_STREAM);

    private static final Runnable generateFrequency = new Runnable() {
        @Override
        public void run() {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            while (MainActivity.MANCStatus) {
                short[] values = frequencyVals();
                audioTrack.write(values, 0, values.length);
            }
        }
    };

    private static final Runnable generateWhiteNoise = new Runnable() {
        @Override
        public void run() {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            while (MainActivity.WNStatus) {
                byte[] values = WhiteNoiseVals();
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static void playWhiteNoise () {
        audioTrack.play();
        audioTrack.setVolume(AudioTrack.getMaxVolume());
        new Thread(generateWhiteNoise);
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

    private static short[] frequencyVals() {
        short[] values = new short[(int)(sampleRate*time)];
        int frames_to_skip = (int)(sampleRate*phase/(2*Math.PI*frequency));
        for (int i = 0; i<(int)(sampleRate*time); i++) {
            if (oldPhase != phase) {
                if (i<=frames_to_skip) {
                    values[i] = (short)0;
                }
            }else{
                values[i] = (short) (Math.sin(i * frequency * 2 * Math.PI / sampleRate) * 32767);
            }
        }
        oldPhase = phase;
        return values;
    }

    private static byte[] WhiteNoiseVals() {
        byte[] values = new byte[(int)(sampleRate*time)];
        Random rand = new Random();
        rand.nextBytes(values);
        return values;
    }
}
