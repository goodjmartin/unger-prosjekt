package no.goodtech.vaadin.event.repository;

import no.goodtech.vaadin.event.model.Event;
import no.goodtech.vaadin.event.model.EventFinder;
import no.goodtech.persistence.server.AbstractRepositoryImpl;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class EventRepository extends AbstractRepositoryImpl<Event, Event, EventFinder> {
}
