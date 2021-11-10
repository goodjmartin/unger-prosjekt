package no.goodtech.vaadin.properties.repository;

import no.goodtech.persistence.server.AbstractRepositoryImpl;
import no.goodtech.vaadin.properties.model.Property;
import no.goodtech.vaadin.properties.model.PropertyValue;
import no.goodtech.vaadin.properties.model.PropertyValueFinder;

import org.springframework.transaction.annotation.Transactional;

/**
 * Grensesnitt mot database for {@link Property}
 * @author oystein
 */
@Transactional
public class PropertyValueRepository extends AbstractRepositoryImpl<PropertyValue, PropertyValue, PropertyValueFinder>{
	
	/**
	 * Vil oppdatere evt. egenskaps-verdier som arves
	 */
	@Override
	public PropertyValue save(PropertyValue entity) {

//TODO: Denne koden skal i stedet brukes når man lagrer en super-eier (som har andre objekter som arver egenskaper)
//		final Class<?> ownerClass = savedValue.getOwnerClass();
//		final PropertySource owner = (PropertySource) em.find(ownerClass, savedValue.getOwnerPk());
//		final Collection<Long> propertyHeirPks = owner.listPropertyHeirPks();
		return super.save(entity);
	}
}
