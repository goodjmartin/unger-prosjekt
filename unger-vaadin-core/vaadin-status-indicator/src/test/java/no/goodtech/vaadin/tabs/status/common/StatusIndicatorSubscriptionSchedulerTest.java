package no.goodtech.vaadin.tabs.status.common;

import no.goodtech.persistence.MainConfig;
import no.goodtech.vaadin.tabs.status.model.StatusIndicator;
import no.goodtech.vaadin.tabs.status.model.StatusIndicatorSubscription;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test av timer for varslings-tjenesten for status-indikatorer
 * @author oystein
 */
@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@Transactional
@ContextConfiguration(classes = MainConfig.class, locations={"classpath*:goodtech-server.xml"})
public class StatusIndicatorSubscriptionSchedulerTest {

//	private static final Logger logger = LoggerFactory.getLogger(StatusIndicatorSubscriptionSchedulerTest.class);
	private StatusIndicatorSubscriptionScheduler scheduler;

	/**
	 * Tester at man kan endre varslings-innstillinger
	 */
	@Test
	public void testReScheduling() {
		StatusIndicator indicator = new StatusIndicator("indicator-id", "indicator-name").save();
		StatusLogger statusLogger = new StatusLogger(indicator);
		statusLogger.warning("advarsel");
		

		StatusIndicatorSubscription subscription = new StatusIndicatorSubscription();
		subscription.setId("abonnement");
		subscription.addStatusIndicator(indicator);
		subscription.addEmailRecipient("oeystein.myhre@goodtech.no");
		subscription.setCronExpression("* 5 * * * *");
		subscription = subscription.save();
		
		scheduler.refresh();
		
		subscription.setCronExpression("* * 7 * * *");
		subscription.save();

		scheduler.refresh();
	}

	/**
	 * @param scheduler får denne av spring
	 */
	@Autowired
	public void setScheduler(StatusIndicatorSubscriptionScheduler scheduler) {
		this.scheduler = scheduler;
	}
}
