package ch.fhnw.tvver;

import ch.fhnw.util.FloatList;

import java.io.File;

import static ch.fhnw.tvver.FastSuliReceiver.plotSamples;

/**
 * Created by cansik on 03/11/15.
 */
public class VeasyReceiver extends AbstractReceiver {
    public FloatList list = new FloatList();
    public FloatList list2 = new FloatList();
    public FloatList list3 = new FloatList();
    public FloatList list4 = new FloatList();

    public static final float THRESHOLD = 0.3f;

    int symbolSize = -1;

    float[] iBuffer;
    float[] qBuffer;

    int time = 0;


    void process_sample(float sample) {
        //modulate by positive and negative carrier wave
        iBuffer[time % symbolSize] = sample * (float) Math.cos(VeasySender.PI2 * VeasySender.FREQ * time);
        qBuffer[time % symbolSize] = sample * -(float) Math.cos(VeasySender.PI2 * VeasySender.FREQ * time + Math.PI / 2f);

        //list.add(iBuffer[time % symbolSize]);
        //list2.add(qBuffer[time % symbolSize]);
        //list3.add();
        //list4.add();
    }

    void make_desicion() {
        float setI = sumArray(iBuffer);
        float setQ = sumArray(qBuffer);

        if (Math.abs(setI) > THRESHOLD || Math.abs(setQ) > THRESHOLD) {
            int bit = 0;

            //desicion machine
            if (setI > 0 && setQ > 0)
                bit = 0;

            if (setI > 0 && setQ < 0)
                bit = 1;

            if (setI < 0 && setQ > 0)
                bit = 2;

            if (setI < 0 && setQ < 0)
                bit = 3;

            System.out.println("I: " + setI + "\t|\tQ: " + setQ + "\t|\tbit: " + bit);
        }

        //list.add(setI);
        //list2.add(setQ);
    }

    @Override
    protected void process(float[] samples) {
        list.addAll(samples);

        symbolSize = (int) (samplingFrequency / VeasySender.FREQ);

        //first init
        if (iBuffer == null) {
            iBuffer = new float[symbolSize];
            qBuffer = new float[symbolSize];
        }

        for (int i = 0; i < samples.length; i++) {
            process_sample(samples[i]);

            //update time
            time++;

            //check if symbolesize is big enough
            if (time % symbolSize == 0) {
                make_desicion();
            }
        }
    }

    float sumArray(float[] arr) {
        float sum = 0f;
        for (int i = 0; i < arr.length; i++)
            sum += arr[i];

        return sum;
    }

    /*protected float generateCarrierWave(int time) {
        double Ac = AMP/2f;
        double Ts = symbolSize;
        double Fc = FREQ;
        double t = i / samplingFrequency;

        double Ci = Ac * Math.cos(PI2 * Fc * t);
        double Cq = Ac * Math.sin(PI2 * Fc * t);

        return Ci + Cq;
    }*/

    @Override
    public final byte[] getAndClearData() {
        byte[] result = data.toArray();
        cleanupPlots();
        plotSamples("plot1.data", list.toArray());
        //plotSamples("plot2.data", list2.toArray());
        //plotSamples("plot3.data", list3.toArray());
        //plotSamples("plot4.data", list4.toArray());
        data.clear();
        return result;
    }

    public static void cleanupPlots() {
        File folder = new File(".");
        File[] listOfFiles = folder.listFiles();
        for (File f : listOfFiles) {
            if (f.getName().matches("plot\\d+\\.data")) {
                System.out.println("deleting " + f.getName() + ": " + f.delete());
            }
        }
    }
}
