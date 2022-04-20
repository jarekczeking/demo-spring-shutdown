package jarek;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@RestController
public class HelloController implements SmartLifecycle {
	private static final Logger log = LoggerFactory.getLogger(HelloController.class);
	private volatile boolean backgroundTaskActive;
	private volatile boolean isStopped;

	@Autowired
	private ApplicationContext applicationContext;

	@GetMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}

	@GetMapping("/wait")
	public String wait(@RequestParam int seconds) {
		NecessaryBean necessaryBean = applicationContext.getBean(NecessaryBean.class);
		log.info("Waiting for {} seconds.", seconds);
		long t0 = System.currentTimeMillis();
		while (System.currentTimeMillis() - t0 < 1000L * seconds) {
			necessaryBean.action();
			Utils.busySleep(1000);
		}
		log.info("Waited.");
		return "ok";
	}

	@GetMapping("/starttask")
	public String startBackgroundTask() {
		// Do CrucialBean odwołujemy się nie wprost, ukrywając to przed springiem.
		// W praktyce do wielu beanów odwołujemy się nie wprost, np. adnotacją @Async.
		NecessaryBean necessaryBean = applicationContext.getBean(NecessaryBean.class);
		backgroundTaskActive = true;
		new Thread(() -> {
			while (backgroundTaskActive) {
				necessaryBean.action();
				Utils.busySleep(10);
			}
		}).start();
		return "task started";
	}

	@GetMapping("/stoptask")
	public String stopBackgroundTask() {
		backgroundTaskActive = false;
		return "task stopped";
	}

	@Override
	public void start() {

	}

	@GetMapping("/stop")
	public void stopEndpoint() {
		new Thread(() -> {
			System.exit(0);
		}).start();
	}

	@Override
	public boolean isRunning() {
		return !isStopped;
	}

	//@PostConstruct
	//public void postConstruct() {
	//	gracefulShutdown.addRunnable("background task", predicate -> {
	//		while (backgroundTaskActive) {
	//			Utils.sleepNoThrow(100);
	//		}
	//	});
	//}

	@Override
	public void stop(Runnable callback) {
		log.info("smart lifecycle stop");
		new Thread(() -> {
			isStopped = true;
			callback.run();
		}).start();
	}

	@Override
	public void stop() {
		throw new IllegalArgumentException("use stop(callback)");
	}

	@PreDestroy
	public void preDestroy() {
		log.info("predestroy");
	}
}