package no.goodtech.vaadin.event.model;

import no.goodtech.persistence.server.AbstractRepositoryImpl;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class EventOwnerRepository extends AbstractRepositoryImpl<EventOwner, EventOwner, EventOwnerFinder>{
}
