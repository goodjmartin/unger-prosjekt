package no.goodtech.vaadin.properties.model;

import no.goodtech.persistence.entity.Entity;
import no.goodtech.persistence.entity.EntityStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PropertyValueService {
	private static final Logger logger = LoggerFactory.getLogger(PropertyValueService.class);

	public PropertyValueService(){
	}

	/**
	 * Adds or updates a PropertyValue.
	 */
	public static boolean setPropertyValue(final Entity entity, final String propertyId, final Object value,
										   final String description){
		final Property property = new PropertyFinder().setId(propertyId).find();

		if (property != null) {
			PropertyValue propertyValue = new PropertyValueFinder().setOwner((EntityStub<?>)entity).setPropertyId(propertyId).find();
			if (propertyValue == null) {
				propertyValue = new PropertyValue(property);
				propertyValue.setOwner(entity);
			}
			propertyValue.setValueAsObject(value);
			propertyValue.setDescription(description);
			propertyValue.save();

			logger.debug("Adding / updating property: propertyId=" + propertyId + ", value=" + value);
			return true;
		}

		logger.warn("Ignoring unknown property: propertyId=" + propertyId + ", value=" + value);
		return false;
	}

	public static boolean setPropertyValue(final Entity entity, final String propertyId, final Object value){
		return setPropertyValue(entity, propertyId, value, null);
	}

	public static void deleteAllProperties(final Entity entity) {
		// Remove all properties
		List<PropertyValue> propertyValues = new PropertyValueFinder().setOwner((EntityStub<?>)entity).list();
		if (propertyValues != null) {
			for (PropertyValue propertyValue : propertyValues) {
				propertyValue.delete();
			}
		}
	}
}
