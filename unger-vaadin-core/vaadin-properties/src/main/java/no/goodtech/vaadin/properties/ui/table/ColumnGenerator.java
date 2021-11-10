package no.goodtech.vaadin.properties.ui.table;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;
import no.goodtech.vaadin.properties.model.Property;

import java.util.HashMap;
import java.util.Map;

/**
 * Brukes til å generere kolonnen med checkboxer og dropdowns
 */
public class ColumnGenerator implements Table.ColumnGenerator {
	//Hashmap hvor property er nøkkelen og komponenten som inneholder propertien er verdien
	private final Map<Property, Component> propertyComponentMap;

	public ColumnGenerator() {
		propertyComponentMap = new HashMap<Property, Component>();
	}

	/**
	 * Genererer celler i treet, kontrollerer hvilken datatype propertien har
	 * og returnerer en type komponent på grunnlag av det, legger propertien i komponenten til i mappet.
	 *
	 * @return komponenten som skal plasseres i cellen
	 */
	@Override
	public Object generateCell(Table source, Object itemId, Object columnId) {
		//VerticalLayout slik at hver egenskapskomponent får en overskrift med ID'en til egenskapen
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setMargin(false);
		verticalLayout.setSpacing(false);
		Property property = (Property) itemId;
		verticalLayout.addComponent(new Label(property.getId()));
		if (property.getDataType().equals(Boolean.class)) {
			if (propertyComponentMap.containsKey(property)) {
				return propertyComponentMap.get(property);
			} else {
				ComboBox booleanSelect = new ComboBox();
				booleanSelect.setImmediate(true);
				booleanSelect.addItem(true);
				booleanSelect.addItem(false);
				booleanSelect.setItemCaption(true, "Ja");
				booleanSelect.setItemCaption(false, "Nei");
				propertyComponentMap.put(property, booleanSelect);
				verticalLayout.addComponent(booleanSelect);
				return verticalLayout;
			}
		} else if (property.getDataType().equals(Integer.class)) {
			if (propertyComponentMap.containsKey(property)) {
				return propertyComponentMap.get(property);
			} else {
				TextField tf = new TextField();
				propertyComponentMap.put(property, tf);
				verticalLayout.addComponent(tf);
				return verticalLayout;
			}
		} else {
			if (!property.getOptionsList().isEmpty()) {
				final ComboBox select = new ComboBox();
				select.setWidth(200, Sizeable.Unit.PIXELS);
				select.setImmediate(true);
				for (String option : property.getOptionsList()) {
					select.addItem(option);
					select.setImmediate(true);
					select.setItemCaption(option, option); //TODO: Er denne nødvendig?
				}
				select.addValueChangeListener(new com.vaadin.v7.data.Property.ValueChangeListener() {
					@Override
					public void valueChange(com.vaadin.v7.data.Property.ValueChangeEvent event) {
					}
				});
				propertyComponentMap.put(property, select);
				verticalLayout.addComponent(select);
				return verticalLayout;
			} else {
				TextField tf = new TextField();
				propertyComponentMap.put(property, tf);
				verticalLayout.addComponent(tf);
				return verticalLayout;
			}
		}
	}

	/**
	 * Returnerer hashmapet med properties og komponeneter
	 *
	 * @return map med property som nøkkel og komponent som verdi
	 */
	public Map<Property, Component> getSearchCriteria() {
		return propertyComponentMap;
	}
}
