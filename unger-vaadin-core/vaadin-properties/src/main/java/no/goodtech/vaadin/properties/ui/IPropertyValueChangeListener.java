package no.goodtech.vaadin.properties.ui;

import no.goodtech.vaadin.properties.model.PropertyValue;

public interface IPropertyValueChangeListener {
	void propertyValueCreated(PropertyValue propertyValue);
	void propertyValueChanged(PropertyValue propertyValue);
	void propertyValueDeleted(PropertyValue propertyValue);
}
