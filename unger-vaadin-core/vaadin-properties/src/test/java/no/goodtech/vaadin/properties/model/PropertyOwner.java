package no.goodtech.vaadin.properties.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;

import no.goodtech.persistence.entity.AbstractEntityImpl;
import no.goodtech.persistence.entity.EntityStub;

/**
 * En test-entitet
 * @author oystein
 */
@Entity
public class PropertyOwner extends AbstractEntityImpl<PropertyOwner> implements PropertySource {

	private String id;
	
	@Transient
	transient List<EntityStub<?>> propertyHeirs = new ArrayList<EntityStub<?>>(); //objekter som kan arve fra meg
	
	@Transient
	transient List<EntityStub<?>> propertySources = new ArrayList<EntityStub<?>>(); //objekter jeg kan arve fra

	/**
	 * Opprett objekt
	 */
	public PropertyOwner() {
	}
	
	/**
	 * Opprett objekt med ID
	 * @param id ID
	 */
	public PropertyOwner(String id) {
		this.id = id;
	}

	@Override
	public String getNiceClassName() {
		return "Owner";
	}
	
	/**
	 * @return ID
	 */
	public String getId() {
		return id;
	}

	@Override
	public Collection<Long> listPropertyHeirPks() {
		Collection<Long> result = new ArrayList<Long>();
		for (EntityStub<?> heir : propertyHeirs)
			result.add(heir.getPk());
		return result;
	}
}
