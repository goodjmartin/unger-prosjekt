package no.goodtech.vaadin.layout;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.ui.TextField;

public class FormTextField extends TextField {

	public FormTextField(String caption, Integer maxLength) {
		setCaption(caption);
		setNullRepresentation("");
		setImmediate(true);
		setWidth(100, Unit.PERCENTAGE);
		if (maxLength != null)
			setMaxLength(maxLength);
	}

	public FormTextField(String caption) {
		this(caption, null);
	}

	public void setValidationRules(boolean required, final FormValidator formValidator, final UniqueValidator uniqueValidator) {
		if (required) {
			setRequired(true);
			setRequiredError("Vennligst angi '" + getCaption() + "'");
		}
		if (uniqueValidator != null) {
			addValidator(new Validator() {
				@Override
				public void validate(Object value) throws InvalidValueException {
					if (!isValid(value))
						throw new InvalidValueException("Vennligst angi en unik verdi for '" + getCaption() + "'");
				}

				public boolean isValid(Object value) {
					return value == null || uniqueValidator.isUnique((String) value);
				}
			});
		}
		if (formValidator != null) {
			addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(Property.ValueChangeEvent event) {
					formValidator.validate();
				}
			});
		}
	}

	public void setValidationRules(boolean required, final UniqueValidator uniqueValidator) {
		setValidationRules(required, null, uniqueValidator);
	}
}
