package no.goodtech.vaadin.properties.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import no.goodtech.persistence.entity.AbstractEntityImpl;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
public class PropertyMembership extends AbstractEntityImpl<PropertyMembership> 
implements PropertyMembershipStub, Comparable<PropertyMembership> {
	
	private Boolean required;

	private Boolean editable;

	private Boolean showInCrosstab;

	private Integer indexNo;

	@NotNull
	@ManyToOne
	@Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
	private PropertyClass propertyClass;

	@NotNull
	@ManyToOne
	@Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
	private Property property;

	public PropertyMembership() {
	}

	public boolean getEditable() {
		return isEditable();
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean getRequired() {
		return isRequired();
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public void setShowInCrosstab(boolean showInCrosstab) {
		this.showInCrosstab = showInCrosstab;
	}

	public boolean isRequired() {
		if(required == null)
			return false;
		return required;
	}

	public boolean isEditable() {
		if(editable == null)
			return false;
		return editable;
	}

	public boolean isShowInCrosstab() {
		if(showInCrosstab == null)
			return false;
		return showInCrosstab;
	}

	public PropertyStub getProperty()
	{
		return property;
	}

	public void setProperty(PropertyStub property)
	{
		this.property = (Property) property;
	}

	public PropertyClassStub getPropertyClass()
	{
		return propertyClass;
	}

	public void setPropertyClass(PropertyClassStub propertyClass) {
		this.propertyClass = (PropertyClass) propertyClass;
	}

	public Integer getIndexNo() {
		return indexNo;
	}

	public void setIndexNo(Integer indexNo) {
		this.indexNo = indexNo;
	}

	public int compareTo(PropertyMembership other) {
		if (indexNo == null || other.indexNo == null)
			return 0;
		return indexNo - other.indexNo;
	}
}
