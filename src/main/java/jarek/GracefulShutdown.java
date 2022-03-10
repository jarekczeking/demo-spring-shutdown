package jarek;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/*
Klasa wzorowana na org.springframework.boot.web.servlet.context.WebServerGracefulShutdownLifecycle.
Umożliwia odpalenie pewnego Runnable w ramach procesu graceful shutdown. Typowy przykład:

                gracefulShutdown.addRunnable("scheduler", (predicate) -> {
                    boolean result = scheduledTask.cancel(false);
                    taskScheduler.waitForTasksToFinish(predicate);
                });

Proces graceful shutdown zdefiniowany jest przez property springa:

server.shutdown=graceful
spring.lifecycle.timeout-per-shutdown-phase=35s

Te 35 sekund to czas pomiędzy rozpoczęciem procesu graceful shutdown, a rozpoczęciem procesu zamykania komponentów.
Zamykanie komponentów następuje przy SmartLifecycle z fazą Integer.MAX_VALUE-1 (patrz ApplicationState).

 */
@Component
public class GracefulShutdown implements SmartLifecycle {
    private static final Logger LOGGER = LoggerFactory.getLogger(GracefulShutdown.class);

    // Bean o wyższym phase może być zależny od beana o niższym phase, gdyż ten drugi będzie zamykany później.
    // Odwrotnie nie można, bo zepsuje się kolejność uruchamiania metody stop.
    // Spring sprawdza zależności pomiędzy beanami lifecycle zanim je zatrzyma (stop).
    private final ApplicationState applicationState;

    private volatile boolean running;

    private final List<RunnableWithDesc> runnables = new CopyOnWriteArrayList<>();

    public GracefulShutdown(ApplicationState applicationState) {
        this.applicationState = applicationState;
    }

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
        LOGGER.info("Starting graceful shutdown...");
        // Podobna koncepcja latcha funkcjonuje w org.springframework.context.support.DefaultLifecycleProcessor.LifecycleGroup.stop
        // My tutaj agregujemy jeden bean typu lifecycle do obsługi wielu procesów. Alternatywnie można do każdego procesu
        // typu graceful shutdown powołać osobnego beana z sufiksem Lifecycle.
        CountDownLatch latch = new CountDownLatch(runnables.size());
        for (RunnableWithDesc runnable : runnables) {
            new Thread(() -> {
                runnable.getRunnableUntilTrue().runUntilTrue(() -> !applicationState.isRunning());
                LOGGER.info("Runnable {} completed.", runnable.getDescription());
                latch.countDown();
            }).start();
        }
        new Thread(() -> {
            while (applicationState.isRunning() && latch.getCount() != 0) {
                Utils.sleepNoThrow(200);
            }
            LOGGER.info("Graceful shutdown complete, runnables completed in time: {} / {}", runnables.size() - latch.getCount(), runnables.size());
            callback.run();
        }).start();
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    public void addRunnable(String description, RunnableUntilTrue runnable) {
        this.runnables.add(new RunnableWithDesc(runnable, description));
    }

    private static class RunnableWithDesc {
        private RunnableUntilTrue runnableUntilTrue;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        private String description;

        public RunnableWithDesc(RunnableUntilTrue runnableUntilTrue, String description) {
            this.runnableUntilTrue = runnableUntilTrue;
            this.description = description;
        }

        public RunnableUntilTrue getRunnableUntilTrue() {
            return runnableUntilTrue;
        }

        public void setRunnableUntilTrue(RunnableUntilTrue runnableUntilTrue) {
            this.runnableUntilTrue = runnableUntilTrue;
        }
    }
}
