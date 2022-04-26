package jarek;

import org.springframework.context.SmartLifecycle;

/*
This class allows to quickly add a shutdownTask to the moment where graceful shutdown begins.
This is the moment, where web server stops accepting requests, but all beans are still active.
 */
public abstract class GracefulShutdownSmartLifecycle implements SmartLifecycle {
    private volatile boolean running;

    public abstract void shutdownTask();

    @Override
    public void start() {
        this.running = true;
    }

    @Override
    public void stop() {
        throw new UnsupportedOperationException("Stop must not be invoked directly");
    }

    @Override
    public void stop(Runnable callback) {
        this.running = false;
        // Smart lifecycle stop method should not operate long, so usually activities should be done in a separate thread.
        new Thread(() -> {
            shutdownTask();
            callback.run();
        }, "cp-gracefulsh-" + this.getClass().getSimpleName()).start();
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }
}
