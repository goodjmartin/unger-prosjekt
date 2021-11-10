package no.goodtech.vaadin.properties.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import no.goodtech.persistence.jpa.AbstractFinder;
import no.goodtech.vaadin.search.FilterPanel.IMaxRowsAware;

public class PropertyMembershipFinder 
extends AbstractFinder<PropertyMembership, PropertyMembershipStub, PropertyMembershipFinder>
implements IMaxRowsAware
{
	public PropertyMembershipFinder() {
		super("select pg from PropertyMembership pg", "pg");
		addSortOrder(prefixWithAlias("indexNo"), true);
	}

	public PropertyMembershipFinder addSortOrderIndexNo(boolean ascending){
		addSortOrder(prefixWithAlias("indexNo"), ascending);
		return this;
	}

	/**
	 * Do not find given membership
	 * @param membership the membership you don't want to find
	 */
	public PropertyMembershipFinder setIgnore(PropertyMembershipStub membership) {
		if (!membership.isNew())
			addNotEqualFilter(prefixWithAlias("pk"), membership.getPk());
		return this;
	}
	
	/**
	 * Set filter for the Property for the membership
	 */
	public PropertyMembershipFinder setPropertyPk(Long pk){
		if (pk == null)
			addNullFilter(prefixWithAlias("property.pk"));
		else
			addEqualFilter(prefixWithAlias("property.pk"), pk);
		return this;
	}

	public PropertyMembershipFinder setProperties(PropertyStub... property){
		if (property != null)
			addInFilter(prefixWithAlias("property.pk"), ((Object[]) getPkList(property)));
		else
			addNullFilter(prefixWithAlias("property.pk"));
		return this;
	}

	public PropertyMembershipFinder setPropertyClassId(String ... propertyClassId){
		if (propertyClassId != null){
			addInFilter(prefixWithAlias("propertyClass.id"), ((Object[]) propertyClassId));
		}
		return this;
	}

	public PropertyMembershipFinder setPropertyClasses(PropertyClassStub... propertyClass){
		if (propertyClass != null)
			addInFilter(prefixWithAlias("propertyClass.pk"), ((Object[]) getPkList(propertyClass)));
		return this;
	}

	/**
	 * Set filter for the PropertyClass for the membership
	 */
	public PropertyMembershipFinder setPropertyClassPk(Long pk){
		if (pk == null)
			addNullFilter(prefixWithAlias("propertyClass_pk"));
		else
			addEqualFilter("propertyClass_pk", pk);
		return this;
	}

	/**
	 * Find all memberhsips with required (true or false)
	 */
	public PropertyMembershipFinder setRequired(Boolean required){
		if(required != null)
			addEqualFilter("required", required);
		else{
			removeFilter("required");
		}
		return this;
	}

	/**
	 * Find all memberhsips with editable (true or false)
	 */
	public PropertyMembershipFinder setEditable(Boolean editable){
		if(editable != null)
			addEqualFilter("editable", editable);
		else{
			removeFilter("editable");
		}
		return this;
	}

	/**
	 * Find all memberhsips with showInCrosstab (true or false)
	 */
	public PropertyMembershipFinder setShowInCrosstab(Boolean showInCrosstab){
		if(showInCrosstab != null)
			addEqualFilter("showInCrosstab", showInCrosstab);
		else{
			removeFilter("showInCrosstab");
		}
		return this;
	}

	/**
	 * @return a linkedhashmap keyed by property and sorted by indexNo
	 */
	public Map<PropertyStub, PropertyMembership> getMembershipByPropertyMap() {
		Map<PropertyStub, PropertyMembership> result = new LinkedHashMap<PropertyStub, PropertyMembership>();
		for (PropertyMembership membership : loadList())
			result.put(membership.getProperty(), membership);
		return result;
	}

	/**
	 * @return all unique properties in the result set
	 */
	public List<PropertyStub> listProperties() {
		LinkedHashSet<PropertyStub> properties = new LinkedHashSet<PropertyStub>();
		for (PropertyMembershipStub membership : list())
			properties.add(membership.getProperty());
		return new ArrayList<PropertyStub>(properties);
	}
	
	/**
	 * @return all unique groups in the result set
	 */
	public List<PropertyClassStub> listPropertyClasses() {
		LinkedHashSet<PropertyClassStub> properties = new LinkedHashSet<PropertyClassStub>();
		for (PropertyMembershipStub membership : list())
			properties.add(membership.getPropertyClass());
		return new ArrayList<PropertyClassStub>(properties);
	}
}
