package no.goodtech.vaadin.event.model;


import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.persistence.jpa.AbstractFinder;

import java.util.Date;

public class EventFinder extends AbstractFinder<Event, Event, EventFinder> {

	public EventFinder() {
		super("select e from Event e", "e");
	}

	public EventFinder setOwner(final EntityStub owner) {
		if (owner == null) {
			addNullFilter(prefixWithAlias("ownerPk"));
			addNullFilter(prefixWithAlias("ownerClass"));
		} else {
			addEqualFilter(prefixWithAlias("ownerPk"), owner.getPk());
			addEqualFilter(prefixWithAlias("ownerClass"), owner.getClass());
		}
		return this;
	}

	public EventFinder setEventDetail(final String eventDetail) {
		if (eventDetail == null) {
			addNullFilter(prefixWithAlias("eventDetail"));
		} else {
			addEqualFilter(prefixWithAlias("eventDetail"), eventDetail);
		}
		return this;
	}

	public EventFinder setOwnerClass(final Class<?> ownerClass) {
		if (ownerClass == null) {
			addNullFilter(prefixWithAlias("ownerClass"));
		} else {
			addEqualFilter(prefixWithAlias("ownerClass"), ownerClass);
		}
		return this;
	}

	public EventFinder setOwnerPk(final Long ownerPk) {
		if (ownerPk == null) {
			addNullFilter(prefixWithAlias("ownerPk"));
		} else {
			addEqualFilter(prefixWithAlias("ownerPk"), ownerPk);
		}
		return this;
	}

	public EventFinder setChangedBy(final String source) {
		if (source == null) {
			addNullFilter(prefixWithAlias("changedBy"));
		} else {
			addEqualFilter(prefixWithAlias("changedBy"), source);
		}
		return this;
	}

	@SuppressWarnings("UnusedDeclaration")
	public EventFinder orderByCreated(final boolean ascending) {
		addSortOrder(prefixWithAlias("created"), ascending);
		return this;
	}

	public EventFinder setEventType(String eventType) {
		addJoin(prefixWithAlias("eventType et"));
		addEqualFilter("et.id", eventType);
		return this;
	}

	/**
	 * Finn hendelser koblet til valgte hendelsestyper
	 *
	 * @param eventTypes Set av hendelsestyper
	 */
	public EventFinder setEventTypes(Object[] eventTypes) {
		if (eventTypes != null && eventTypes.length > 0) {
            this.addInFilter(prefixWithAlias("eventType"), (Object[]) eventTypes);
        }
		return this;
	}

	/**
	 * Finn hendelser som kun er utført av kildene i sources
	 */
	public EventFinder setSources(Object[] sources) {
		if (sources != null && sources.length > 0) {
            this.addInFilter(prefixWithAlias("changedBy"), (Object[]) sources);
        }
		return this;
	}

	/**
	 * Finn hendelser som er opprettet etter angitt tidspunkt
	 */
	public EventFinder setCreatedAfter(Date createdTime) {
		if (createdTime != null) {
            addGreaterThanFilter("created", createdTime);
        }
		return this;
	}

	/**
	 * Finn hendelser som er opprettet før angitt tidspunkt
	 */
	public EventFinder setCreatedBefore(Date createdTime) {
		if (createdTime != null) {
            addSmallerThanFilter("created", createdTime);
        }
		return this;
	}

}