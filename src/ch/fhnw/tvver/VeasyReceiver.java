package ch.fhnw.tvver;

import ch.fhnw.util.FloatList;

import static ch.fhnw.tvver.FastSuliReceiver.plotSamples;

/**
 * Created by cansik on 03/11/15.
 */
public class VeasyReceiver extends AbstractReceiver {
    public FloatList list = new FloatList();
    public FloatList list2 = new FloatList();

    public static final float THRESHOLD = 0.3f;

    int symbolSize = -1;

    float[] iBuffer;
    float[] qBuffer;

    int time = 0;


    void process_sample(float sample) {
        //modulate by positive and negative carrier wave
        iBuffer[time % symbolSize] = sample * (float) Math.cos((VeasySender.PI2 * (float) time) / symbolSize); //(float)Math.cos(VeasySender.PI2 * VeasySender.FREQ * (float)time);
        qBuffer[time % symbolSize] = sample * (float) Math.sin((VeasySender.PI2 * (float) time) / symbolSize); //(float)Math.cos(VeasySender.PI2 * VeasySender.FREQ * (float)time + Math.PI / 2f);
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

        list.add(setI);
        list2.add(setQ);
    }

    @Override
    protected void process(float[] samples) {
        //list.addAll(samples);

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

            //check if symbolisize is big enough
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

    protected float generateCarrierWave(int time, int pole) {
        double Ac = VeasySender.AMP;
        double Ts = symbolSize;
        double Fc = VeasySender.FREQ;
        double t = time / Fc;


        return (float) (pole * (Math.cos((VeasySender.PI2 * time) / Ts)));
    }

    @Override
    public final byte[] getAndClearData() {
        byte[] result = data.toArray();
        plotSamples("plot.data", list.toArray());
        plotSamples("plot2.data", list2.toArray());
        data.clear();
        return result;
    }
}
