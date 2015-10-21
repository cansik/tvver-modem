package ch.fhnw.tvver;

import ch.fhnw.util.FloatList;

/**
 * Created by cansik on 14/10/15.
 */
public class FastSuliSender extends AbstractSender {
    private static final double PI2  = Math.PI * 2;
    /* Carrier frequency. */
    static final float  FREQ = 3000;

    /**
     * Create a wave with given amplitude.
     * @param amp Amplitude for this symbol.
     * @return Audio data for symbol.
     */
    private float[] symbol(float amp) {
        final int symbolSz = (int) (samplingFrequency / FREQ);
        final float[] result = new float[symbolSz];

        for(int i = 0; i < result.length; i++)
            result[i] = (float)(Math.sin((PI2 * i) / symbolSz)) * amp;

        return result;
    }

    /**
     * Create amplitude modulated wave for a given data byte.
     * @param b Data byte to encode.
     */
    @Override
    public float[] synthesize(byte data) {
        FloatList result = new FloatList();

		/* Send start bit. */
        result.addAll(symbol(1f));
		/* Send data bits. */
        for(int i = 0; i < 8; i++)
            result.addAll(symbol((data & (1 << i)) == 0 ? 0 : 0.9f));

        return result.toSimpleArray();
    }
}
