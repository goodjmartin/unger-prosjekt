package no.goodtech.vaadin.properties.repository;

import org.springframework.transaction.annotation.Transactional;

import no.goodtech.persistence.server.AbstractRepositoryImpl;
import no.goodtech.vaadin.properties.model.Property;
import no.goodtech.vaadin.properties.model.PropertyFinder;
import no.goodtech.vaadin.properties.model.PropertyStub;

/**
 * Grensesnitt mot database for {@link Property}
 * @author oystein
 */
@Transactional
public class PropertyRepository extends AbstractRepositoryImpl<Property, PropertyStub, PropertyFinder>{
}
