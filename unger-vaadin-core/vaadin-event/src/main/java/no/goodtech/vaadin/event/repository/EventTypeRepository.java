package no.goodtech.vaadin.event.repository;

import no.goodtech.vaadin.event.model.EventType;
import no.goodtech.vaadin.event.model.EventTypeFinder;
import no.goodtech.persistence.server.AbstractRepositoryImpl;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class EventTypeRepository extends AbstractRepositoryImpl<EventType, EventType, EventTypeFinder> {
}