package jarek;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class NecessaryBean extends AbstractHealthBean {

    public NecessaryBean() {
        super();
        log.info("constructed");
    }

    public void action() {
        confirmHealth();
    }
}
