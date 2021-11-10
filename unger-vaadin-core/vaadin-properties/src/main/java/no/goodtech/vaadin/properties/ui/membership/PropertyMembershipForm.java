package no.goodtech.vaadin.properties.ui.membership;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.*;
import no.goodtech.vaadin.main.SimpleInputBox.IinputBoxContent;
import no.goodtech.vaadin.properties.model.*;
import no.goodtech.vaadin.properties.ui.PropertyComboBox;
import no.goodtech.vaadin.properties.ui.Texts;
import no.goodtech.vaadin.utils.GenericUniqueValidator;

import java.util.List;

/**
 * A form to edit a property membership
 */
public class PropertyMembershipForm extends VerticalLayout implements IinputBoxContent {

	private final CheckBox required, editable, showInCrosstab;
	private final TextField indexNo;
	private final PropertyComboBox property;
	private final Binder<PropertyMembership> binder;

	public PropertyMembershipForm(final PropertyMembershipStub membership) {
		required = new CheckBox(getCaption("field.caption.required"));
		editable = new CheckBox(getCaption("field.caption.editable"));
		showInCrosstab = new CheckBox(getCaption("field.caption.showInCrosstab"));

		property = new PropertyComboBox(getCaption("field.caption.property"));
		property.setSizeFull();
		property.refresh(listAvailableProperties(membership));

		indexNo = new TextField(getCaption("field.caption.indexNo"));
		indexNo.setWidth("100%");
		
		binder = new Binder<>(PropertyMembership.class);

		final GenericUniqueValidator<PropertyStub, PropertyMembership, PropertyMembershipStub, PropertyMembershipFinder> uniqueValidator;
		if (membership.isNew()) {
			binder.setBean((PropertyMembership) membership);
			uniqueValidator = new GenericUniqueValidator<>(new PropertyMembershipFinder().setPropertyClasses(membership.getPropertyClass()).setProperties(property.getValue()));
		} else {
			binder.setBean(membership.load());
			uniqueValidator = new GenericUniqueValidator<>(new PropertyMembershipFinder().setPropertyClasses(membership.getPropertyClass()).setProperties(property.getValue()).setNotPk(membership.getPk()));
		}

		List<PropertyMembershipStub> lastPropertyMembership = new PropertyMembershipFinder().setPropertyClassPk(membership.getPropertyClass().getPk()).addSortOrderIndexNo(false).setMaxResults(1).list();
		Integer nextIndexNo = 1;
		if (lastPropertyMembership.size() > 0 && lastPropertyMembership.get(0).getIndexNo() != null)
			nextIndexNo = lastPropertyMembership.get(0).getIndexNo() + 1;
		binder.forField(indexNo).withConverter(new StringToIntegerConverter("Må være et heltall")).withNullRepresentation(nextIndexNo).bind("indexNo");
		binder.forField(property).asRequired("Egenskap er påkrevd").withValidator(uniqueValidator).bind("property");
		binder.forField(editable).bind("editable");
		binder.forField(required).bind("required");
		binder.forField(showInCrosstab).bind("showInCrosstab");

		setSizeFull();
		setMargin(false);
		addComponents(indexNo, property, new Label(), editable, required, showInCrosstab);
	}


	/**
	 * Remove properties already in use by this group
	 * TODO this should be one query. Not two..
	 */
	private List<PropertyStub> listAvailableProperties(PropertyMembershipStub membership) {
		final List<PropertyStub> properties = new PropertyFinder().setOrderByName().list();
		final PropertyMembershipFinder finder = new PropertyMembershipFinder();
		finder.setPropertyClasses(membership.getPropertyClass());
		finder.setIgnore(membership);
		final List<PropertyStub> propertiesInUse = finder.listProperties();
		properties.removeAll(propertiesInUse);
		return properties;
	}

	private static String getCaption(String key) {
		return Texts.get("property.membership.form." + key);
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public boolean commit() {
		binder.validate();
		try {
			if (binder.isValid()) {
				binder.writeBean(binder.getBean());
				binder.getBean().save();
				return true;
			}
		}catch (ValidationException e) {
			Notification.show(getCaption("validateFields"), Notification.Type.WARNING_MESSAGE);
		}
		return false;
	}

	public void setReadOnly(boolean readOnly) {
		binder.setReadOnly(readOnly);
	}
	
	public void focus() {
		indexNo.focus();
	}
}
