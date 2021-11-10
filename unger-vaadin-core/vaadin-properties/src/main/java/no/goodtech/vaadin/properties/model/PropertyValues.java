package no.goodtech.vaadin.properties.model;

import java.util.*;

/**
 * Help to organize property values owned by owners of the same type.
 * To lookup a specific property value, use {@link #getValue(Long, String)} --> String = property.getId()
 * To find which properties is contained, use {@link #getProperties()} or {@link #contains(Object)} 
 */
public class PropertyValues {

	// Key <String> = the id of Property
	private Map<Long, Map<String, PropertyValueStub>> valuesPerOwnerPk = new LinkedHashMap<>();
	private Set<PropertyStub> properties = new LinkedHashSet<>();

	/**
	 * Create a bunch of property values with same owner type 
	 * @param values the values you would like to organize
	 * @throws IllegalArgumentException if the values are owned by different owner types 
	 */
	public PropertyValues(List<? extends PropertyValueStub> values) {
		Set<Class<?>> ownerClasses = new HashSet<>();
		for (PropertyValueStub value : values) {
			final Long ownerPk = value.getOwnerPk();
        	
			Map<String, PropertyValueStub> valuesForThisOwner = valuesPerOwnerPk.get(ownerPk);
        	if (valuesForThisOwner == null) {
        		valuesForThisOwner = new HashMap<>();
        		valuesPerOwnerPk.put(ownerPk, valuesForThisOwner);
        	}
        	final PropertyStub property = value.getProperty();
			properties.add(property);
			valuesForThisOwner.put(property.getId(), value);
			
        	ownerClasses.add(value.getOwnerClass());
        }
		if (ownerClasses.size() > 1) {
			throw new IllegalArgumentException("I cannot handle to different owner types at the same time: " + ownerClasses);
		}
	}

	/**
	 * @param ownerPk the primary key to the owner of the value
	 * @param propertyId the id of the property the value is based on
	 * @return the property value owned by the owner with the right pk
	 */
	public PropertyValueStub getValue(Long ownerPk, String propertyId) {
		final Map<String, PropertyValueStub> valuesForThisOwner = valuesPerOwnerPk.get(ownerPk);
		if (valuesForThisOwner != null)
			return valuesForThisOwner.get(propertyId);
		return null;
	}
	
	/**
	 * @param property the property you need
	 * @return true if I contain the property you asked for
	 */
	public boolean contains(Object property) {
		return properties.size() > 0 && properties.contains(property);
	}

	/**
	 * @return all properties contained
	 */
	public Set<PropertyStub> getProperties() {
		return properties;
	}	
}
