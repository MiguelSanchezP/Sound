package com.miguelsanchezp.ancmediaplayer;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.M)
public class ANC {

    private static final double duration = 0.05; //0.1;
    private static final int audioSource = MediaRecorder.AudioSource.MIC;
    private static final int sampleRate = 22050; //44100;
    private static final int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private static final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private static final int N = 1024; //4096;
    private static final double durationANC = 0.2; //1;

    private static final int GAUSSIAN = 0;
    private static final int PARABOLIC = 1;

    private static final AudioRecord audioRecord = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat));
    private static final short[] Buffer = new short[(int)(sampleRate*(duration))];

    private static final AudioTrack track = new AudioTrack.Builder().setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()).setAudioFormat(new AudioFormat.Builder().setEncoding(audioFormat).setSampleRate(sampleRate).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build()).setBufferSizeInBytes(100*AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)).build();

    private static long initialtime;
    private static long finaltime;
    private static ArrayList<Long> analysisTimes = new ArrayList<>();
    private static ArrayList<Long> trackTimes = new ArrayList<>();

    public static void performANC () {
        track.setVolume(AudioTrack.getMaxVolume());
        while (MainActivity.ANCStatus) {
            short[] values = get_recording(duration, audioSource, sampleRate, channelConfig, audioFormat);
            Complex[] fft_values = fft(values, N);
            initialtime = System.nanoTime();
            double [] analysedData = analyse(fft_values, N, GAUSSIAN);
            finaltime = System.nanoTime();
//            analysisTimes.add(finaltime-initialtime);
//            initialtime = System.nanoTime();
            play (generateFrequency (durationANC, sampleRate, analysedData[0], analysedData[1]));
        }
        audioRecord.stop();
        audioRecord.release();
    }

    private static short [] get_recording (double duration, int audioSource, int sampleRate, int channelConfig, int audioFormat) {
        audioRecord.startRecording();
        audioRecord.read(Buffer, 0, (int)(sampleRate*duration)); //samplerate*duration
        return Buffer;
    }

    private static Complex[] fft (short[] values, int N) {
        if (N==1) {
            return new Complex[] {new Complex(values[0], 0)};
        }
        Complex[] output = new Complex[N];

        short [] evens = new short[N/2];
        short[] odds = new short[N/2];
        for (int i = 0; i<N/2; i++) {
            evens[i] = values[i*2];
            odds[i] = values[i*2+1];
        }
        Complex [] FFT_evens = fft(evens, N/2);
        Complex [] FFT_odds = fft(odds, N/2);
        for (int k = 0; k<N/2; k++) {
            double root = -2*Math.PI*k/N;
            output [k] = Complex.add(FFT_evens[k], Complex.multiply(new Complex(Math.cos(root), Math.sin(root)), FFT_odds[k]));
            output [k+N/2] = Complex.subtract(FFT_evens[k], Complex.multiply(new Complex(Math.cos(root), Math.sin(root)), FFT_odds[k]));
        }
        return output;
    }

    private static double [] analyse (Complex[] data, int N, int interpolation) {
        double[] realValues = new double[N / 2];
        double max = 0.0;
        int max_i = 0;
        double imaginary = 0.0;
        for (int i = 0; i < data.length / 2; i++) {
            realValues[i] = Math.abs(data[i].getReal()) / N;
            if (realValues[i] > max) {
                max_i = i;
                max = realValues[i];
                imaginary = Math.abs(data[i].getImaginary()/N);
            }
        }
        double delta_m = 0;
        if (interpolation == GAUSSIAN) {
            if (max_i > 0) {
                delta_m = Math.log(realValues[max_i + 1] / realValues[max_i - 1]) / (2 * Math.log(Math.pow(realValues[max_i], 2) / (realValues[max_i + 1] * realValues[max_i - 1])));
            }
        } else if (interpolation == PARABOLIC) {
            if (max_i > 0) {
                delta_m = (realValues[max_i + 1] - realValues[max_i - 1]) / (2 * (2 * realValues[max_i] - realValues[max_i - 1] - realValues[max_i + 1]));
            }
        }
        double frequency = ((double) sampleRate / N) * (max_i + delta_m);
        return new double [] {frequency, Math.atan(imaginary/frequency)};
    }

    private static short [] generateFrequency (double duration, double sampleRate, double frequency, double phase) {
        short [] ANCVals = new short [(int)(duration*sampleRate)];
        phase = phase+(2*Math.PI*frequency*15.6838*((finaltime-initialtime)/1000000000.0)); //according to the mean provided by correlation.py
        for (int i = 0; i<sampleRate*duration; i++) {
            ANCVals[i] = (short)(Math.sin(i*frequency*2*Math.PI/sampleRate)*32767 + phase);
        }
        return ANCVals;
    }

    private static void play (short [] ANCVals) {
        track.write(ANCVals, 0, ANCVals.length);
//        finaltime = System.nanoTime();
//        trackTimes.add(finaltime-initialtime);
        track.play();
    }

    public static ArrayList<Long> getAnalysisTimes () {
        return analysisTimes;
    }
    public static ArrayList<Long> getTrackTimes () {
        return trackTimes;
    }
}
