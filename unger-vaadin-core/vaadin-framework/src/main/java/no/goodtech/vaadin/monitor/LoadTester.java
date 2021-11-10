package no.goodtech.vaadin.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
@EnableScheduling
@Profile("LoadTester")
class LoadTester {

	private final Logger logger = LoggerFactory.getLogger(LoadTester.class);

	@SuppressWarnings("RedundantStringConstructorCall")
	@Scheduled(initialDelayString = "${loadTester.initialDelay}", fixedDelayString = "${loadTester.fixedDelay}")
	public void loadTester() {
		long now = System.currentTimeMillis();
		for (long count = 0; count < 2000000000; count++) {
			new String("Allocate some memory");
			new String("Allocate some more memory");
		}

		logger.debug(String.format("Load testing in %d millisecond", System.currentTimeMillis() - now));
	}

}
