package no.goodtech.vaadin.validation;


import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.ui.Field;
import no.goodtech.vaadin.ui.Texts;
import no.goodtech.vaadin.utils.Utils;

/**
 * Compares two numeric fields. Will NOT yell if fields are empty or they don't contain numbers
 */
public class CompareNumericFieldsValidator implements Validator {

	public enum Rule {
		LESS_THAN, LESS_THAN_OR_EQUAL, BIGGER_THAN, BIGGER_THAN_OR_EQUAL
	}

	private final Field<?> fieldToCompareTo;
	private final Rule rule;

	public CompareNumericFieldsValidator(Field<?> fieldToCompareTo, Rule rule) {
		this.fieldToCompareTo = fieldToCompareTo;
		this.rule = rule;
	}

	/**
	 * Create validators and apply them to both fields in one go
	 * @param field1 the first field to validate
	 * @param field2 the other field to validate
	 * @param rule example: field1 must be bigger than field 2
	 * @return the validator for field1
	 */
	public static CompareNumericFieldsValidator apply(Field<?> field1, Field<?> field2, Rule rule) {
		CompareNumericFieldsValidator validatorForField1 = new CompareNumericFieldsValidator(field2, rule);
		CompareNumericFieldsValidator validatorForField2 = null;
		switch (rule) {
			case LESS_THAN:
				validatorForField2 = new CompareNumericFieldsValidator(field1, Rule.BIGGER_THAN);
				break;
			case LESS_THAN_OR_EQUAL:
				validatorForField2 = new CompareNumericFieldsValidator(field1, Rule.BIGGER_THAN_OR_EQUAL);
				break;
			case BIGGER_THAN:
				validatorForField2 = new CompareNumericFieldsValidator(field1, Rule.LESS_THAN);
				break;
		}
		field1.addValidator(validatorForField1);
		field1.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				field2.requestRepaint();
			}
		});
		field2.addValidator(validatorForField2);
		field2.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				field1.requestRepaint();
			}
		});
		return validatorForField1;
	}


	@Override
	public void validate(Object value) throws InvalidValueException {
		if (value != null && fieldToCompareTo.getValue() != null) {
			final String thisValue = value.toString();
			final String otherValue = fieldToCompareTo.getValue().toString();
			if (Utils.isNumeric(thisValue) && Utils.isNumeric(otherValue)) {
				int compare = thisValue.compareTo(otherValue);
				switch (rule) {
					case LESS_THAN:
						if (compare >= 0)
							throwException();
						break;
					case LESS_THAN_OR_EQUAL:
						if (compare > 0)
							throwException();
						break;
					case BIGGER_THAN:
						if (compare <= 0)
							throwException();
							break;
					case BIGGER_THAN_OR_EQUAL:
						if (compare < 0)
							throwException();
						break;
				}
			}
		}
	}

	private void throwException() {
		final String message = Texts.get("validator.compareNumericFields." + rule);
		throw new InvalidValueException(String.format(message, fieldToCompareTo.getCaption()));
	}
}
