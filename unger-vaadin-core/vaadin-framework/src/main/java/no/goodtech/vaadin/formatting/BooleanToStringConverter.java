package no.goodtech.vaadin.formatting;

import com.vaadin.v7.data.util.converter.Converter;

import java.util.Locale;

public class BooleanToStringConverter implements Converter<Boolean, String> {

	@Override
	public String convertToModel(Boolean value, Class<? extends String> targetType, Locale locale) throws Converter.ConversionException {
		if (value == null) {
			return null;
		}
		Boolean boolValue = (Boolean) value;
		if (boolValue) {
			return "true";
		} else {
			return "false";
		}
	}

	@Override
	public Boolean convertToPresentation(String value, Class<? extends Boolean> targetType, Locale locale) throws Converter.ConversionException {
		if (value == null || value.isEmpty()) {
			return null;
		}
		// Remove leading and trailing white space
		value = value.trim();

		if (getTrueString().equals(value)) {
			return true;
		} else if (getFalseString().equals(value)) {
			return false;
		} else {
			throw new Converter.ConversionException("Cannot convert " + value + " to "
					+ getModelType().getName());
		}
	}

	@Override
	public Class<String> getModelType() {
		return String.class;
	}

	@Override
	public Class<Boolean> getPresentationType() {
		return Boolean.class;
	}

	/**
	 * Gets the string representation for true. Default is "true".
	 *
	 * @return the string representation for true
	 */
	protected String getTrueString() {
		return Boolean.TRUE.toString();
	}

	/**
	 * Gets the string representation for false. Default is "false".
	 *
	 * @return the string representation for false
	 */
	protected String getFalseString() {
		return Boolean.FALSE.toString();
	}
}
