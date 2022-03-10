package jarek;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    public static void sleepNoThrow(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.info("Thread interrupted inside sleepNoThrow.", e);
            Thread.currentThread().interrupt();
        }
    }
}
