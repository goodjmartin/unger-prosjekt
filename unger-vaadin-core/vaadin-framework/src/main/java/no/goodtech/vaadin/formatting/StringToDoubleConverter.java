package no.goodtech.vaadin.formatting;

import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import no.cronus.common.utils.ParseUtils;
import no.goodtech.vaadin.ui.Texts;

/**
 *  A converter that accepts both , and . as decimal separator.
 *  I doesn't accept thousand separators.
 */
public class StringToDoubleConverter extends com.vaadin.data.converter.StringToDoubleConverter {

	public StringToDoubleConverter() {
		super(Texts.get("stringToDoubleConverter.error.message"));
	}

	public StringToDoubleConverter(String errorMessage) {
		super(errorMessage);
	}

	public StringToDoubleConverter(Double emptyValue, String errorMessage) {
		super(emptyValue, errorMessage);
	}

	@Override
	public Result<Double> convertToModel(String value, ValueContext context) {
		try {
			final Double doubleValue = ParseUtils.parseDoubleDoNotAcceptGroupingSeparators(value);
			return Result.ok(doubleValue);
		} catch (IllegalArgumentException illegalArgumentException) {
			return Result.error(getErrorMessage(context));
		}
	}

	@Override
	public String convertToPresentation(Double value, ValueContext context) {
		if (value == null) {
			return null;
		}
		final String stringValue = value.toString();
		if (stringValue.endsWith(".0")) {
			return stringValue.replace(".0", "");
		}
		return stringValue;
	}
}
