package jarek;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {
    private static final Logger log = LoggerFactory.getLogger(AppConfiguration.class);

    @Bean
    public SmartLifecycle shutdownTaskWithHelper(BusySingleton busySingleton) {
        return new GracefulShutdownSmartLifecycle() {
            @Override
            public void shutdownTask() {
                log.info("another task");
                log.info("Let's wait until not busy");
                busySingleton.waitTillFree();
            }
        };
    }

}
