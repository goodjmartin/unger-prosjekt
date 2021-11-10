package no.goodtech.vaadin.formatting;

import com.vaadin.v7.data.util.converter.Converter;
import no.cronus.common.utils.ParseUtils;

import java.util.Locale;

/**
 * Brukes til tekstfelt hvor vi redigerer et desimaltall, hvor vi ikke ønsker spesiell formatering i tekstfeltet
 */
public class PlainDoubleToStringConverter implements Converter<String, Double> {

	@Override
	public String convertToPresentation(Double value, Class<? extends String> targetType, Locale locale)
			throws com.vaadin.v7.data.util.converter.Converter.ConversionException {
		if (value == null)
			return null;
		return value.toString();
	}
	
	public Class<Double> getModelType() {
		return Double.class;
	}

	public Class<String> getPresentationType() {
		return String.class;
	}

	public Double convertToModel(String value, Class<? extends Double> targetType, Locale locale)
			throws com.vaadin.v7.data.util.converter.Converter.ConversionException {
		try {
			return ParseUtils.parseDoubleDoNotAcceptGroupingSeparators(value);
    	} catch (IllegalArgumentException illegalArgumentException) {
    		throw new ConversionException("Vennligst angi et desimaltall", illegalArgumentException);
    	}
	}

}
