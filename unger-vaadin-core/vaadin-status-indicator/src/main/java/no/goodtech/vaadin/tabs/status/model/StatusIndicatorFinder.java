package no.goodtech.vaadin.tabs.status.model;

import no.goodtech.persistence.jpa.AbstractFinder;

public class StatusIndicatorFinder extends AbstractFinder<StatusIndicator, StatusIndicatorStub, StatusIndicatorFinder> {
	
	private static final long serialVersionUID = 1L;

	public StatusIndicatorFinder() {
		super("select si from StatusIndicator si", "si");
	}

	public StatusIndicatorFinder setOrderById(boolean ascending){
		addSortOrder(prefixWithAlias("id"), ascending);
		return this;
	}
}
