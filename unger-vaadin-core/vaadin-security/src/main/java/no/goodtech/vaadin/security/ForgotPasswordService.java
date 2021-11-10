package no.goodtech.vaadin.security;

import java.util.Random;

import no.goodtech.vaadin.security.model.User;
import no.goodtech.vaadin.security.model.UserFinder;
import no.goodtech.vaadin.security.ui.Texts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class ForgotPasswordService {

	private static Logger LOGGER = LoggerFactory.getLogger(ForgotPasswordService.class);
	private final MailSender mailSender;
	private final boolean disabled;
	private final String from;

	public ForgotPasswordService(MailSender mailSender, String smtpHost, String from, boolean disabled) {
		this.mailSender = mailSender;
		this.from = from;
		this.disabled = disabled;
		if (disabled)
			LOGGER.warn("Service is disabled");
		else
			LOGGER.info("Will send new password to forgetful users through " + smtpHost);
	}

	public String resetPassword(String userId) {
		User user = new UserFinder().setId(userId).find();
		if (user == null)
			return String.format(Texts.get("forgotPasswordService.userNotFound"), userId);
		
		if (user.getEmail() == null || user.getEmail().trim().length() == 0)
			return String.format(Texts.get("forgotPasswordService.noEmail"), userId);
		
		final String password = generatePassword();
		
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(user.getEmail());
		message.setFrom(from);
		message.setSubject(Texts.get("forgotPasswordService.mailSubject"));
		message.setText(String.format(Texts.get("forgotPasswordService.mailContent"), password));
		mailSender.send(message);

		user.setPassword(MD5Hash.convert(password));
		user.setLoginFailures(0);
		user = user.save();
		LOGGER.info(String.format("Reset password for '%s' and sent new password to %s", user.getId(), user.getEmail()));

		return null;
	}

	/**
	 * Snarfed from http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
	 */
	private String generatePassword() {
		final String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random random = new Random();
		StringBuilder stringBuilder = new StringBuilder(8);

		for (int i = 0; i < 8; i++)
			stringBuilder.append(characters.charAt(random.nextInt(characters.length())));
		
		return stringBuilder.toString();
	}
	
	public boolean isAvailable() {
		return !disabled;
	}
}

