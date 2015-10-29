package ch.fhnw.tvver;

import ch.fhnw.util.FloatList;

/**
 * Created by cansik on 14/10/15.
 */
public class FastSuliSender extends AbstractSender {
    private static final double PI2  = Math.PI * 2;

    /*
    static final float S_00 = (float)(PI2 / 4 * 1);
    static final float S_01 = (float)(PI2 / 4 * 2);
    static final float S_10 = (float)(PI2 / 4 * 3);
    static final float S_11 = (float)(PI2 / 4 * 4);
    */

    static final float S_00 = (float)(Math.PI / 4);
    static final float S_01 = (float)(3*Math.PI / 4);
    static final float S_10 = (float)(-Math.PI / 4);
    static final float S_11 = (float)(-3*Math.PI / 4);

    /* Carrier frequency. */
    static final float  FREQ = 3000;

    /**
     * Create a wave with given amplitude.
     * @return Audio data for symbol.
     */
    private float[] symbol(float transition) {
        final int symbolSz = (int) (samplingFrequency / FREQ);
        final float[] result = new float[symbolSz];

        for(int i = 0; i < result.length; i++)
            result[i] = (float)(Math.sin((PI2 * i) / symbolSz + transition));

        return result;
    }

    /**
     * Create amplitude modulated wave for a given data byte.
     * @param b Data byte to encode.
     */
    @Override
    public float[] synthesize(byte data) {
        FloatList result = new FloatList();

		/* Send all possible values. */
        result.addAll(symbol(S_00));
        result.addAll(symbol(S_01));
        result.addAll(symbol(S_10));
        result.addAll(symbol(S_11));

		/* Send data bits (two in one) */
        /*
        for(int i = 0; i < 4; i++) {

            boolean firstBit = (data & (1 << i)) == 1;
            boolean secondBit = (data & (1 << i+1)) == 1;

            float nib = (float)Math.PI / 4;

            if(firstBit)
                nib *= 3;

            if(secondBit)
                nib *= -1;

            result.addAll(symbol(1f, nib));
        }
        */

        return result.toArray();
    }
}
