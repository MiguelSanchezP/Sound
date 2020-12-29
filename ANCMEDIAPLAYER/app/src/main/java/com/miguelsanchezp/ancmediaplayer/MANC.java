package com.miguelsanchezp.ancmediaplayer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class MANC {
    private static double frequency;
    private static double phase;
    private static final int sampleRate = 44100;
    private static final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, 8000, AudioTrack.MODE_STREAM);

    public static Runnable generateFrequency = new Runnable() {
        @Override
        public void run() {
//            byte[]
//            audioTrack.write()
        }
    };

    static void play () {
        audioTrack.play();
        new Thread(generateFrequency).start();
    }

    static void setFrequency (double frequency) {
        MANC.frequency = frequency;
    }

    static void setPhase (double phase) {
        MANC.phase = phase;
    }
}
