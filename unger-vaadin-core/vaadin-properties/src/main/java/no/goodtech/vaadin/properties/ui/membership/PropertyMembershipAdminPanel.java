package no.goodtech.vaadin.properties.ui.membership;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import no.goodtech.vaadin.main.SimpleInputBox;
import no.goodtech.vaadin.properties.model.PropertyMembership;
import no.goodtech.vaadin.properties.model.PropertyMembershipFinder;
import no.goodtech.vaadin.properties.model.PropertyMembershipStub;
import no.goodtech.vaadin.properties.ui.Texts;
import no.goodtech.vaadin.security.AccessFunction;
import no.goodtech.vaadin.tabs.IMenuView;
import no.goodtech.vaadin.ui.SimpleCrudAdminPanel;

@UIScope
@SpringView(name = PropertyMembershipAdminPanel.VIEW_ID)
public class PropertyMembershipAdminPanel extends SimpleCrudAdminPanel<PropertyMembership, PropertyMembershipStub, PropertyMembershipFinder> implements IMenuView {

	public static final String VIEW_ID = "PropertyMembershipAdminPanel";
	public static final String VIEW_NAME = Texts.get("propertyMembership.viewName");

	private static String ACCESS_VIEW = "propertyMembership.view";
	private static String ACCESS_EDIT = "propertyMembership.edit";

	public PropertyMembershipAdminPanel() {
		super(new PropertyMembershipFilterPanel(),new PropertyMembershipGrid());
		setSizeFull();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		((PropertyMembershipFilterPanel)filterPanel).refresh();
		refresh();
	}

	@Override
	public String getViewName() {
		return VIEW_NAME;
	}

	@Override
	protected SimpleInputBox.IinputBoxContent createDetailForm(PropertyMembershipStub entity) {
		return new PropertyMembershipForm(entity);
	}

	@Override
	protected PropertyMembershipStub createEntity() {
		final PropertyMembership membership = new PropertyMembership();
		membership.setPropertyClass(((PropertyMembershipFilterPanel)filterPanel).getPropertyClass());
		return membership;
	}

	@Override
	protected AccessFunction getAccessFunctionView() {
		return new AccessFunction(ACCESS_VIEW, Texts.get("accessFunction." + ACCESS_VIEW));
	}

	@Override
	protected AccessFunction getAccessFunctionEdit() {
		return new AccessFunction(ACCESS_EDIT, Texts.get("accessFunction." + ACCESS_EDIT));
	}
}
