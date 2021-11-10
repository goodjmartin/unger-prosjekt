package no.goodtech.vaadin.properties.repository;

import no.goodtech.persistence.server.AbstractRepositoryImpl;
import no.goodtech.vaadin.properties.model.PropertyOwner;
import no.goodtech.vaadin.properties.model.OwnerFinder;

import org.springframework.transaction.annotation.Transactional;

/**
 * Grensesnitt mot database for {@link PropertyOwner}
 * @author oystein
 */
@Transactional
public class OwnerRepository extends AbstractRepositoryImpl<PropertyOwner, PropertyOwner, OwnerFinder>{
}
