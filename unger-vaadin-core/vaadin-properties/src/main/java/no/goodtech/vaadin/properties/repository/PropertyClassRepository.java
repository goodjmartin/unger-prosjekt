package no.goodtech.vaadin.properties.repository;


import no.goodtech.persistence.server.AbstractRepositoryImpl;
import no.goodtech.vaadin.properties.model.PropertyClass;
import no.goodtech.vaadin.properties.model.PropertyClassStub;
import no.goodtech.vaadin.properties.model.PropertyClassFinder;
import org.springframework.transaction.annotation.Transactional;

/**
 * Grensesnitt mot database for {@link PropertyClass}
 * Created by mikkelsn on 21.08.2014.
 */
@Transactional
public class PropertyClassRepository extends AbstractRepositoryImpl<PropertyClass, PropertyClassStub, PropertyClassFinder>
{
}
