package jarek;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Component
public class ApplicationState implements SmartLifecycle {
    private static final Logger log = LoggerFactory.getLogger(ApplicationState.class);

    private enum State {
        NOT_STARTED,
        STARTED,
        //CLOSING - można dodać w przyszłości, na podstawie eventu ContextClosedEvent; na ten moment nie było potrzeby
        //CLOSING_GRACEFULLY - można dodać na podstawie drugiej klasy SmartLifeCycle, z pustym getPhase
        STOPPED
    }

    private volatile State state = State.NOT_STARTED;

    @Override
    public void start() {
        this.state = State.STARTED;
    }

    @Override
    public void stop() {
        this.state = State.STOPPED;
        log.info("state changed to {}", this.state);
    }

    @Override
    public boolean isRunning() {
        return this.state == State.STARTED;
    }

    @Override
    public int getPhase() {
        // This phase is used after graceful shutdown is completed, like in org.springframework.boot.web.servlet.context.WebServerStartStopLifecycle
        return Integer.MAX_VALUE - 1;
    }

}
