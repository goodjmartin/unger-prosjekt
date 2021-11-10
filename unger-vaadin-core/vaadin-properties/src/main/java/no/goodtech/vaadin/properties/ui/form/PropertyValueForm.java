package no.goodtech.vaadin.properties.ui.form;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Buffered.SourceException;
import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.Label;
import javafx.scene.control.RadioButton;
import no.goodtech.persistence.entity.Entity;
import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.vaadin.properties.model.PropertyMembership;
import no.goodtech.vaadin.properties.model.PropertyMembershipFinder;
import no.goodtech.vaadin.properties.model.PropertyStub;
import no.goodtech.vaadin.properties.model.PropertyValue;
import no.goodtech.vaadin.properties.model.PropertyValueFinder;
import no.goodtech.vaadin.properties.ui.PropertyValueField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A component with all the PropertyValueFields for all the PropertyValues connected to the object
 * with the specified ownerClass and ownerPk.
 */
public class PropertyValueForm extends VerticalLayout {
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertyValueForm.class);

	private String propertyClassId;
	private Entity owner;
	private Class<?> booleanRepresentation = CheckBox.class;
	private boolean showTooltip = true;
	private boolean showOnlyEditableFields = false;
	private final List<PropertyValueField> fields;

	protected boolean isReadOnly;

	public PropertyValueForm() {
		fields = new ArrayList<>();
		setMargin(false);
	}

	public void refresh(Entity owner) {
		if (owner != null) {
			refresh(owner.getClass().getSimpleName(), owner);
		} else {
			removeAllComponents();
			fields.clear();
		}
	}

	/**
	 * Refresh PropertyValues with its PropertyValueField for the object specified by ownerClass and ownerPk.
	 * Properties decides which properties to be displayed. If propertyClassId is null, all PropertyValues are displayed for
	 * the specified owner. The owner is not nullable
	 * @param propertyClassId - the PropertyValue's propertyClass
	 * @param owner           - the PropertyValue's owner
	 */
	public void refresh(String propertyClassId, Entity owner) {
		removeAllComponents();
		fields.clear();

		if (owner != null) {
			this.propertyClassId = propertyClassId;
			this.owner = owner;

			final PropertyMembershipFinder memberShipFinder = new PropertyMembershipFinder().setPropertyClassId(propertyClassId);
			Map<PropertyStub, PropertyMembership> propertyMemberships = memberShipFinder.getMembershipByPropertyMap();

			Map<PropertyStub, PropertyValue> propertyValues = new LinkedHashMap<PropertyStub, PropertyValue>();
			if (owner.isNew()) {
				for (PropertyStub property : propertyMemberships.keySet())
					propertyValues.put(property, new PropertyValue(property, owner));
			} else {
				final PropertyValueFinder finder = new PropertyValueFinder();
				finder.setProperties(propertyMemberships.keySet()).setOwner((EntityStub<?>) owner);
				propertyValues = finder.getPropertyValueMap();
			}
				
			for (Map.Entry<PropertyStub, PropertyMembership> entry : propertyMemberships.entrySet()) {
				PropertyStub property = entry.getKey();
				final PropertyValue propertyValue = propertyValues.get(property);
				addField(propertyValue, entry.getValue(), owner);
			}		
		}
		setVisible(fields.size() > 0);
	}


	private void addField(PropertyValue propertyValue, PropertyMembership propertyMembership, Entity owner) {
		PropertyStub property = propertyMembership.getProperty();
		PropertyValueField field = new PropertyValueField(property, booleanRepresentation);
		if (!(field.getComponentType().equals(CheckBox.class))) {
			field.setCaption(property.getName());
		}
		field.setDescription(property.getDescription());
		field.setShowTooltip(showTooltip);
		field.setValue(propertyValue);
		if (propertyMembership != null) {
			if (!propertyMembership.isEditable()) {
				field.setReadOnly(true);
				if (showOnlyEditableFields) {
					return; //do not add field
				}
			}
			field.setRequired(propertyMembership.isRequired());
		}
		fields.add(field);

		if (isReadOnly())
			field.setReadOnly(true); //override read only setting if form is set read only specifically
		
		if (property.getUnitOfMeasure() != null) {
			final Label unitLabel = new Label(property.getUnitOfMeasure());
			unitLabel.setSizeUndefined();
			final HorizontalLayout layout = new HorizontalLayout(field, unitLabel);
			layout.setMargin(false);	
			layout.setWidth("100%");
			layout.setComponentAlignment(unitLabel, Alignment.BOTTOM_RIGHT);
			layout.setExpandRatio(field, 1);
			addComponent(layout);
		} else {
			addComponent(field);
		}
	}

	public PropertyMembership getPropertyFromMembership(List<PropertyMembership> propertyMemberships, PropertyStub property) {
		for (PropertyMembership membership : propertyMemberships) {
			if (membership.getProperty().getPk().equals(property.getPk())) {
				return membership;
			}
		}
		return null;
	}

	public void setOwner(Entity owner){
		this.owner = owner;
	}

	/**
	 * Saves all propertyValues and refreshes the form.
	 * @throws FieldGroup.CommitException if any field doesn't validate 
	 */
	public void save() throws FieldGroup.CommitException {
		for (Field<?> field : fields)
			field.validate();
		for (Field<?> field : fields) {
			PropertyValue propertyValue = (PropertyValue) field.getValue();
			if (propertyValue == null) {
				LOGGER.warn("property value was null for field: " + field);
			} else {
				propertyValue.setOwner(owner);
				Object value = propertyValue.getValue(); 
				if (value != null)
					propertyValue.save();
				else if (!propertyValue.isNew())
					propertyValue.delete();					
			}
		}
		refresh(propertyClassId, owner);
	}


	/**
	 * Validates all the fields
	 * @throws CommitException if any field doesn't validate
	 */
	public void commit() throws CommitException {
		try {
			for (Field<?> field : fields) {
				field.commit();
				field.validate();
			}
		} catch (InvalidValueException e) {
			throw new CommitException(e);
		} catch (SourceException e) {
			throw new CommitException(e);
		}
	}

	public boolean isValid(){
		try {
			commit();
			return true;
		} catch (CommitException e) {
		}
		return false;
	}
	
	@Override
	public void setReadOnly(boolean readOnly) {
		isReadOnly = readOnly;
		for (Field<?> field : fields)
			field.setReadOnly(readOnly);
	}
	
	/**
	 * @return shows property description as tooltip, default = true
	 */
	public boolean isShowTooltip() {
		return showTooltip;
	}

	/**
	 * @param showTooltip true = show property description as tooltip, false = do not show tooltip
	 */
	public void setShowTooltip(boolean showTooltip) {
		this.showTooltip = showTooltip;
	}

	/**
	 * @return how boolean vales are shown. Default = CheckBox.
	 * @see #setBooleanRepresentation(Class)
	 */
	public Class<?> getBooleanRepresentation() {
		return booleanRepresentation;
	}

	/**
	 * @param booleanRepresentation how to show boolean values. Use {@link RadioButton}, {@link ComboBox} for yes/no or {@link CheckBox}. null => CheckBox
	 */
	public void setBooleanRepresentation(Class<?> booleanRepresentation) {
		this.booleanRepresentation = booleanRepresentation;
	}

	public boolean isShowOnlyEditableFields() {
		return showOnlyEditableFields;
	}

	public void setShowOnlyEditableFields(boolean showOnlyEditableFields) {
		this.showOnlyEditableFields = showOnlyEditableFields;
	}

	@Override
	protected boolean isReadOnly() {
		return isReadOnly;
	}
}
