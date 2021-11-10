package no.goodtech.vaadin.properties.ui;

import com.vaadin.ui.Component;
import com.vaadin.ui.Link;
import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.v7.data.util.converter.Converter.ConversionException;
import com.vaadin.v7.shared.ui.datefield.Resolution;
import com.vaadin.v7.ui.*;
import no.goodtech.vaadin.formatting.PlainDoubleToStringConverter;
import no.goodtech.vaadin.formatting.StringToLongConverter;
import no.goodtech.vaadin.linkField.LinkField;
import no.goodtech.vaadin.properties.model.AbstractPropertyValue;
import no.goodtech.vaadin.properties.model.Property;
import no.goodtech.vaadin.properties.model.PropertyStub;
import no.goodtech.vaadin.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * A field to represent a property value
 * TODO: Refactor PropertyAndValueBean with this
 * To use this in a form, call {@link #setValue(AbstractPropertyValue) and #getValue() as usual}
 * To validate, call {@link #validate()}
 * If you want to save the property value as a string, try {@link StringPropertyValueField} instead   
 */
public class PropertyValueField extends CustomField<AbstractPropertyValue> {

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertyValueField.class);
	private final Property property;
	private final AbstractField<?> field;
	private boolean showTooltip = true;
	private AbstractPropertyValue propertyValue;

	/**
	 * Create field according to the rules defined by given property
	 * @param booleanRepresentation how to show boolean values. Use {@link OptionGroup}, {@link ComboBox}/{@link NativeSelect} or {@link CheckBox}. null => CheckBox
	 */
	public PropertyValueField(PropertyStub property, Class<?> booleanRepresentation) {
		field = createField(property, booleanRepresentation);
		field.setCaption(property.getName());
		this.property = (Property) property;
	}

	public PropertyValueField(PropertyStub property) {
		this(property, CheckBox.class);
	}

	/**
	 * Oppretter felt på grunnlag av hvilken datatype property har
	 * @param booleanRepresentation 
	 * @return valgt felt for datatypen
	 */
	private AbstractField<?> createField(PropertyStub property, Class<?> booleanRepresentation) {
		Class<?> dataType = property.getDataType();

		if (property.getOptionsList().size() > 0) {
			//Hvis property har options, så lag en ny select.
			return createPropertyOptionsComboboxField(property.getOptionsList());
		} else if (Property.isTypeAnyOf(dataType, String.class, Long.class, Double.class)) {
			return createTextField(dataType); //Hvis datatypen er heltall, desimaltall eller tekst, opprett tekstfelt
		} else if (Property.isTypeAnyOf(dataType, Boolean.class)) {
			if (ComboBox.class.equals(booleanRepresentation) || NativeSelect.class.equals(booleanRepresentation))
				return createBooleanSelect();
			else if (OptionGroup.class.equals(booleanRepresentation))
				return createBooleanOptionGroupField();
			return createCheckBoxField();
		} else if (Property.isTypeAnyOf(dataType, Date.class)) {
			return createDateField(property);
		} else if (Property.isTypeAnyOf(dataType, Link.class)) {
			return createLinkField(property);
		}

		LOGGER.warn("Unsupported datatype: " + property.getDataType() + ", property ID: " + property.getId());
		return null;
	}

	private LinkField createLinkField(PropertyStub property) {
		final LinkField linkField = new LinkField(null, property.getFormatPattern());
		return linkField;
	}
	
	private DateField createDateField(PropertyStub property) {
		DateField dateField = new DateField();
		dateField.setImmediate(true);
		dateField.setResolution(Resolution.MINUTE);
		if (property.getFormatPattern() != null)
			dateField.setDateFormat(property.getFormatPattern());
		else
			dateField.setDateFormat(Utils.DATETIME_FORMAT);
		dateField.setWidth("100%");
		return dateField;
	}

	/**
	 * Oppretter komboboks med true / false verdier
	 * @return combo box for a true/false-property
	 */
	private NativeSelect createBooleanSelect() {
		NativeSelect select = new NativeSelect();
		configureBooleanSelect(select);
		return select;
	}

	/**
	 * @return radio buttons for a true/false-property
	 */
	private OptionGroup createBooleanOptionGroupField() {
		OptionGroup optionGroup = new OptionGroup();
		configureBooleanSelect(optionGroup);
		optionGroup.setMultiSelect(false);
		optionGroup.addStyleName("horizontal");
		return optionGroup;
	}

	private void configureBooleanSelect(AbstractSelect select) {
		select.setNullSelectionAllowed(false);
		select.addItem(false);
		select.addItem(true);
		select.setItemCaption(false, Texts.get("propertyValueFormatter.booleanValue.false"));
		select.setItemCaption(true, Texts.get("propertyValueFormatter.booleanValue.true"));
		select.setImmediate(true);
		select.setWidth("100%");
	}

	/**
	 * Creates a yes/no field
	 * @return checkbox with the right value; true = checked, false = unchecked
	 */
	private CheckBox createCheckBoxField() {
		CheckBox booleanComboBox = new CheckBox();
		booleanComboBox.setImmediate(true);
		booleanComboBox.setWidth("100%");
		return booleanComboBox;
	}

	/**
	 * Oppretter komboboks med verdier fra property sine options
	 * @param options options til Property
	 * @return propertyOptionsComboBox, combobox som inneholder alle options i Property
	 */
	private ComboBox createPropertyOptionsComboboxField(List<String> options) {
		ComboBox propertyOptionsComboBox = new ComboBox();
		propertyOptionsComboBox.setNullSelectionAllowed(false);
		for (Object option : options) {
			propertyOptionsComboBox.addItem(option);
		}
		propertyOptionsComboBox.setImmediate(true);
		propertyOptionsComboBox.setWidth("100%");
		return propertyOptionsComboBox;
	}

	private TextField createTextField(Class<?> dataType) {

		TextField field = new TextField();
		if (dataType == Double.class) {
			field.setConverter(new PlainDoubleToStringConverter());
		} else if (dataType == Long.class) {
			field.setConverter(new StringToLongConverter());
		}

		field.setNullRepresentation("");
		field.setImmediate(true);
		field.setWidth("100%");
		return field;
	}

	public Class<? extends AbstractPropertyValue> getType() {
		return AbstractPropertyValue.class;
	}

	protected Component initContent() {
		return field;
	}

	public void setValue(AbstractPropertyValue newFieldValue) throws com.vaadin.v7.data.Property.ReadOnlyException,
			ConversionException {
		propertyValue = newFieldValue;
		if (propertyValue != null)
			((AbstractField<?>) field).setConvertedValue(propertyValue.getValue());
		else
			((AbstractField<?>) field).setConvertedValue(null);
	}
		
	protected AbstractPropertyValue getInternalValue() {
		if (propertyValue == null)
			propertyValue = property.createValue();
		propertyValue.setValueAsObject(field.getValue());
		return propertyValue;
	}

	public void validate() throws InvalidValueException {
		field.validate();
	}
	
	public void setRequired(boolean required) {
		super.setRequired(required);
		field.setRequired(required);
	}

	public boolean isShowTooltip() {
		return showTooltip;
	}

	public void setShowTooltip(boolean showTooltip) {
		this.showTooltip = showTooltip;
	}
	
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		field.setReadOnly(readOnly);
	}

	public Property getProperty() {
		return property;
	}

	public Class<? extends AbstractField> getComponentType() {
		if (field == null) {
			return null;
		}
		return field.getClass();
	}
}
