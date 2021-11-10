package no.goodtech.vaadin.utils;

import com.vaadin.v7.data.Validator;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

public class NumericPositiveValidator implements Validator {

	public void validate(Object value) throws InvalidValueException {
		if (value != null) {
			String stringValue = (String) value;
			if (!Utils.isInteger(stringValue))
				throw new Validator.InvalidValueException(getText("mustBeNumeric"));
			else if(Integer.parseInt((String) value) <= 0)
				throw new Validator.InvalidValueException(getText("mustBeLargerThanZero"));
		}
	}
	
	private String getText(String key) {
		return ApplicationResourceBundle.getInstance("vaadin-core").getString("validator." + key);
	}
}
