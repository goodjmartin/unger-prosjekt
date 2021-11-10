package no.goodtech.vaadin.event.ui;

import no.goodtech.vaadin.event.model.EventType;

import java.util.Set;

public interface IEventListener {
	void eventTypesSelected(final Set<EventType> eventTypes);

	void changedBySelected(final Set<String> sourcesOfChange);
}
