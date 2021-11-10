package no.goodtech.vaadin.event.model;

import no.goodtech.persistence.jpa.AbstractFinder;

public class EventTypeFinder extends AbstractFinder<EventType, EventType, EventTypeFinder> {

	public EventTypeFinder() {
		super("select et from EventType et", "et");
	}

	/**
	 * Filtrer på ownerClass til hendelsestypen
	 *
	 * @param ownerClass eier-klassen til hendelsestypen
	 * @return finder med filter på ownerClass satt
	 */
	public EventTypeFinder setOwnerClass(final Class<?> ownerClass) {
		if (ownerClass == null) {
			addNullFilter(prefixWithAlias("ownerClass"));
		} else {
			addEqualFilter(prefixWithAlias("ownerClass"), ownerClass);
		}
		return this;
	}

	/**
	 * Filtrer på ID til hendelsestypen
	 *
	 * @param id ID til hendelsestypen
	 * @return finder med equalFilter på ID satt
	 */
	public EventTypeFinder setId(final String id) {
		if (id == null) {
			addNullFilter(prefixWithAlias("id"));
		} else {
			addEqualFilter(prefixWithAlias("id"), id);
		}
		return this;
	}

}