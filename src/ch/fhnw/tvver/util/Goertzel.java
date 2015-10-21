package ch.fhnw.tvver.util;

/**
 * Created by cansik on 14/10/15.
 */
public class Goertzel implements Constants {

    private float sampling_rate;
    private float target_frequency;
    private int n;
    private double[] testData;

    private double coeff, Q1, Q2;
    private double sine, cosine;

    public boolean debug = false;

    /**
     * Default Constructor.  This constructor assumes that the smapling rate
     * 44.1kHz, and the frequency to search for is 21kHz
     */
    public Goertzel() {
        this(SAMPLING_RATE, TARGET_FREQUENCY, N, false);
    }

    /**
     * Constructor
     *
     * @param sampleRate is the sampling rate of the signal to be analyzed
     * @param targetFreq is the frequency that Goertzel will look for.
     * @param inN        is the block size to use with Goertzel
     * @param inDebug    indicates whether or not to turn debugging info on.
     */
    public Goertzel(float sampleRate, float targetFreq, int inN,
                    boolean inDebug) {
        sampling_rate = sampleRate;
        target_frequency = targetFreq;
        n = inN;
        debug = inDebug;
        testData = new double[n];

        // In case initGoertzel is not called, initialize the Goertzel
        // parameters with default precomputed values.
        // Below = 21000 Hz
        sine = 0.14904226617617444692935471527722;     // = sin(2*pi*200/420)
        cosine = -0.98883082622512854506974288293401;  // = cos(2*pi*200/420)
        // Below = 19005 Hz
        //sine = 0.42035722830956549189972281978021;   // = sin(2*pi*181/420)
        //cosine = -0.90735869456786483795065221200264;// = cos(2*pi*181/420)
        // Below = 22995 Hz
        //sine = -0.13423326581765547603701864151067;  // = sin(2*pi*219/420)
        //cosine = -0.99094976176793475524868671316836;// = cos(2*pi*219/420)
        coeff = 2 * cosine;
    }

    /**
     * Call this method after every block of N samples has been
     * processed.
     *
     * @return void
     */
    public void resetGoertzel() {
        Q2 = 0;
        Q1 = 0;
    }

    /**
     * Call this once, to precompute the constants.
     *
     * @return void
     */
    public void initGoertzel() {
        int k;
        float floatN;
        double omega;

        floatN = (float) n;
        k = (int) (0.5 + ((floatN * target_frequency) / sampling_rate));
        omega = (2.0 * Math.PI * k) / floatN;
        sine = Math.sin(omega);
        cosine = Math.cos(omega);
        coeff = 2.0 * cosine;

        resetGoertzel();
    }

    /**
     * Call this routine for every sample.
     *
     * @param sample is a double
     * @return void
     */
    public void processSample(double sample) {
        double Q0;

        Q0 = coeff * Q1 - Q2 + sample;
        Q2 = Q1;
        Q1 = Q0;
    }

    /**
     * Basic Goertzel.  Call this routine after every block to get the
     * complex result.
     *
     * @param parts has length two where the first item is the real
     *              part and the second item is the complex part.
     * @return double[] stores the values in the param
     */
    public double[] getRealImag(double[] parts) {
        parts[0] = (Q1 - Q2 * cosine);
        parts[1] = (Q2 * sine);
        return parts;
    }

    /**
     * Optimized Goertzel.  Call this after every block to get the
     * RELATIVE magnitude squared.
     *
     * @return double is the value of the relative mag squared.
     */
    public double getMagnitudeSquared() {
        return (Q1 * Q1 + Q2 * Q2 - Q1 * Q2 * coeff);
    }

    /**
     * End of Goertzel-specific code, the remainder is test code.
     */

    /**
     * Pass in some test data.
     */
    void signal(double[] frequency) {
        int x = n;
        if (frequency.length < n) {
            for (int i = frequency.length; i < x; i++)
                testData[i] = 0;
            x = frequency.length;
        }

        for (int index = 0; index < x; index++) {
            testData[index] = frequency[index];
        }
    }

    /**
     * Synthesize some test data at a given frequency.
     */
    void generate(double frequency) {
        double step;

        step = (frequency * ((2.0 * Math.PI) / sampling_rate));

	/* Generate the test data */
        for (int index = 0; index < n; index++) {
            testData[index] = (100.0 * Math.sin(index * step) + 100.0);
            if (debug) {
                System.out.println("generate.index " + index);
                System.out.println("generate.testData " + (int) testData[index]);
                System.out.println("generate.Step: " + step);
                System.out.println("generate.Sine " + Math.sin(index * step));
            }
        }
    }
}
