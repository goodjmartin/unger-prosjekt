package no.goodtech.vaadin.tabs.status.common;

import no.cronus.common.date.DateTimeFactory;
import no.cronus.common.date.DateTimeUtils;
import no.goodtech.vaadin.tabs.status.Globals;
import no.goodtech.vaadin.tabs.status.model.StatusIndicatorSubscription;
import no.goodtech.vaadin.tabs.status.model.StatusIndicatorSubscriptionFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSender;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Kjører alle varslings-jobber på ønsket tidspunkt
 * Hver varslings-jobb får sin egen tråd
 * @author oystein
 */
public class StatusIndicatorSubscriptionScheduler {
	
	private static final String STATUSLOGGER = "PROSESS: STATUSVARSLING";

	private static final Logger LOGGER = LoggerFactory.getLogger(StatusIndicatorSubscriptionScheduler.class);

	private final MailSender mailSender;
	private final String url, from;
	private final Map<StatusIndicatorSubscription, ScheduledFuture<?>> tasks = new HashMap<StatusIndicatorSubscription, ScheduledFuture<?>>();
	private final boolean disabled;
	private TaskScheduler taskScheduler;

	/**
	 * Opprett objektet med alt det det trenger for å kjøre
	 * @param mailSender tjeneste for å sende mail
	 * @param url link til applikasjonen (blir skrevet i mail)
	 * @param from avsender for mailene som sendes ut
	 */
	public StatusIndicatorSubscriptionScheduler(MailSender mailSender, String url, String from, boolean disabled) {
		this.mailSender = mailSender;
		this.url = url;
		this.from = from;
		this.disabled = disabled;
	}

	/**
	 * Henter alle indikator-varslere fra databasen og programmerer dem til å kjøre på riktig tidspunkt.
	 * Om noen varslere kjørte fra før, vil disse bli stoppet 
	 */
	public void refresh() {
		for (StatusIndicatorSubscription subscription : tasks.keySet()) {
			tasks.get(subscription).cancel(false);
		}

		if (disabled) {
			LOGGER.warn("E-post-varsling er deaktivert");
		} else {
			for (StatusIndicatorSubscription subscription : new StatusIndicatorSubscriptionFinder().loadList()) {
				final String cronExpression = subscription.getCronExpression();
	        	final String subscriptionDescription = "Varsling til: " + subscription.getEmailRecipientsAsString();
				if (cronExpression != null) {		        
		        	final StatusIndicatorMailAlerter mailAlerter = new StatusIndicatorMailAlerter(subscription, mailSender, from, url, getStatusLogger());
		        	final CronTrigger trigger = new CronTrigger(cronExpression);
					final ScheduledFuture<?> task = getTaskScheduler().schedule(mailAlerter, trigger);
					final LocalDateTime nextRun = DateTimeFactory.dateTime(task.getDelay(TimeUnit.MILLISECONDS), ChronoUnit.MILLIS);
			        tasks.put(subscription, task);
					getStatusLogger().success(subscriptionDescription + " blir utført iht. flg. mønster: " + 
						cronExpression + ". Neste kjøring er: " + DateTimeUtils.format(nextRun, "yyyy-MM-dd HH:mm"));
				} else {
					getStatusLogger().warning(subscriptionDescription + " mangler kjøremønster; vil ikke starte denne");
				}
			}
		}
	}
	
	private IStatusLogger getStatusLogger() {
		return Globals.getStatusLoggerRepository().lookupStatusLogger(STATUSLOGGER);
	}

	private TaskScheduler getTaskScheduler() {
		if (taskScheduler == null) {
			ThreadPoolTaskScheduler threadPoolTaskScheduler= new ThreadPoolTaskScheduler();
			threadPoolTaskScheduler.initialize();
			taskScheduler = threadPoolTaskScheduler;
		}
		return taskScheduler;
	}
	
}
