package no.goodtech.vaadin.ui.v7;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.Field;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Copy values from one field to other fields
 * @param <T> the type of the field content
 */
public class FieldCopyListener<T> implements Property.ValueChangeListener {

	private Set<Field<T>> fieldsToCopyTo = new LinkedHashSet<>();

	public FieldCopyListener(Set<Field<T>> fieldsToCopyTo) {
		this.fieldsToCopyTo.addAll(fieldsToCopyTo);
	}

	public FieldCopyListener(Field<T>... fieldsToCopyTo) {
		this(new LinkedHashSet<>(Arrays.asList(fieldsToCopyTo)));
	}

	@Override
	public void valueChange(Property.ValueChangeEvent event) {
		for (Field<T> fieldToCopyTo : fieldsToCopyTo) {
			if (fieldToCopyTo.getValue() == null
					|| (fieldToCopyTo.getValue() instanceof String && ((String)fieldToCopyTo.getValue()).isEmpty())) {

				final Object value = event.getProperty().getValue();
				fieldToCopyTo.setValue((T) value);
			}
		}
	}
}
