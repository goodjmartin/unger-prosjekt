package no.goodtech.vaadin.properties.ui;

import com.vaadin.ui.Component;
import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.v7.data.util.converter.Converter.ConversionException;
import com.vaadin.v7.ui.CustomField;
import no.goodtech.vaadin.properties.model.AbstractPropertyValue;
import no.goodtech.vaadin.properties.model.PropertyStub;

/**
 * A wrapper for {@link PropertyValueField} that converts to/from string values
 * To use this in a form or table, call {@link #setValue(String) and #getValue() as usual}
 * To validate, call {@link #validate()}
 */
public class StringPropertyValueField extends CustomField<String> {

	final PropertyValueField field;

	/**
	 * See {@link PropertyValueField}
	 */
	public StringPropertyValueField(PropertyStub property, Class<?> booleanRepresentation) {
		this.field = new PropertyValueField(property, booleanRepresentation);
	}

	protected Component initContent() {
		return field;
	}

	public Class<? extends String> getType() {
		return String.class;
	}

	protected String getInternalValue() {
		final AbstractPropertyValue propertyValue = field.getValue();
		if (propertyValue == null || propertyValue.getValue() == null)
			return null;
		try {
			return propertyValue.toString();
		} catch (RuntimeException e) {
			throw new ConversionException("Unable to parse " + propertyValue, e);
		}
	}

	public void setValue(String newFieldValue) throws com.vaadin.v7.data.Property.ReadOnlyException,
			ConversionException {
		final AbstractPropertyValue propertyValue = field.getProperty().createValue();
		propertyValue.setValue(newFieldValue);
		field.setValue(propertyValue);
	}
	
	public void validate() throws InvalidValueException {
		field.validate();
	}
}
