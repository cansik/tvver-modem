package ch.fhnw.tvver;

import ch.fhnw.util.FloatList;

/**
 * Created by cansik on 03/11/15.
 */
public class VeasySender extends AbstractSender {

    public static final double PI2 = Math.PI * 2f;

    public static final float AMP = 1.0f;
    public static final float FREQ = 4000.0f;

    int symbolSize = -1;

    @Override
    public float[] synthesize(byte data) {
        symbolSize = (int) (samplingFrequency / FREQ);
        FloatList result = new FloatList();

        //a = 01100001 || 228 = 11100100

        //de-multiplexer
        for (int i = 0; i < 4; i++) {
            int d = (data << (6 - i * 2)) & 0xFF;
            d >>= 6;

            //NRZ Encoder
            int bit1 = (d & 0x1) == 0 ? -1 : 1;
            int bit2 = (d & 0x2) == 0 ? -1 : 1;

            result.addAll(createSymbol(d + 1, bit1, bit2));
        }

        return result.toArray();
    }

    private float[] createSymbol(int n, int Pi, int Pq) {
        final float[] result = new float[symbolSize];

        for (int i = 0; i < result.length; i++) {
            double Ac = AMP;
            double Ts = symbolSize;
            double Fc = FREQ;
            double t = i / Fc;

            double Ci = Ac * Math.cos(PI2 * Fc * i);
            double Cq = Ac * Math.cos(PI2 * Fc * i + Math.PI / 2f);
            //Math.sin(PI2 * Fc * i);

            double Sqpsk = Pi * Ci + Pq * Cq;

            result[i] = (float) Sqpsk;


            result[i] = (float) (Math.cos((PI2 * i) / Ts + (2f * n - 1f) * Math.PI / 4f));
        }

        return result;
    }
}
