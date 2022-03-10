package jarek;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
public class HelloController {
	private static final Logger log = LoggerFactory.getLogger(HelloController.class);
	private volatile boolean backgroundTaskActive;

	@Autowired
	private GracefulShutdown gracefulShutdown;

	@GetMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}

	@GetMapping("/wait")
	public String wait(@RequestParam int seconds) {
		log.info("Waiting for {} seconds.", seconds);
		Utils.sleepNoThrow(1000L * seconds);
		log.info("Waited.");
		return "ok";
	}

	@GetMapping("/starttask")
	public String startBackgroundTask() {
		backgroundTaskActive = true;
		return "task started";
	}

	@GetMapping("/stoptask")
	public String stopBackgroundTask() {
		backgroundTaskActive = false;
		return "task stopped";
	}

	@GetMapping("/stop")
	public void stop() {
		new Thread(() -> {
			System.exit(0);
		}).start();
	}

	@PostConstruct
	public void postConstruct() {
		gracefulShutdown.addRunnable("background task", predicate -> {
			while (backgroundTaskActive) {
				Utils.sleepNoThrow(100);
			}
		});
	}
}