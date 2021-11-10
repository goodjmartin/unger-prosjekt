package no.goodtech.vaadin.tabs.status.model;

import java.util.Date;

import no.goodtech.persistence.MainConfig;
import no.goodtech.vaadin.tabs.status.common.StatusLogger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
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
public abstract class MailSenderTest {

	@Autowired
	private MailSender mailSender;

	/**
	 * Tester om mail-sende-funksjonen virker
	 */
	@Test
	public void testSendMail() {
		StatusIndicator indicator = new StatusIndicator("indicator-id", "indicator-name").save();
		StatusLogger logger = new StatusLogger(indicator);
		logger.success("success");

		StatusIndicatorSubscription subscription = new StatusIndicatorSubscription();
		subscription.addStatusIndicator(indicator);
		subscription.addEmailRecipient("to");
		subscription.save();

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo("oeystein.myhre@goodtech.no");
		message.setFrom("oystein.myhre@yahoo.com");
		final Date now = new Date();
		message.setSubject("test 3 " + now);
		message.setText(now.toString());
		mailSender.send(message); 
	}

}
