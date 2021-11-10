package no.goodtech.vaadin.properties.model;

import no.goodtech.persistence.entity.EntityStub;

/**
 * Created by mikkelsn on 26.08.2014.
 */
public interface PropertyMembershipStub extends EntityStub<PropertyMembership>
{

	boolean isRequired();

	boolean isEditable();

	/**
	 * @return true if this property is visible in crosstabs
	 * @see no.goodtech.vaadin.properties.ui.PropertyValueColumnGenerator
	 */
	boolean isShowInCrosstab();

	/**
	 * Used if if the properties in a membership have a specific order. 0 is first element. Null if sequence is not relevant 
	 */
	Integer getIndexNo();
	
	PropertyStub getProperty();
	
	PropertyClassStub getPropertyClass();
}
