package no.goodtech.vaadin.tabs.status.model;

import no.goodtech.persistence.jpa.AbstractFinder;

/**
 * Bruk denne til å finne {@link StatusIndicatorSubscription} i databasen
 * @author oystein
 */
public class StatusIndicatorSubscriptionFinder 
extends AbstractFinder<StatusIndicatorSubscription, StatusIndicatorSubscriptionStub, StatusIndicatorSubscriptionFinder> {

	private static final long serialVersionUID = 1L;

	/**
	 * Opprett søke-objekt
	 */
	public StatusIndicatorSubscriptionFinder() {
        super("select sil from StatusIndicatorSubscription sil", "sil");
	}

}
