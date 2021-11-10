package no.goodtech.vaadin.properties.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import no.cronus.common.utils.CollectionFactory;
import no.goodtech.persistence.entity.AbstractEntityImpl;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * For grouping and filtering available properties for certain views.
 */
@Entity
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class PropertyClass extends AbstractEntityImpl<PropertyClass> implements PropertyClassStub
{
	@Column(name = "id", nullable = false, unique = true)
	private String id;

	private String description;

	@OneToMany(cascade= CascadeType.ALL, mappedBy="propertyClass", orphanRemoval=true)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<PropertyMembership> propertyMemberships = CollectionFactory.getHashSet();

	public PropertyClass(){
	}

	public PropertyClass(final String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getDescription(){
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public void addPropertyMembership(Property property, Boolean required, Boolean editable){
		addPropertyMembership(property, required, editable, null);
	}

	public void addPropertyMembership(Property property, Boolean required, Boolean editable, Integer indexNo){
		//TODO: Sjekk om property er medlem allerede før du prøver å melde inn
		PropertyMembership membership = new PropertyMembership();
		membership.setPropertyClass(this);
		membership.setProperty(property);
		membership.setIndexNo(indexNo);

		if (required == null)
			membership.setRequired(false);
		else
			membership.setRequired(required);
		
		if (editable == null)
			membership.setEditable(false);
		else
			membership.setEditable(editable);
		
		propertyMemberships.add(membership);
	}

	/**
	 * @return all items ordered by index-number, see {@link PropertyMembershipStub#getIndexNo()}
	 */
	public SortedSet<PropertyMembership> getPropertyMemberships(){
		return new TreeSet<>(propertyMemberships);
	}

	public PropertyMembership getPropertyMembership(PropertyStub property) {
		for (PropertyMembership propertyMembership : propertyMemberships) {
			if (property.equals(propertyMembership.getProperty())) {
				return propertyMembership;
			}
		}
		return null;
	}
	
	public List<PropertyStub> getProperties() {
		List<PropertyStub> properties = new ArrayList<PropertyStub>();
		for(PropertyMembership membership: propertyMemberships) {
			properties.add(membership.getProperty());
		}
		return properties;
	}


	/**
	 * TODO: Kan ikke denne gjenbruke 
	 * {@link #getRequiredProperties(Boolean)} og {@link #getEditableProperties(Boolean)}?
	 * 
	 * It returns the properties with the right filters for required and editable.
	 * It does not care about the filterparameter when it is null.
	 * i.e. all PropertyMemberships are added when both required and editable is null.
	 * @param required
	 * @param editable
	 */
	public List<PropertyStub> getProperties(Boolean required, Boolean editable){
		List<PropertyStub> properties = new ArrayList<PropertyStub>();

		for(PropertyMembership membership: propertyMemberships){
			if(required != null){
				if(required.equals(membership.getRequired())) {
					if (editable != null) {
						if(editable.equals(membership.getEditable())){
							properties.add(membership.getProperty()); // both filters ok
						}
					}else {
						properties.add(membership.getProperty()); // required ok, editable null
					}
				}
			}else {
				if(editable != null){
					if(editable.equals(membership.getEditable())){
						properties.add(membership.getProperty()); // required null, editable ok
					}
				}else {
					properties.add(membership.getProperty()); // both null
				}
			}
		}
		return properties;
	}

	public List<PropertyStub> getRequiredProperties(Boolean required){
		List<PropertyStub> properties = new ArrayList<PropertyStub>();
		for(PropertyMembership membership: Collections.unmodifiableSet(propertyMemberships)){
			if(required == null || required.equals(membership.getRequired()))
				properties.add(membership.getProperty());
		}
		return properties;
	}

	public List<PropertyStub> getEditableProperties(Boolean editable){
		List<PropertyStub> properties = new ArrayList<PropertyStub>();
		for(PropertyMembership membership: Collections.unmodifiableSet(propertyMemberships)){
			if(editable == null || editable.equals(membership.getEditable()))
				properties.add(membership.getProperty());
		}
		return properties;
	}
	
	public void lazyLoad() {
		propertyMemberships.size();
	}
}
