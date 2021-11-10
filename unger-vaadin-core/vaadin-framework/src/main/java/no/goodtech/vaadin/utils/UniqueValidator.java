package no.goodtech.vaadin.utils;

import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

/**
 * Checks if object under editing is unique.
 * You have to provide a query to ask for possible duplicates from the database through {@link IDuplicateCandidateProvider}
 * @deprecated for new Vaadin 8 forms
 */
@Deprecated
public class UniqueValidator implements Validator {

	public interface IDuplicateCandidateProvider {
		Object findObjectWithSameCharacteristics(); 
	}

	private final BeanFieldGroup<?> binder;
	private final IDuplicateCandidateProvider uniqueAssistance;
	
	/**
	 * Create validator
	 * @param binder the field group that holds the object under editing
	 * @param uniqueAssistance a query that finds possible duplicates
	 */
	public UniqueValidator(BeanFieldGroup<?> binder, final IDuplicateCandidateProvider uniqueAssistance) {
		this.binder = binder;
		this.uniqueAssistance = uniqueAssistance;
	}

	public void validate(Object value) throws InvalidValueException {
		if (value != null) {
			Object possibleDuplicate = uniqueAssistance.findObjectWithSameCharacteristics();
			if (possibleDuplicate != null) {
				final Object entityUnderEditing = binder.getItemDataSource().getBean();
				if (!possibleDuplicate.equals(entityUnderEditing)) {
					final String message = ApplicationResourceBundle.getInstance("vaadin-core").getString("validator.mustBeUnique");
					throw new Validator.InvalidValueException(String.format(message, value));
				}
					
			}
		}
	}
}
