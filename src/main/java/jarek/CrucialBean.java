package jarek;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class CrucialBean extends AbstractHealthBean {

    @Autowired
    private ApplicationContext applicationContext;

    public CrucialBean() {
        super();
        log = LoggerFactory.getLogger(CrucialBean.class);
        log.info("constructed");
    }

    public void action() {
        confirmHealth();
        applicationContext.getBean(NecessaryBean.class).action();
        //necessaryBean.action();
    }

    @PreDestroy
    public void preDestroy() {
        log.info("predestroy");
    }
}
