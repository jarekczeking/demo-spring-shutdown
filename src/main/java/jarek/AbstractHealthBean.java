package jarek;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;

public abstract class AbstractHealthBean {
    protected Logger log = null;
    private volatile boolean healthy;

    public AbstractHealthBean() {
        log = LoggerFactory.getLogger(this.getClass());
        healthy = true;
    }

    public void confirmHealth() {
        if (healthy) {
            log.info("relax");
        } else {
            log.warn("i'm not healthy :(");
        }
    }

    @PreDestroy
    public void preDestroy() {
        log.info("predestroy");
        healthy = false;
    }

}
