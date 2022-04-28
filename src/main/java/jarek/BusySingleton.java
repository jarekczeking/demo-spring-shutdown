package jarek;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class BusySingleton {
    private static final Logger log = LoggerFactory.getLogger(BusySingleton.class);

    private volatile boolean busy;

    public void waitTillFree() {
        while (busy) {
            try {
                synchronized (this) {
                    wait(5000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Wait interrupted.");
            }
        }
    }

    @PostConstruct
    public void postConstruct() {
        new Thread(() -> {
            while (true) {
                synchronized (this) {
                    busy = !busy;
                    if (!busy) {
                        notifyAll();
                    }
                    log.info("Now I am " + (busy ? "" : "not ") + "busy");
                }
                Utils.sleepNoThrow(3000);
            }
        }).start();
    }

    @PreDestroy
    public void preDestroy() {
        if (busy) {
            throw new IllegalArgumentException("It was not nice.");
        }
    }
}
