package jarek;

import org.springframework.stereotype.Component;

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
