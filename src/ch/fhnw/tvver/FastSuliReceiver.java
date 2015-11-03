package ch.fhnw.tvver;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by cansik on 14/10/15.
 */
public class FastSuliReceiver extends AbstractReceiver {
    /* Experimental threshold for detecting start tone. Should be adaptive. */
    private static float START_THRESH = 0.15f;

    // treshold which defines the maximal difference for a preamble to match
    private static final float PREAMBLE_THRESH = 4.5f;

    /* Idle / data state */
    private boolean idle = true;

    private int sampleCount = 0;
    private int symbolCount = 0;

    private boolean readPreamble = false;

    private Map<Integer, float[]> preambles = new HashMap<>();
    private float[] buffer;

    private int dataBuffer = 0;

    public ArrayList<Float> floatList = new ArrayList<>();

    int symbolSz = 0;

    /**
     * Process one sample (power).
     *
     * @param sample The sample to process.
     */
    private void process(float sample) {
        //create preamble list first time (initialize)
        if(preambles.isEmpty())
        {
            for(int i = 0; i < 4; i++)
                preambles.put(i, new float[symbolSz]);

            buffer = new float[symbolSz];
        }

        floatList.add(sample);

        //wait for signal that is strong enough to be data
        if(idle) {
            if (Math.abs(sample) > START_THRESH) {
                idle = false;
                readPreamble = true;
                sampleCount = 0;
            }
        }
        else
        {
            //first read preamble
            if (readPreamble) {

                //read samples for symbol
                //floatList.add(sample);
                preambles.get(symbolCount)[sampleCount] = sample;

                sampleCount++;
                if (sampleCount == symbolSz) {
                    //new symbol read
                    symbolCount++;
                    //floatList.add(1f); //mark in plot
                    //System.out.println("Preamble Symbol: " + symbolCount);
                    sampleCount = 0;

                    if (symbolCount == 4) {
                        symbolCount = 0;
                        readPreamble = false;
                    }
                }
            }
            else
            {
                //read real data

                //get 16 samples and then compare with preambles
                buffer[sampleCount] = sample;

                sampleCount++;
                if (sampleCount == symbolSz) {
                    symbolCount++;

                    float minDiff = Float.MAX_VALUE;
                    int bestPreamble = -1;

                    //compare with preambles
                    for(int i = 0; i < preambles.size(); i++)
                    {
                        //System.out.print("Symbol " + symbolCount + " | " + i + ": ");
                        float diff = calculateDifference(buffer, preambles.get(i));
                        //System.out.println(diff);

                        if (diff < minDiff)
                        {
                            bestPreamble = i;
                            minDiff = diff;
                        }
                    }

                    //check if preamble matches good enough
                    if(minDiff >= PREAMBLE_THRESH)
                    {
                        //no preamble detected
                        idle = true;
                        symbolCount = 0;
                        readPreamble = true;
                        dataBuffer = 0;

                        //System.out.println("Preamble difference was to big: " + minDiff);
                    }
                    else
                    {
                        //System.out.println("best preamble was: " + bestPreamble);

                        //add data to result
                        dataBuffer |= bestPreamble << ((symbolCount - 1) * 2);

                        //go to the next data
                        if(symbolCount == 4)
                        {
                            //System.out.println("received: " + (char)dataBuffer);

                            //add dataBuffer to final result
                            addData((byte)dataBuffer);

                            symbolCount = 0;
                            dataBuffer = 0;
                        }
                    }

                    sampleCount = 0;
                }
            }
        }
    }


    protected float calculateDifference(float[] list1, float[] list2)
    {
        float diff = 0;

        for(int i = 0; i < list1.length; i++)
        {
            diff += Math.abs(list1[i] - list2[i]);
        }

        return diff;
    }

    /**
     * Process samples. Samples are squared (power).
     *
     * @param samples The samples to process.
     */
    @Override
    protected void process(float[] samples) {

        symbolSz = (int) (samplingFrequency / SimpleAMSender.FREQ);
        float maxAmplitude = Float.MIN_VALUE;

        for (int i = 0; i < samples.length; i++) {
            maxAmplitude = Math.max(Math.abs(samples[i]), maxAmplitude);
        }

        //System.out.println("Max: " +  maxAmplitude);

        //only process if there's not only noise
        if(maxAmplitude > 0.1) {

            //Take max as amplitude point and start at this amplitude to track!
            float startPoint = (float)(Math.sin((FastSuliSender.PI2 * 0) / symbolSz + FastSuliSender.S_00) * maxAmplitude);
            START_THRESH = startPoint * 0.6f;

            for (int i = 0; i < samples.length; i++) {
                process(samples[i]);
            }
        } else {
            for (int i = 0; i < 128; i++)
                process(0.0f);
        }
    }

    public static void plotSamples(float[] samples)
    {
        StringBuilder b = new StringBuilder();
        for(int i = 0; i < samples.length; i++) {
            b.append(i + "," + (samples[i])+"\n");
        }

        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileOutputStream("plot.data", false));
            out.println(b.toString());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
