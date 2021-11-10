package no.goodtech.vaadin.properties.model;

import no.goodtech.persistence.jpa.AbstractFinder;
import org.apache.commons.lang3.StringUtils;

/**
 * Find propertyclass(es)
 */
public class PropertyClassFinder extends AbstractFinder<PropertyClass, PropertyClassStub, PropertyClassFinder>
{

	public PropertyClassFinder() {
		super("select pc from PropertyClass pc", "pc");
	}

	/**
	 * Search for propertyclasses with the given description
	 *
	 * @param description LIKE-search for description. ? and * is not necessary
	 * @return The result after search
	 */
	public PropertyClassFinder setDescription(String description) {
		if (StringUtils.isNotBlank(description))
			addLikeFilter("pc.description", description, true);
		return this;
	}

//	/**
//	 * Search for propertyclasses with the id: propertyId
//	 *
//	 * @param propertyId the propertyId
//	 * @return The result after searching with the new filter
//	 */
//	public PropertyClassFinder setPropertyId(String propertyId) {
//		if (StringUtils.isNotBlank(propertyId)) {
////			addEqualFilter("pg.id", propertyId);
//			Property property = new PropertyFinder().setId(propertyId).list().get(0).load();
//			join("join pc.propertyGroups pg");
//			addInFilter(property, "pg.id");
//		}
//		return this;
//	}

	/**
	 * Filters on id with prefix.
	 * Filters on id with prefix.
	 * @param id
	 * @return spørring med dette filteret satt
	 */
	public PropertyClassFinder setId(String id){
		if (id == null)
			addNullFilter(prefixWithAlias("id"));
		else if(id.length() == 0)
			removeFilter("id");
		else
			addLikeFilter(prefixWithAlias("id"), id, false);
		return this;
	}

	public PropertyClassFinder setOrderByDescription() {
		addSortOrder("description", true);
		return this;
	}

//	public PropertyClass getPropertyClassConnection (EntityStub<?> owner) {
//		// TODO read from resource-file and return PropertyClass
//		return null;
//	}

}