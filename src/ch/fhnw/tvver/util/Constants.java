package ch.fhnw.tvver.util;

/**
 * Created by cansik on 14/10/15.
 */
/**
 * This interface defines several useful constants used throughout
 * the mobile client code for ultrasound detection.
 *
 * @author Tony Offer
 * @author Chris Palistrant
 * @version 1.0
 */
public interface Constants {
    // The sampling rate used for recording and used by the
    // ultrasound detection algorithm for various calculations.
    public static final float SAMPLING_RATE = 44100.0F;

    // A default target frequency for the ultrasound detection
    // algorithm.  The target frequency is the frequency that the
    // ultrasound detection algorithm looks for in the audio signal.
    public static final float TARGET_FREQUENCY = 21000.0F;

    // The block size used by the Goertzel algorithm.  This number
    // represents the amount of samples necessary to make a decision
    // as to the presence of the target frequency.
    public static final int N = 420;

    // The size, in bits, of the audio samples that will be recorded
    // while listening for ultrasound.  Note that reading from a
    // Java data line for the purposes of recording audio returns a
    // stream of bytes, which do not necessarily contain the same
    // number of bits as the samples themselves.
    public static final int SAMPLE_SIZE = 16;

    // The number of channels used for recording is not listed here
    // as a constant because different systems may support different
    // channels.

    // Whether or not the recorded samples are signed.
    public static final boolean SIGN = true;

    // Whether or not the samples employ big endian encoding.
    public static final boolean BIGEND = false;

    // The multicast group that the DatagramListener thread's socket
    // should join in order to receive datagram broadcasts.
    public static final String GROUPADDR = "235.255.255.254";

    // The port number on which the DatagramListener thread should
    // listen for datagram broadcasts.
    public static final int PORTNUM = 8019;

    // The maximum datagram message size that could be received by
    // the DatagramListener thread.  This essentially defines the
    // maximum size of room messages.
    public static final int MAXMSGSIZE = 1000;

    // The number of positive ultrasound detections in a row that
    // are required before a positive ultrasound identification can
    // be made.
    public static final int DEBOUNCE_VAL = 5;

    // The number of UltrasoundAnalyzer threads to use in the
    // UltrasoundListener object's thread pool.  This value can
    // be tweaked to improve performance since a greater number of
    // threads means more parallelism while too many threads means
    // wasted resources.  Peak performance is achieved when each
    // of the threads in the thread pool are always busy but the
    // UltrasoundListener object does not have to wait for any
    // thread to become available when it has data to analyze.  See
    // the UltrasoundListener class for more details.
    public static final int NUMTHREADS = 20;

    // The number of entries to keep in the recent history of room
    // determinations.  When ultrasound is detected for a certain
    // room, the room string will be placed in a history of this
    // length.  A larger history means that room determinations will
    // be more accurate, while a smaller history means that room
    // determinations will occur more quickly.
    public static final int HISTORY_SIZE = 20;

    // The average amount of time it takes for ultrasound to be
    // detected after receiving an 802.11 message (assuming the
    // ultrasound was generated concurrently with the 802.11).  This
    // value was determined experimentally.  It is in ms.
    public static final int AVG_DETECT_TIME = 410;

    // The maximum amount of time it takes for ultrasound to be
    // detected after receiving an 802.11 message (assuming the
    // ultrasound was generated concurrently with the 802.11).  This
    // value was determined experimentally.  It is in ms.
    public static final int MAX_DETECT_TIME = 839;

    // The number of received datagram messages to store while
    // waiting to receive ultrasound.
    public static final int ROOM_MSG_BUF = 10;

    // The number of room determinations for the same room that must
    // occur in a row before the confidence magnitude for that room is
    // boosted.  The idea here is to quickly increase confidence
    // that a device is in a given room if multiple room decisions
    // occur in a sequence for that room.
    public static final int SEQUENCE = 3;

    // The additional confidence magnitude boost that should be
    // given to a room if it has been decided upon at least SEQUENCE
    // times in a row.
    public static final int WEIGHT = 50;
}