package jarek;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class MBean {
    private static final Logger log = LoggerFactory.getLogger(MBean.class);

    @PreDestroy
    public void preDestroy() {
        log.info("long predestroy starts");
        Utils.busySleep(1500);
        log.info("long predestroy ends");
    }
}
