package no.goodtech.vaadin.properties.ui;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.data.util.PropertyValueGenerator;
import com.vaadin.v7.ui.Grid;
import no.goodtech.persistence.entity.Entity;
import no.goodtech.vaadin.properties.model.PropertyStub;
import no.goodtech.vaadin.properties.model.PropertyValueStub;
import no.goodtech.vaadin.properties.model.PropertyValues;

public class PropertyValueGridColumnGenerator {

	private GeneratedPropertyContainer generatedPropertyContainer;
	private PropertyValues propertyValues;

	public PropertyValueGridColumnGenerator(GeneratedPropertyContainer generatedPropertyContainer, PropertyValues propertyValues){
		this.generatedPropertyContainer = generatedPropertyContainer;
		this.propertyValues = propertyValues;
	}

	/**
	 * Adds columns to the generatedPropertyContainer for every property in PropertyValues
	 * @param grid to set header caption on the column
	 */
	public void generateColumns(Grid grid){
		for (PropertyStub property : propertyValues.getProperties()){
			String propertyColumnId = property.getId();
			generatedPropertyContainer.addGeneratedProperty(propertyColumnId, new PropertyValueGenerator<String>() {
				@Override
				public String getValue(final Item item, final Object o, final Object o1) {
					Entity entity = (Entity) o;
					PropertyValueStub value = propertyValues.getValue(entity.getPk(), propertyColumnId);
					if (value != null)
						return (String) value.getValue();
					else
						return "";
				}

				@Override
				public Class<String> getType() {
					return String.class;
				}
			});

			Grid.Column column = grid.getColumn(propertyColumnId);
			if(column != null) {
				column.setHeaderCaption(property.getName());
			}
		}
	}
}
