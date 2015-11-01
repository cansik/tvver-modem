package ch.fhnw.tvver;

import ch.fhnw.util.FloatList;

/**
 * Created by cansik on 14/10/15.
 */
public class FastSuliSender extends AbstractSender {
    public static final double PI2  = Math.PI * 2;

    public static final float S_00 = (float)(Math.PI / 4);
    public static final float S_01 = (float)(3*Math.PI / 4);
    public static final float S_10 = (float)(-Math.PI / 4);
    public static final float S_11 = (float)(-3*Math.PI / 4);

    static final float[] SYMBOLS = new float[] {
            S_00,
            S_01,
            S_10,
            S_11
    };

    static boolean sendPreamble = true;

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
        {
            result[i] = (float)(Math.sin((PI2 * i) / symbolSz + transition));
        }

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

        if(sendPreamble) {
            result.addAll(symbol(S_00));
            result.addAll(symbol(S_01));
            result.addAll(symbol(S_10));
            result.addAll(symbol(S_11));
            sendPreamble = false;
        }


        //sample data (a = 01100001)
        /*
        result.addAll(symbol(S_01));
        result.addAll(symbol(S_00));
        result.addAll(symbol(S_10));
        result.addAll(symbol(S_01));
        */

		/* Send data bits (two in one) */
        for(int i = 0; i < 4; i++) {
            int d = (data << (6-i*2)) & 0xFF;
            d >>= 6;

            result.addAll(symbol(SYMBOLS[d]));
        }

        return result.toArray();
    }
}
