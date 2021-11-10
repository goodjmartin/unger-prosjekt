package no.goodtech.vaadin.tabs.status.model;

import no.goodtech.persistence.MainConfig;
import no.goodtech.vaadin.tabs.status.common.MyMailSender;
import no.goodtech.vaadin.tabs.status.common.StatusIndicatorMailAlerter;
import no.goodtech.vaadin.tabs.status.common.StatusLogger;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test av varslings-tjenesten for status-indikatorer
 * @author oystein
 */
@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@Transactional
@ContextConfiguration(classes = MainConfig.class, locations={"classpath*:goodtech-server.xml"})
public class StatusIndicatorMailAlerterTest {

	/**
	 * Tester om varslings-funksjonen sender mail når den skal
	 */
	@Test
	public void testSendMail() {
		StatusIndicator indicator = new StatusIndicator("indicator-id", "indicator-name").save();
		StatusLogger logger = new StatusLogger(indicator);
		logger.success("success");

		StatusIndicatorSubscription subscription = new StatusIndicatorSubscription();
		subscription.addStatusIndicator(indicator);
		subscription.addEmailRecipient("to");
		subscription = subscription.save();

		MyMailSender mailSender = new MyMailSender();
		StatusIndicatorMailAlerter alerter = new StatusIndicatorMailAlerter(subscription, mailSender, "from", "url", logger);

		Assert.assertFalse(mailSender.isMailSent());
		alerter.run();
		Assert.assertFalse(mailSender.isMailSent()); //ingen mail skal sendes fordi ingen advarsler er logget

		//logger en advarsel => vil gjør at mail blir sendt neste gang man skal rapportere
		logger.warning("advarsel");
		alerter.run();
		Assert.assertTrue(mailSender.isMailSent());
	}

}
