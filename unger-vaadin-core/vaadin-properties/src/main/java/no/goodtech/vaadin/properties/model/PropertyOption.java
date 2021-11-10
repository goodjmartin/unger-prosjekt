package no.goodtech.vaadin.properties.model;

import javax.persistence.ManyToOne;

import no.goodtech.persistence.entity.AbstractSimpleEntityImpl;

/**
 * En "lovlig verdi" for en {@link Property}
 * Brukes av egenskaper hvor bruker skal velge mellom et sett av aktuelle alternativer
 * @author oystein
 * @deprecated denne skal antakelig ikke brukes
 */
public class PropertyOption extends AbstractSimpleEntityImpl {

	@ManyToOne
	private Property property;
	
	private String value, description;
	
	private Integer index;
	
	@Override
	public String getNiceClassName() {
		return "Alternativ";
	}

	public PropertyStub getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}
}
