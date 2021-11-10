package no.goodtech.vaadin.properties.ui.admin;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.server.Setter;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;
import no.goodtech.persistence.entity.AbstractSimpleEntityImpl;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.main.SimpleInputBox.IinputBoxContent;
import no.goodtech.vaadin.properties.model.Property;
import no.goodtech.vaadin.properties.model.Property.Fields;
import no.goodtech.vaadin.properties.model.PropertyFinder;
import no.goodtech.vaadin.utils.UniqueIdValidator;

public class PropertyForm extends VerticalLayout implements IinputBoxContent {

	public static final String SELECT_OPTION = getText("select.option.select");
	public static final String TEXT_OPTION = getText("select.option.text");
	private static final String VALUETYPESELECT_PROPERTY_ID = getText("select.caption");

	private final TextField pk = createTextField(Fields.PK);
	private final TextField id = createTextField(Fields.ID);
	private final TextField name = createTextField(Fields.NAME);
	private final TextField unitOfMeasure = createTextField(Fields.UNIT_OF_MEASURE);
	private final TextField formatPattern = createTextField(Fields.FORMAT_PATTERN);
	private final TextArea description = createTextArea(Fields.DESCRIPTION);
	private final TextArea options = createTextArea(Fields.OPTIONS);
	private final ComboBox<Class> dataType = new ComboBox<>(VALUETYPESELECT_PROPERTY_ID);
	private final Binder<Property> binder;

	PropertyForm(Property property, boolean isDataTypeEditable) {
        binder = new Binder<>(Property.class);
        binder.forField(pk).withConverter(new StringToLongConverter("Må være heltall")).withNullRepresentation(-1L).bind((ValueProvider<Property, Long>) AbstractSimpleEntityImpl::getPk, (Setter<Property, Long>) (property1, aLong) -> { }).setReadOnly(true);
		binder.forField(id).asRequired("ID må være satt").withNullRepresentation("").withValidator(new UniqueIdValidator<>(new PropertyFinder(), binder)).bind("id");
		binder.forField(name).withNullRepresentation("").bind("name");
		binder.bind(unitOfMeasure, "unitOfMeasure");
		binder.bind(formatPattern, "formatPattern");
		binder.bind(description, "description");
		binder.bind(options, "options");
		binder.forField(dataType).asRequired("Egenskap må ha en datatype").bind("dataType");

		binder.setBean(property);

		setSizeFull();
		setMargin(false);
		addComponents(pk, id, name, dataType, unitOfMeasure, formatPattern, options, description);
		description.setSizeFull();
    	setExpandRatio(description, 1);

    	dataType.setEmptySelectionAllowed(false);
    	dataType.setItemCaptionGenerator((ItemCaptionGenerator<Class>) Property::getNiceDatatypeName);
    	dataType.setItems(Property.getDatatypesSupportedArray());
    	dataType.setValue(String.class);
//		dataType.addItem(SELECT_OPTION);
		dataType.setWidth("100%");
		
		if (!property.isNew() && !isDataTypeEditable)
			dataType.setReadOnly(true);

		options.setDescription(getText("options.tooltip"));
	}
	
	public Component getComponent() {
		return this;
	}

	public boolean commit() {
		binder.validate();
		try {
			if (binder.isValid()) {
				binder.writeBean(binder.getBean());
				binder.getBean().save();
				return true;
			}
		}catch (ValidationException e) {
			Notification.show(getText("propertyEditingForm.validation.failed"), Type.WARNING_MESSAGE);
		}
		return false;
	}

	private TextField createTextField(String captionKey) {
    	TextField field = new TextField(getText("field.caption." + captionKey));
    	field.setSizeFull();
    	field.setMaxLength(255);
    	return field;
    }
    
    private TextArea createTextArea(String captionKey) {
    	TextArea area = new TextArea(getText("field.caption." + captionKey));
    	area.setWidth("100%");
    	return area;
    }

	private static String getText(String key) {
		return ApplicationResourceBundle.getInstance("vaadin-properties").getString("propertyEditingForm." + key);
	}
}
