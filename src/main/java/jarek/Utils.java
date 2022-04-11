package jarek;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
    private static final Logger log = LoggerFactory.getLogger(Utils.class);
    private static byte[] busySleepBuf = new byte[100];

    public static void sleepNoThrow(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.info("Thread interrupted inside sleepNoThrow.", e);
            Thread.currentThread().interrupt();
        }
    }

    public static void busySleep(long millis) {
        long t0 = System.currentTimeMillis();
        while (System.currentTimeMillis() < t0 + millis) {
            for (int i = 0; i < busySleepBuf.length; i++) {
                long t = System.currentTimeMillis();
                busySleepBuf[i] = (byte) ((busySleepBuf[i] ^ ((t % 257) * i)) % 256);
            }
        }
    }
}
