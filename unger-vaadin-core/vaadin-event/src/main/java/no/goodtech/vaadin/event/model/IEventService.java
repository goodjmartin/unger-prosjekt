package no.goodtech.vaadin.event.model;

import no.goodtech.persistence.entity.EntityStub;

public interface IEventService {

	public Event registerEvent(final EntityStub owner, final String changedBy, final String eventTypeId, final String eventDetail);

	public Event registerEvent(final Class<?> ownerClass, final Long ownerPk, final String changedBy, final String eventTypeId, final String eventDetail);

	public void deleteAllEvents(final EntityStub entity);

}
