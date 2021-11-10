package no.goodtech.vaadin.properties.ui.table;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;
import no.goodtech.vaadin.properties.model.AbstractPropertyValue;
import no.goodtech.vaadin.properties.model.Property;
import no.goodtech.vaadin.properties.model.PropertyValue;
import no.goodtech.vaadin.properties.model.PropertyValueStub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyContainer extends IndexedContainer {
	public static final String PROPERTY_ID = "ID";
	public static final Object PROPERTY_VALUE = "Verdi";
	//public static final Object PROPERTY_VALUE_DESCRIPTION = "Verdi-beskrivelse";
	public static final Object PROPERTY_INHERITED = "Arvet";
	//Eierklassen
	private Class<?> ownerClass;
	//EierPk
	private Long ownerPk;

	private List<Field> fieldsToValidate = new ArrayList<Field>();

	//Map med nøkkel for komponenter som arver verdi, verdi for hva den arvede verdien er
	Map<Component, Object> inheritedValueMap = new HashMap<Component, Object>();

	public PropertyContainer() {
		addContainerProperty(PROPERTY_ID, String.class, null);
		addContainerProperty(PROPERTY_VALUE, Component.class, null);
		addContainerProperty(PROPERTY_INHERITED, Object.class, null);
		//	addContainerProperty(PROPERTY_VALUE_DESCRIPTION, Object.class, null);
	}

	/**
	 * Legger til nye propertyValues
	 *
	 * @param propertyValues liste over propertyValues
	 * @param ownerClass     eierklassen
	 * @param ownerPk        eierPk
	 */
	public void refreshPropertyTableContainer(List<PropertyValueStub> propertyValues, Class<?> ownerClass, Long ownerPk) {
		this.ownerClass = ownerClass;
		this.ownerPk = ownerPk;
		removeAllItems();
		fieldsToValidate.clear();
		for (PropertyValueStub propertyValue : propertyValues) {
			final Item item = addItem(propertyValue);
			Class propertyValueDataType = propertyValue.getProperty().getDataType();
			item.getItemProperty(PROPERTY_ID).setValue(propertyValue.getProperty().getId());
			if (propertyValueDataType.equals(Boolean.class)) {
				item.getItemProperty(PROPERTY_VALUE).setValue(createBooleanPropertyValueSelect(propertyValue.load()));
			} else if (propertyValueDataType.equals(Integer.class)) {
				item.getItemProperty(PROPERTY_VALUE).setValue(createPropertyValueTextField(propertyValue.load()));
			} else {
				if (propertyValue.getProperty().getOptionsList().isEmpty()) {
					item.getItemProperty(PROPERTY_VALUE).setValue(createPropertyValueTextField(propertyValue.load()));
				} else {
					item.getItemProperty(PROPERTY_VALUE).setValue(createPropertyValueSelect(propertyValue.load()));
				}
			}
			if (propertyValue.isInherited()) {
				item.getItemProperty(PROPERTY_INHERITED).setValue("ja");
			}
		}
		sort(new Object[]{PROPERTY_ID}, new boolean[]{true});

	}

	/**
	 * Legger til properties så lenge det ikke eksisterer en propertyvalue av den typen
	 *
	 * @param properties egenskaper
	 */
	public void showAllProperties(List<Property> properties) {
		List<Object> itemIds = getAllItemIds();
		List<Property> propertiesWithValue = new ArrayList<Property>();
		for (Object item : itemIds) {
			if (item.getClass().equals(PropertyValue.class)) {
				AbstractPropertyValue propertyValue = (AbstractPropertyValue) item;
				propertiesWithValue.add(propertyValue.getProperty().load());
			}
		}
		for (Property property : properties) {
			if (!propertiesWithValue.contains(property)) {
				removeItem(property);
				final Item item = addItem(property);
				Class aClass = property.getDataType();
				item.getItemProperty(PROPERTY_ID).setValue(property.getId());
				if (aClass.equals(Boolean.class)) {
					item.getItemProperty(PROPERTY_VALUE).setValue(createBooleanPropertySelect());
				} else if (aClass.equals(Integer.class)) {
					item.getItemProperty(PROPERTY_VALUE).setValue(createPropertyTextField(property));
				} else {
					if (property.getOptionsList().isEmpty()) {
						item.getItemProperty(PROPERTY_VALUE).setValue(createPropertyTextField(property));
					} else {
						item.getItemProperty(PROPERTY_VALUE).setValue(createPropertySelect(property));
					}
				}
			}
		}
		sort(new Object[]{PROPERTY_ID}, new boolean[]{true});
	}

	public void addProperty(Property property) {
		Item item = addItem(property);
		item.getItemProperty(PROPERTY_ID).setValue(property.getId());
		sort(new Object[]{PROPERTY_ID}, new boolean[]{true});

	}

	private Component createBooleanPropertySelect() {
		final ComboBox booleanSelect = new ComboBox();
		booleanSelect.addItem(true);
		booleanSelect.addItem(false);
		booleanSelect.setItemCaption(true, "Ja");
		booleanSelect.setItemCaption(false, "Nei");
		booleanSelect.setImmediate(true);
		return booleanSelect;
	}


	private Component createBooleanPropertyValueSelect(final PropertyValue propertyValue) {
		final ComboBox propertyValueSelect = new ComboBox();
		propertyValueSelect.setNullSelectionAllowed(false);
		propertyValueSelect.setImmediate(true);
		propertyValueSelect.addItem(true);
		propertyValueSelect.addItem(false);
		propertyValueSelect.setItemCaption(true, "Ja");
		propertyValueSelect.setItemCaption(false, "Nei");
		propertyValueSelect.setImmediate(true);
		if (propertyValue.validate() == null) {
			propertyValueSelect.setValue(propertyValue.getValue());
		}
		//I de tilfellene hvor det endres fra feks tekstfelt til select
		if (!propertyValue.getValue().getClass().equals(boolean.class)) {
			propertyValueSelect.addItem(propertyValue.getValue());
			propertyValueSelect.select(propertyValue.getValue());
		}
		propertyValueSelect.setWidth(350, Sizeable.Unit.PIXELS);
		return propertyValueSelect;
	}

	private ComboBox createPropertyValueSelect(final AbstractPropertyValue propertyValue) {
		final ComboBox propertyValueSelect = new ComboBox();
		propertyValueSelect.setImmediate(true);
		propertyValueSelect.setNullSelectionAllowed(false);
		List<String> options = propertyValue.getProperty().getOptionsList();
		for (String option : options) {
			propertyValueSelect.addItem(option);
		}
		propertyValueSelect.addValueChangeListener(new com.vaadin.v7.data.Property.ValueChangeListener() {
			@Override
			public void valueChange(com.vaadin.v7.data.Property.ValueChangeEvent event) {
				//		getContainerProperty(propertyValue, PROPERTY_VALUE_DESCRIPTION).setValue(optionDescriptionMap.get(propertyValueSelect.getValue()));
			}
		});
		//I de tilfellene hvor det endres fra feks tekstfelt til select
		if (!options.contains(propertyValue.toString())) {
			propertyValueSelect.addItem(propertyValue.toString());
			propertyValueSelect.select(propertyValue.toString());
			if (propertyValue.toString().equals("true")) {
				propertyValueSelect.setItemCaption("true", "Ja");
			} else if (propertyValue.toString().equals("false")) {
				propertyValueSelect.setItemCaption("false", "Nei");
			}
		} else {
			propertyValueSelect.select(propertyValue.toString());
		}
		propertyValueSelect.setWidth(350, Sizeable.Unit.PIXELS);
		return propertyValueSelect;
	}

	private ComboBox createPropertySelect(final no.goodtech.vaadin.properties.model.Property itemId) {
		final ComboBox propertyValueSelect = new ComboBox();
		propertyValueSelect.setImmediate(true);
		List<String> options = itemId.getOptionsList();
		for (String option : options) {
			propertyValueSelect.addItem(option);
		}
		propertyValueSelect.addValueChangeListener(new com.vaadin.v7.data.Property.ValueChangeListener() {
			@Override
			public void valueChange(com.vaadin.v7.data.Property.ValueChangeEvent event) {
				//		getContainerProperty(itemId, PROPERTY_VALUE_DESCRIPTION).setValue(optionDescriptionMap.get(propertyValueSelect.getValue()));
			}
		});
		return propertyValueSelect;
	}

	/**
	 * Lag et textfelt for properties
	 *
	 * @return tekstfeltet
	 */
	private TextField createPropertyTextField(final Property property) {
		final TextField textField = new TextField();
		textField.setImmediate(true);
		textField.setWidth(350, Sizeable.Unit.PIXELS);
		final PropertyValue dummyValue = property.createValue();
		dummyValue.setOwnerPk(ownerPk);
		dummyValue.setOwnerClass(ownerClass);
		textField.addValidator(createPropertyValueValidator(dummyValue));
		fieldsToValidate.add(textField);
		return textField;
	}

	/**
	 * Lag et tekstfelt for propertyValues
	 *
	 * @param itemId PropertyValue
	 * @return tekstfelt for innskriving av propertyvalue
	 */
	private TextField createPropertyValueTextField(final Object itemId) {
		final TextField textField = new TextField();
		textField.setImmediate(true);
		textField.setWidth(350, Sizeable.Unit.PIXELS);
		//Sett verdien til propertyValue sin verdi
		final PropertyValue propertyValue = (PropertyValue) itemId;
		textField.setValue(propertyValue.toString());
		textField.setData(itemId);
		textField.setNullRepresentation("");
		textField.addValidator(createPropertyValueValidator(propertyValue));
		fieldsToValidate.add(textField);
		return textField;
	}

	private Validator createPropertyValueValidator(final PropertyValue propertyValue) {
		return new Validator() {
			@Override
			public void validate(Object value) throws InvalidValueException {
				//TODO fører til problem ved visning av potensielle egenskaper for et objekt
				if (value != null && !value.equals("")) {
					propertyValue.setValue(value.toString());
				final String errorMessage = propertyValue.validate();
					if (errorMessage != null)
						throw new InvalidValueException(errorMessage);
				}
			}
		};
	}

	public void setOwner(Long pk, Class<?> ownerClass) {
		this.ownerPk = pk;
		this.ownerClass = ownerClass;

	}

	/**
	 * Validerer alle feltene
	 *
	 * @return null om alt er ok, eller feilmelding om jeg ikke er fornøyd.
	 */
	public String validate() {
		try {
			for (Field field : fieldsToValidate)
				field.validate();
		} catch (InvalidValueException e) {
			return e.getMessage();
		}
		return null;
	}

	public void save() {
		for (Object itemID : getAllItemIds()) {
			if (itemID instanceof Property) {
				Property property = (Property) itemID;
				com.vaadin.v7.data.Property itemPropertyValue = getContainerProperty(itemID, PROPERTY_VALUE);
				Component component = (Component) itemPropertyValue.getValue();
				if (component instanceof TextField) {
					TextField textField = (TextField) component;
					if (textField.getValue() != null && !textField.getValue().isEmpty()) {
						//Lagre egenskapen hvis den ikke arves, eller hvis den overskriver arven
						if (!(inheritedValueMap.containsKey(component)) || !(inheritedValueMap.get(component).equals(textField.getValue()))) {
							PropertyValue propertyValue = new PropertyValue();
							propertyValue.setOwnerClass(ownerClass);
							propertyValue.setOwnerPk(ownerPk);
							propertyValue.setProperty(property);
							propertyValue.setValue(textField.getValue());
							propertyValue.save();
						}
					}
				} else if (component instanceof ComboBox) {
					ComboBox comboBox = (ComboBox) component;
					if (comboBox.getValue() != null && !comboBox.getValue().toString().isEmpty()) {
						//Lagre egenskapen hvis den ikke arves, eller hvis den overskriver arven
						if (!(inheritedValueMap.containsKey(component)) || !(inheritedValueMap.get(component).equals(comboBox.getValue().toString()))) {
							PropertyValue propertyValue = new PropertyValue();
							propertyValue.setOwnerClass(ownerClass);
							propertyValue.setOwnerPk(ownerPk);
							propertyValue.setProperty(property);
							propertyValue.setValue(comboBox.getValue().toString());
							propertyValue.save();
						}
					}
				}
			} else if (itemID instanceof PropertyValue) {
				PropertyValue propertyValue = (PropertyValue) itemID;
				Object previousPropertyValue = propertyValue.toString();
				if (propertyValue.getOwnerClass() != ownerClass || !propertyValue.getOwnerPk().equals(ownerPk)) {
					Property inheritedPropertyValueType = propertyValue.getProperty().load();
					propertyValue = new PropertyValue();
					propertyValue.setProperty(inheritedPropertyValueType);
					propertyValue.setOwnerPk(ownerPk);
					propertyValue.setOwnerClass(ownerClass);//NULL ?
				}
				com.vaadin.v7.data.Property itemPropertyValue = getContainerProperty(itemID, PROPERTY_VALUE);
				Component component = (Component) itemPropertyValue.getValue();
				if (component instanceof TextField) {
					TextField textField = (TextField) component;
					if (textField.getValue() != null && !textField.getValue().isEmpty()) {
						if (previousPropertyValue == null || !previousPropertyValue.equals(textField.getValue())) {
							propertyValue.setValue(textField.getValue());
							propertyValue.save();
						}
					}
				} else if (component instanceof ComboBox) {
					ComboBox comboBox = (ComboBox) component;
					if (comboBox.getValue() != null && !comboBox.getValue().toString().isEmpty()) {
						if (previousPropertyValue == null || !previousPropertyValue.toString().equals(comboBox.getValue().toString())) {
							propertyValue.setValue(comboBox.getValue().toString());
							propertyValue.save();
						}
					}
				}
			}
		}
	}
	public void saveProperties(Long pk, Class<?> ownerClass) {
		setOwner(pk, ownerClass);
		save();
	}

	public void propertySaved(Property property, List<Property> propertiesList) {
		showAllProperties(propertiesList);
	}

	/**
	 * Viser de arvede egenskapene
	 * TODO: nils spesifikk, ta ut
	 * @param inheritedPropertyValues arvede egenskaper
	 */
	public void displayCollectionPropertyValues(List<PropertyValue> inheritedPropertyValues) {
		discardCollectionPropertyValues();
		for (AbstractPropertyValue propertyValue : inheritedPropertyValues) {
			if (containsId(propertyValue.getProperty())) {
				com.vaadin.v7.data.Property itemPropertyValue = getContainerProperty(propertyValue.getProperty(),
						PROPERTY_VALUE);
				Component component = (Component) itemPropertyValue.getValue();
				if (component instanceof TextField) {
					TextField textField = (TextField) component;
					textField.setValue(propertyValue.getValue().toString());
					inheritedValueMap.put(component, propertyValue.getValue());
				} else if (component instanceof ComboBox) {
					ComboBox comboBox = (ComboBox) component;
					comboBox.setValue(propertyValue.getValue());
					inheritedValueMap.put(component, propertyValue.getValue().toString());
				}
			}
		}
	}

	/**
	 * Fjerner arvede verdier fra komponenter
	 */
	public void discardCollectionPropertyValues() {
		for (Map.Entry<Component, Object> entry : inheritedValueMap.entrySet()) {
			Component key = entry.getKey();
			if (key instanceof TextField) {
				TextField textField = (TextField) key;
				textField.setValue(null);
			} else if (key instanceof ComboBox) {
				ComboBox comboBox = (ComboBox) key;
				comboBox.setValue(null);
			}
			inheritedValueMap = new HashMap<Component, Object>();
		}
	}
}
