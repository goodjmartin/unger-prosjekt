package no.goodtech.vaadin.properties.ui;

import com.vaadin.v7.data.util.converter.Converter;
import no.goodtech.vaadin.properties.model.AbstractPropertyValue;
import no.goodtech.vaadin.properties.model.PropertyStub;

import java.util.Locale;

/**
 * Use this to convert a property value internal value to a string so you can save the string representation in the database
 */
public class StringToPropertyValueObjectConverter implements Converter<Object, String> {
	
	private final PropertyStub property;
	
	public StringToPropertyValueObjectConverter(PropertyStub property) {
		this.property = property;
	}

	public String convertToModel(Object propertyValue, Class<? extends String> targetType, Locale locale)
			throws ConversionException {
		
		if (propertyValue == null)
			return null;
		try {
			return propertyValue.toString();
		} catch (RuntimeException e) {
			throw new ConversionException("Unable to parse " + propertyValue, e);
		}
	}

	public Object convertToPresentation(String value, Class<?> targetType,
			Locale locale) throws ConversionException {
	
		if (property == null)
			return null;

		final AbstractPropertyValue propertyValue = property.createValue();
		propertyValue.setValue(value);
		return propertyValue.getValue();
	}

	public Class<String> getModelType() {
		return String.class;
	}

	public Class<Object> getPresentationType() {
		return Object.class;
	}
}
