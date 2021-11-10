package no.goodtech.vaadin.properties.ui.admin;

import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import no.goodtech.vaadin.main.SimpleInputBox.IinputBoxContent;
import no.goodtech.vaadin.properties.model.Property;
import no.goodtech.vaadin.properties.model.PropertyFinder;
import no.goodtech.vaadin.properties.model.PropertyStub;
import no.goodtech.vaadin.properties.model.PropertyValueFinder;
import no.goodtech.vaadin.properties.ui.Texts;
import no.goodtech.vaadin.security.AccessFunction;
import no.goodtech.vaadin.tabs.IMenuView;
import no.goodtech.vaadin.ui.SimpleCrudAdminPanel;

/**
 * Panel som viser en liste av for visning av egenskaper, inneholder knapper for opprettelse og sletting
 */
@UIScope
@SpringView(name = PropertyAdminPanel.VIEW_ID)
public class PropertyAdminPanel extends SimpleCrudAdminPanel<Property, Property, PropertyFinder> implements IMenuView {

	public static final String VIEW_ID = "PropertyAdminPanel";
	public static final String VIEW_NAME = Texts.get("propertyPanel.viewName");

	private static final String ACCESS_VIEW = "propertyPanel.view";
	private static final String ACCESS_EDIT = "propertyPanel.edit";

	public PropertyAdminPanel() {
		super(new PropertyFilterPanel(), new PropertyGrid());

	}

	@Override
	protected IinputBoxContent createDetailForm(Property property) {
		return new PropertyForm(property, isDataTypeEditable(property));
	}

	@Override
	protected Property createEntity() {
		return new Property();
	}

	protected AccessFunction getAccessFunctionView() {
		return new AccessFunction(ACCESS_VIEW, Texts.get("accessFunction.propertyPanel.view"));
	}

	protected AccessFunction getAccessFunctionEdit() {
		return new AccessFunction(ACCESS_EDIT, Texts.get("accessFunction.propertyPanel.edit"));
	}

	@Override
	protected Property copy(Property entityToCopy) {
		return entityToCopy.load().copy();
	}

	@Override
	protected String isDeleteOk(Property property) {
		if (!isDataTypeEditable(property))
			return ("Denne egenskapen er i bruk, og kan derfor ikke slettes");
		return null;
	}
	@Override
	public String getViewName() {
		return VIEW_NAME;
	}
	
	/**
	 * Checks if the property is in use
	 * It is not editable if the Property is connected to a PropertyValue.
	 * @param property the Property
	 * @return true: no PropertyValue connected, false: at least one PropertyValue connected
	 */
	public boolean isDataTypeEditable(PropertyStub property) {
		return !(new PropertyValueFinder().setProperty(property).exists());
	}
}
