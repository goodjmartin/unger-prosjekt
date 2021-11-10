package no.goodtech.vaadin.properties.model;

import no.goodtech.persistence.jpa.AbstractFinder;
import no.goodtech.vaadin.search.FilterPanel.IMaxRowsAware;


/**
 * Bruk denne for å finne kunde-spesifikke egenskaper
 * @author oystein
 */
public class PropertyFinder extends AbstractFinder<Property, PropertyStub, PropertyFinder> 
implements IMaxRowsAware {

	/**
	 * Opprett søk
	 */
	public PropertyFinder() {
		super("select p from no.goodtech.vaadin.properties.model.Property p", "p");
	}

	/**
	 * Filtrer på bruker av egenskap
	 * @param implementationClass objekttype som bruker egenskap 
	 * @return søk med dette filteret satt
	 */
	public PropertyFinder setImplementationClass(Class<?> implementationClass) {
		addJoin(getAlias() + ".implementations i");
		addEqualFilter("i.ownerClass", implementationClass);
		return this;
	}

	/**
	 * Filter based on a propertyclass.
	 *
	 * @param propertyClassId decides the propertyclass
	 * @return the result from the search
	 */
	public PropertyFinder setPropertyClassId(final String... propertyClassId) {
		if (propertyClassId != null) {
			addJoin("p.propertyMemberships pg");
			addInFilter("pg.propertyClass.id", ((Object[]) propertyClassId));
		}
		return this;
	}
	
	public PropertyFinder setOrderByName() {
		addSortOrder(prefixWithAlias("name"), true);
		return this;
	}

	/**
	 * Filter by name
	 * @param name you may use wildcards
	 */
	public PropertyFinder setName(String name) {
		String property = "name";
		if (name == null)
			addNullFilter(prefixWithAlias(property));
		else
			addLikeFilter(prefixWithAlias(property), name, false);
		return this;
	}
}
