package no.goodtech.vaadin.tabs.status.model;

import java.util.Set;

import no.goodtech.persistence.entity.EntityStub;

import org.springframework.scheduling.support.CronTrigger;

/**
 * En forenklet utgave av {@link StatusIndicatorSubscription}
 */
public interface StatusIndicatorSubscriptionStub extends EntityStub<StatusIndicatorSubscription> {

	/**
	 * @return id
	 */
	String getId();
	
	/**
	 * @return beskrivelse
	 */
	String getDescription();

	/**
	 * @return e-post-adresser som skal få varsling
	 */
	Set<String> getEmailRecipients();
	
	/**
	 * @return e-post-adresser som skal få varsling (som kommmaseparert tekst)
	 */
	String getEmailRecipientsAsString();
	
	/**
	 * @return hvor langt tilbake i loggen jeg skal sjekke (i millisekunder). Standard = 1 døgn.
	 */
	long getMaxLogEntryAge();
	
	/**
	 * Innstillinger for når du blir varslet, bruker Spring sin {@link CronTrigger}
	 * @return en liste av tidspunkter på cron-format
	 * @see "http://en.wikipedia.org/wiki/Cron#Format"
	 */
	String getCronExpression();
}
