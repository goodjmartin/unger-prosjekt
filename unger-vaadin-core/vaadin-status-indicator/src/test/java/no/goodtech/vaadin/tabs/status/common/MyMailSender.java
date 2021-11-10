package no.goodtech.vaadin.tabs.status.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * Brukes til å teste om applikasjonen sende mail som den skal
 */
public class MyMailSender implements MailSender {

	private static final Logger logger = LoggerFactory.getLogger(MyMailSender.class);
	private boolean mailSent = false;

	@Override
	public void send(SimpleMailMessage[] simpleMessages) throws MailException {
	}

	@Override
	public void send(SimpleMailMessage simpleMessage) throws MailException {
		mailSent = true;
		logger.debug("mail sendt til " + simpleMessage.getTo() + ": " + simpleMessage.getSubject());
	}

	/**
	 * @return har fått beskjed om å sende mail
	 */
	public boolean isMailSent() {
		return mailSent;
	}
}