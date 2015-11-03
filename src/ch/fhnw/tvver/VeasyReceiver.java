package ch.fhnw.tvver;

import ch.fhnw.util.FloatList;

import static ch.fhnw.tvver.FastSuliReceiver.plotSamples;

/**
 * Created by cansik on 03/11/15.
 */
public class VeasyReceiver extends AbstractReceiver {
    public FloatList list = new FloatList();


    @Override
    protected void process(float[] samples) {
        list.addAll(samples);
    }

    @Override
    public final byte[] getAndClearData() {
        byte[] result = data.toArray();
        plotSamples(list.toArray());
        data.clear();
        return result;
    }
}
