package no.goodtech.vaadin.properties.model;

import no.goodtech.vaadin.properties.model.PropertyValue;

/**
 * Represents an owner of one or more {@link PropertyValue}s
 */
public interface IPropertyOwner {
	
	/**
	 * @return the primary key of the owner
	 */
	Long getPk();
}
