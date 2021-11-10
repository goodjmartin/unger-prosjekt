package no.goodtech.vaadin.properties.model;

import no.goodtech.persistence.entity.EntityStub;

import java.util.List;

public class PropertyValuesUtil {
	/**
	 * Use this to fetch PropertyValues for a list of owners. (the owners has to be the same kind of Class)
	 *
	 * @param owners the owners of the PropertyValues
	 * @param propertyIds specify certain Properties with their ids. Leave empty to get all.
	 * @return PropertyValues for owners
	 */
	public static PropertyValues getPropertyValues(List<? extends EntityStub<?>> owners, String ... propertyIds) {
		final PropertyValueFinder finder = new PropertyValueFinder().setOwners(owners);
		if(propertyIds != null && propertyIds.length>0)
			finder.setPropertyId(propertyIds);
		return new PropertyValues(finder.list());
	}
}
