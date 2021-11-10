package no.goodtech.vaadin.properties.ui;

import no.goodtech.vaadin.properties.model.PropertyStub;
import no.goodtech.vaadin.properties.model.PropertyValueStub;

/**
 * Formats a property value according to the format of the property
 * If the Property has no format, the toString will be returned
 */
public class PropertyValueFormatter {

	private static final String RESOURCE_PREFIX = "propertyValueFormatter.booleanValue.";

	/**
	 * Formats the value according to the format of the property.
	 * Boolean property values will get language specific formatting. 
	 * @param propertyValue the value that holds the value to be formatted.
	 * @param property the property holding the datatype and the value
	 * @return the value formatted. If format is missing, the toString() is returned 
	 */
	public static String formatValue(PropertyValueStub propertyValue, PropertyStub property) {
		Object value = propertyValue.getValue();
		return formatValue(value, property);
	}

	/**
	 * Formats the value according to the format of the property.
	 * Boolean property values will get language specific formatting. 
	 * @param value the value to be formatted.
	 * @param property the property holding the datatype and the value
	 * @return the value formatted. If format is missing, the toString() is returned 
	 */
	public static String formatValue(String value, PropertyStub property) {
		Object propertyValue = property.parseValue(value);
		return formatValue(propertyValue, property);
	}
	
	/**
	 * @see #formatValue(String, PropertyStub)
	 */
	public static String formatValue(Object value, PropertyStub property) {
		if (value == null)
			return "";
		if (property.getDataType().equals(Boolean.class)) {
			return Texts.get(RESOURCE_PREFIX + value);
		}
		return property.format(value);
	}
}
