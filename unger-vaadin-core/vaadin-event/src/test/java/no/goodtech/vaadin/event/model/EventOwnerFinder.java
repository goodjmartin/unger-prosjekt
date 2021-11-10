package no.goodtech.vaadin.event.model;

import no.goodtech.persistence.jpa.AbstractFinder;

public class EventOwnerFinder extends AbstractFinder<EventOwner, EventOwner, EventOwnerFinder> {

	public EventOwnerFinder() {
		super("select o from Owner o", "o");
	}

	public EventOwnerFinder setId(final String id) {
		addEqualFilter(prefixWithAlias("id"), id);
		return this;
	}

}
