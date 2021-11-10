package no.goodtech.vaadin.utils;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.ValueContext;
import no.goodtech.persistence.entity.Entity;
import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.persistence.jpa.AbstractFinder;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

/**
 * Vaadin 8 validator for entities with unique ids
 */
public class UniqueIdValidator<ENTITY extends Entity, ENTITYSTUB extends EntityStub<?>, FINDER> extends GenericUniqueValidator<String, ENTITY, ENTITYSTUB, FINDER> {
	private final Binder<ENTITY> binder;

	public UniqueIdValidator(final AbstractFinder<ENTITY, ENTITYSTUB, FINDER> finder, final Binder<ENTITY> binder) {
		this(finder, null, binder);
	}

	public UniqueIdValidator(final AbstractFinder<ENTITY, ENTITYSTUB, FINDER> finder, final String errorMessage, final Binder<ENTITY> binder) {
		super(finder, errorMessage);
		this.binder = binder;
	}

	@Override
	public ValidationResult apply(String value, ValueContext context) {
		AbstractFinder copy = finder.copy();
		Entity entity = binder.getBean();
		if (!entity.isNew())
			copy.setNotPk(entity.getPk());
		copy.setId(value);
		String msg = errorMessage != null ? errorMessage : ApplicationResourceBundle.getInstance("vaadin-core").getString("validator.mustBeUnique");
		return copy.exists() ? ValidationResult.error(msg) : ValidationResult.ok();
	}
}
