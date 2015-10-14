package ch.fhnw.tvver;

import java.util.Arrays;

/**
 * Created by cansik on 14/10/15.
 */
public class FastSuliReceiver extends AbstractReceiver {
    /* Experimental threshold for detecting start tone. Should be adaptive. */
    private static final float START_THRESH = 0.1f;
    /* Threshold for detecting binary "one". */
    private static final float ONE_THRESH   = 0.5f;

    /* Idle / data state */
    private boolean       idle = true;
    /* Index for accumulating samples */
    private int           energyIdx;
    /* Energy accumulator */
    private final float[] energy = new float[9];
    /* Sample index into the current symbol */
    private int           sampleIdx;
    /* Symbol phase of start symbol */
    private int           symbolPhase;

    /**
     * Process one sample (power).
     *
     * @param sample The sample to process.
     */
    private void process(float sample) {
        final int symbolSz = (int) (samplingFrequency / SimpleAMSender.FREQ);
        symbolPhase        = symbolSz / 4;

		/* Wait for signal to rise above start threshold. */
        if(idle) {
            if(sample > START_THRESH) {
                sampleIdx = symbolPhase;
                idle     = false;
            }
        } else {
			/* Accumulate energy */
            energy[energyIdx] += sample;
			/* End of symbol? */
            if(++sampleIdx == symbolSz) {
				/* Advance to next symbol */
                sampleIdx = 0;
                energyIdx++;
				/* Enough data for a byte? */
                if(energyIdx == energy.length) {
					/*  Collect bits. */
                    int val = 0;
                    for(int i = 0; i < 8; i++)
						/* Use first symbol as reference value */
                        if(energy[i+1] > ONE_THRESH * energy[0])
                            val |= 1 << i;
                    addData((byte) val);
					/* Advance to next data byte */
                    energyIdx = 0;
                    sampleIdx = symbolPhase;
                    Arrays.fill(energy, 0f);
                    idle = true;
                }
            }
        }
    }



    /**
     * Process samples. Samples are squared (power).
     *
     * @param samples The samples to process.
     */
    @Override
    protected void process(float[] samples) {
        for(int i = 0; i < samples.length; i++)
            process(samples[i]*samples[i]);
    }
}
