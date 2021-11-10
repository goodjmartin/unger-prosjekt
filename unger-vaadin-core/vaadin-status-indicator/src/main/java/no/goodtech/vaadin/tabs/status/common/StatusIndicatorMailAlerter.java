package no.goodtech.vaadin.tabs.status.common;

import no.cronus.common.date.DateTimeFactory;
import no.cronus.common.date.DateTimeUtils;
import no.goodtech.vaadin.tabs.status.model.*;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.TimerTask;

/**
 * Sender mail iht. innstillinger angitt i {@link StatusIndicatorSubscription}
 *
 * @author oystein
 */
public class StatusIndicatorMailAlerter extends TimerTask {

	private final IStatusLogger statusLogger;
	private final StatusIndicatorSubscription subscription;
	private final MailSender mailSender;
	private final String url;
	private final String from;

	/**
	 * Opprett en mail-sendings-tjeneste iht. angitte innstillinger
	 *
	 * @param alerter    varslings-innstillinger
	 * @param mailSender tjeneste for å sende mail
	 * @param from       avsender
	 * @param url        link til applikasjonen
	 */
	public StatusIndicatorMailAlerter(final StatusIndicatorSubscription alerter, final MailSender mailSender, final String from, final String url, final IStatusLogger statusLogger) {
		this.subscription = alerter;
		this.mailSender = mailSender;
		this.from = from;
		this.url = url;
		this.statusLogger = statusLogger;
	}

	/**
	 * For testing
	 *
	 * @param args ikke i bruk
	 */
	public static void main(String[] args) {
		//TODO: Wire opp med Spring
		StatusIndicator indicator = new StatusIndicator();
		StatusLogger logger = new StatusLogger(indicator);
		logger.warning("advarsel");

		StatusIndicatorSubscription alerter = new StatusIndicatorSubscription();
		alerter.addEmailRecipient("oeystein.myhre@goodtech.no");
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("mta-01.zeus.no");
		new StatusIndicatorMailAlerter(alerter, mailSender, "oeystein.myhre@goodtech.no", "http://www.goodtech.no/", null).run();
	}

	@Override
	public void run() {
		try {
			boolean anythingToReport = false;
			if (subscription.getEmailRecipients().size() == 0) {
				statusLogger.warning(subscription + " mangler e-post-mottakere, mail ble ikke sendt");
			} else if (subscription.getStatusIndicators().size() == 0) {
				statusLogger.warning(subscription + " mangler status-indikatorer, mail ble ikke sendt");
			} else {
				final LocalDateTime fromDate = DateTimeFactory.dateTime(-subscription.getMaxLogEntryAge(), ChronoUnit.MILLIS);
				StringBuilder content = new StringBuilder();
				content.append("Her er feilmeldinger rapportert siden ");
				content.append(DateTimeUtils.format(fromDate, "yyyy-MM-dd HH:mm"));
				content.append(". For mer info, sjekk ");
				content.append(url);
				content.append("\n\n");
				for (StatusIndicatorStub statusIndicator : subscription.getStatusIndicators()) {
					final StatusLogEntryFinder finder = new StatusLogEntryFinder().setStatusIndicator(statusIndicator).
							setStateTypes(StateType.WARNING, StateType.FAILURE).setCreatedAfter(fromDate);
					List<StatusLogEntryStub> statusLogEntries = finder.orderByPk(false).list();
					if (statusLogEntries.size() > 0) {
						anythingToReport = true;
						content.append("Status-indikator: ");
						content.append(statusIndicator.getName());
						content.append("\n");
						for (StatusLogEntryStub logEntry : statusLogEntries) {
							content.append(printLogEntry(logEntry));
						}
						content.append("\n");
					}
				}

				if (anythingToReport) {
					sendMail(subscription.getId(), content.toString());
				}
			}
		} catch (RuntimeException e) {
			statusLogger.failure("Det oppsto en uventet feil ved varsling", e);
		}
	}

	private void sendMail(final String subscriptionId, final String content) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(subscription.getEmailRecipientsAsString().split(","));
		message.setFrom(from);
		message.setSubject("Status-indikator-rapport for: " + subscriptionId);
		message.setText(content);
		if (mailSender == null) {
			throw new IllegalStateException("Får ikke til å sende mail fordi jeg mangler en mail-sende-tjeneste");
		}

		mailSender.send(message);
	}

	private String printLogEntry(StatusLogEntryStub logEntry) {
		return String.valueOf(logEntry.getStateType()) +
				": " +
				logEntry.getCreated() +
				": " +
				logEntry.getMessage() +
				"\n";
	}
}