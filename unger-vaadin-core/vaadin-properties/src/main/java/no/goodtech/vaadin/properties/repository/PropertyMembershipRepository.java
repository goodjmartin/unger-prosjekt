package no.goodtech.vaadin.properties.repository;

import no.goodtech.persistence.server.AbstractRepositoryImpl;
import no.goodtech.vaadin.properties.model.PropertyMembership;
import no.goodtech.vaadin.properties.model.PropertyMembershipFinder;
import no.goodtech.vaadin.properties.model.PropertyMembershipStub;
import org.springframework.transaction.annotation.Transactional;

/**
 * Grensesnitt mot database for {@link no.goodtech.vaadin.properties.model.PropertyMembership}
 * Created by mikkelsn on 21.08.2014.
 */
@Transactional
public class PropertyMembershipRepository extends AbstractRepositoryImpl<PropertyMembership, PropertyMembershipStub, PropertyMembershipFinder>
{
}

