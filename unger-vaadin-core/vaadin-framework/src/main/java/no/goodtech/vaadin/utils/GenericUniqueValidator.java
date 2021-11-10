package no.goodtech.vaadin.utils;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import no.goodtech.persistence.entity.Entity;
import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.persistence.jpa.AbstractFinder;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

/**
 * Vaadin 8 validator for entities. Checks if there is any entities with the given finder
 */
public class GenericUniqueValidator<TYPE, ENTITY extends Entity, ENTITYSTUB extends EntityStub<?>, FINDER> implements Validator<TYPE> {

	protected final AbstractFinder<ENTITY, ENTITYSTUB, FINDER> finder;
	protected final String errorMessage;

	public GenericUniqueValidator(final AbstractFinder<ENTITY, ENTITYSTUB, FINDER> finder) {
		this(finder, null);
	}

	public GenericUniqueValidator(final AbstractFinder<ENTITY, ENTITYSTUB, FINDER> finder, final String errorMessage) {
		this.finder = finder;
		this.errorMessage = errorMessage;
	}

	@Override
	public ValidationResult apply(TYPE value, ValueContext context) {
		String msg = errorMessage != null ? errorMessage : ApplicationResourceBundle.getInstance("vaadin-core").getString("validator.mustBeUnique");
		return finder.exists() ? ValidationResult.error(msg) : ValidationResult.ok();
	}

	public AbstractFinder<ENTITY, ENTITYSTUB, FINDER> getFinder() {
		return finder;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
