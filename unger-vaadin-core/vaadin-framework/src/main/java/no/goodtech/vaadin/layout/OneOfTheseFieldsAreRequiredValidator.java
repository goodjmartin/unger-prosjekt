package no.goodtech.vaadin.layout;

import com.vaadin.server.ErrorMessage;
import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.Field;
import no.cronus.common.utils.CollectionUtils;
import no.goodtech.vaadin.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Validates a group of connected fields and require that one of them must have a value
 */
public class OneOfTheseFieldsAreRequiredValidator {

	final HashMap<AbstractField<?>, Validator> validators;
	public OneOfTheseFieldsAreRequiredValidator(final AbstractField<?>... fields) {

		validators = new HashMap<>();
		for (AbstractField<?> thisField : fields) {
			final Validator validator = new Validator() {

				public void validate(Object value) throws InvalidValueException {
					if (value == null) {

						for (AbstractField<?> field : fields) {
							if (field.getValue() != null) {
								clearAllErrorMessages(fields);
								return; //one of the fields have a value, so we are happy and stop yelling
							} else {
								field.setComponentError(createError(getErrorMessage(fields)));
							}
						}
						throw new EmptyValueException(""); //we have already error-marked the field, so throw this only to prevent commit
					} else {
						//field is set so we are happy
						clearAllErrorMessages(fields);
					}
				}
			};
			validators.put(thisField, validator);
		}
	}
	
	private void clearAllErrorMessages(final AbstractField<?>... fields) {
		for (AbstractField<?> field : fields) {
			field.setComponentError(null);
		}
	}

	private ErrorMessage createError(String errorMessage) {
		return new ErrorMessage() {
			
			public String getFormattedHtmlMessage() {
				return errorMessage;
			}
			
			public ErrorLevel getErrorLevel() {
				return ErrorLevel.WARNING;
			}
		};
	}
	
	private String getErrorMessage(Field<?>[] fields) {
		List<String> captions = new ArrayList<String>();
		for (Field<?> field : fields)
			captions.add(field.getCaption());
		
		return Utils.getText("oneOfTheseFieldsAreRequiredValidator.message") + ": " + CollectionUtils.listToCommaSeparated(captions); 
	}

	/**
	 * Remove validators
	 */
	public void clear() {
		for (Map.Entry<AbstractField<?>, Validator> entry : validators.entrySet()) {
			entry.getKey().removeValidator(entry.getValue());
		}
		validators.clear();
	}
}
