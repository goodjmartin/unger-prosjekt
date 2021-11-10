package no.goodtech.vaadin.formatting;

import com.vaadin.v7.data.util.converter.AbstractStringToNumberConverter;

import java.util.Locale;

/**
 * Brukes til tekstfelt hvor vi redigerer et tall, hvor vi ikke ønsker spesiell formatering i tekstfeltet
 */
public class StringToLongConverter extends AbstractStringToNumberConverter<Long> {

    public Class<String> getPresentationType() {
        return String.class;
    }

	public Class<Long> getModelType() {
		return Long.class;
	}

	public String convertToPresentation(Long value, Class<? extends String> targetType, Locale locale)
			throws com.vaadin.v7.data.util.converter.Converter.ConversionException {
		if (value == null)
			return null;
		return value.toString();
	}

	public Long convertToModel(String value, Class<? extends Long> targetType, Locale locale)
			throws com.vaadin.v7.data.util.converter.Converter.ConversionException {
		if (value == null || value.trim().length() == 0)
			return null;
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			throw new ConversionException("Vennligst angi et heltall", e);
		}
	}
}
