package no.goodtech.vaadin.properties.ui;

import no.goodtech.vaadin.properties.model.PropertyStub;

/**
 * Used as a POJO to hold columnids for propertyvalues in tables.
 * Gives the opportunity to have the same Property in several columns in a table.
 */
public class PropertyValueColumn {
	PropertyStub property;
	String version;

	/**
	 * Use this if there are only one kind of this Property in the table columns
	 */
	public PropertyValueColumn(PropertyStub property){
		this.property = property;
		this.version = null;
	}

	/**
	 * Use this if there are several kinds of this Property. Use a string to define this special column.
	 */
	public PropertyValueColumn(PropertyStub property, String version){
		this.property = property;
		this.version = version;
	}

	public PropertyStub getProperty() {
		return property;
	}

	public String getVersion() {
		return version;
	}

	/**
	 * @return a default column name
	 */
	public String getDefaultName(){
		return property.getName();
	}
}
