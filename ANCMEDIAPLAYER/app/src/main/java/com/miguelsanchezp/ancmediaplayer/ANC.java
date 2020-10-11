package com.miguelsanchezp.ancmediaplayer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class ANC {

    private static double duration = 0.1;
    private static int audioSource = MediaRecorder.AudioSource.MIC;
    private static int sampleRate = 44100;
    private static int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    public static short [] values;
    public static Complex [] fft_values;

    private static final int GAUSSIAN = 0;
    private static final int PARABOLIC = 1;

    private static final String TAG = "ANC";

    public static void performANC () {
        values = get_recording(duration, audioSource, sampleRate, channelConfig, audioFormat);
        fft_values = fft (values, 4096);
        analyse (fft_values, 4096, GAUSSIAN);
        Log.d(TAG, "performANC: " + values[254]);
    }

    private static short [] get_recording (double duration, int audioSource, int sampleRate, int channelConfig, int audioFormat) {
        int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        AudioRecord audioRecord = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, bufferSize);
        audioRecord.startRecording();
        short[] Buffer = new short[(int)(sampleRate*(duration))];
        audioRecord.read(Buffer, 0, (int)(sampleRate*(duration)));
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

    private static void analyse (Complex[] data, int N, int interpolation) {
        double[] realValues = new double[N / 2];
        double max = 0.0;
        int max_i = 0;
        for (int i = 0; i < data.length / 2; i++) {
            realValues[i] = Math.abs(data[i].getReal()) / N;
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
        } else if (interpolation == PARABOLIC) {
            if (max_i > 0) {
                delta_m = (realValues[max_i + 1] - realValues[max_i - 1]) / (2 * (2 * realValues[max_i] - realValues[max_i - 1] - realValues[max_i + 1]));
            }
        }
        double frequency = ((double) sampleRate / N) * (max_i + delta_m);
        Log.d(TAG, "performANC: frequency being: " + frequency);
    }
}
