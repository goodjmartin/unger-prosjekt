package no.goodtech.vaadin.properties.model;

/**
 * En søkeklasse for Owner (entiteten som brukes til testing) 
 * @author oystein
 */
public class OwnerFinder extends PropertyOwnerFinder<PropertyOwner, PropertyOwner, OwnerFinder> {

	/**
	 * Opprett søk
	 */
	public OwnerFinder() {
		super("select o from PropertyOwner o", "o");
	}
}
