package com.miguelsanchezp.ancmediaplayer;

public class MANC {
    private static double frequency;
    private static double phase;
    private static final int sampleRate = 44100;

    public static Runnable generateFrequency = new Runnable() {
        @Override
        public void run() {
//            byte[]
        }
    };

    static void play () {
    }

    static void setFrequency (double frequency) {
        MANC.frequency = frequency;
    }

    static void setPhase (double phase) {
        MANC.phase = phase;
    }
}
