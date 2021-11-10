package no.goodtech.vaadin.properties.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.persistence.jpa.AbstractFinder;


/**
 * Bruk denne for å finne verdiene til kunde-spesifikke egenskaper 
 * @author oystein
 */
public class PropertyValueFinder extends AbstractFinder<PropertyValue, PropertyValue, PropertyValueFinder> {

	PropertyInheritanceUtils inheritanceUtils = null;
	
	/**
	 * Opprett søk
	 */
	public PropertyValueFinder() {
		super("select v from PropertyValue v", "v");
	}

	/**
	 * Filter på eier av egenskapene.
	 * @param owner eier av egenskapene
	 * @return søk med dette filteret lagt til
	 */
	public PropertyValueFinder setOwner(EntityStub<?> owner) {
		if (owner == null) {
			addNullFilter(prefixWithAlias("ownerPk"));
			addNullFilter(prefixWithAlias("ownerClass"));
		} else {
			addEqualFilter(prefixWithAlias("ownerPk"), owner.getPk());
			addEqualFilter(prefixWithAlias("ownerClass"), owner.getClass());
		}
		return this;
	}

	/**
	 * Ta med arvede verdier
	 * @param inheritanceSources objekter man kan arve verdier fra. Rekkefølgen i lista styrer arverekkefølgen.
	 * Nærmeste kilde først, deretter de fjernere kildene. Hvis en egenskap eies av flere kilder, 
	 * er det egenskapen til den nærmeste kilden (den som ligger først i lista) som blir returnert.
	 * TODO: Arv fra flere likestilte kilder er ikke støttet ennå
	 * @return søk med dette filteret lagt til
	 */
	public PropertyValueFinder setMayInheritFrom(List<? extends EntityStub<?>> inheritanceSources) {
		inheritanceUtils = new PropertyInheritanceUtils(inheritanceSources);
		return this;
	}

	/**
	 * Filter på type eier av egenskapene.
	 * @param ownerClass objekttypen som eier egenskapene
	 * @return søk med dette filteret lagt til
	 */
	public PropertyValueFinder setOwnerClass(Class<?> ownerClass) {
		if (ownerClass == null)
			addNullFilter(prefixWithAlias("ownerClass"));
		else
			addEqualFilter(prefixWithAlias("ownerClass"), ownerClass);
		return this;
	}

	/**
	 * Will find properties owned by these.
	 * All the owners must be of the same class 
	 * @throws IllegalArgumentException if not all owners are of the same class
	 */
	public PropertyValueFinder setOwners(Collection<? extends EntityStub<?>> owners) {
		if (owners == null || owners.size() == 0) {
			addNullFilter(prefixWithAlias("ownerPk"));
		} else {
			List<Long> ownerPks = new ArrayList<>();
			Set<Class<?>> ownerClasses = new HashSet<>();
			for (EntityStub<?> owner : owners) {
				if(owner != null) {
					ownerClasses.add(owner.getClass());
					ownerPks.add(owner.getPk());
				}
			}
			if (ownerClasses.size() > 1)
				throw new IllegalArgumentException("Owners should be the same class, but they are of these different classes: " + ownerClasses);

			setOwnerClass(ownerClasses.iterator().next());
			addInFilter(prefixWithAlias("ownerPk"), ownerPks.toArray());
		}
		return this;
	}

	/**
	 * Filter på eier av egenskapene. Bør kombineres med {@link #setOwnerClass(Class)}
	 * @param ownerPks primærnøkler til eier(e) av egenskapene
	 * @return søk med dette filteret lagt til
	 */
	public PropertyValueFinder setOwnerPk(Collection<Long> ownerPks) {
		if (ownerPks == null)
			addNullFilter(prefixWithAlias("ownerPk"));
		else
			addInFilter(prefixWithAlias("ownerPk"), ownerPks.toArray());
		return this;
	}

	public PropertyValueFinder setOwnerPk(final Long ... ownerPks) {
		if (ownerPks == null) {
			addNullFilter(prefixWithAlias("ownerPk"));
		} else if (ownerPks.length == 1) {
			addEqualFilter(prefixWithAlias("ownerPk"), ownerPks[0]);
		} else {
			addInFilter(prefixWithAlias("ownerPk"), ownerPks);
		}
		return this;
	}

	/**
	 * Filter på egenskap
	 * @param property egenskap som skal filtreres på
	 * @return søk med dette filteret lagt til
	 */
	public PropertyValueFinder setProperty(PropertyStub property) {
		if (property == null)
			addNullFilter(prefixWithAlias("property"));
		else
			addEqualFilter(prefixWithAlias("property.pk"), property.getPk());
		return this;
	}

	/**
	 * Filter by multiple properties
	 * @param properties the properties you want
	 * @return søk med dette filteret lagt til
	 */
	public PropertyValueFinder setProperties(Collection<? extends PropertyStub> properties) {
		if (properties == null)
			addNullFilter(prefixWithAlias("property"));
		else if (properties.size() == 1)
			addEqualFilter(prefixWithAlias("property.pk"), properties.iterator().next().getPk());
		else
			addInFilter(prefixWithAlias("property.pk"), getPkList(properties));
		return this;
	}

	/**
	 * Filter på egenskap
	 * @param id ID til egenskap som skal filtreres på (eller liste av id)
	 * @return søk med dette filteret lagt til
	 */
	public PropertyValueFinder setPropertyId(String... id) {
		if (id == null)
			addNullFilter(prefixWithAlias("property"));
		else if (id.length == 1)
			addEqualFilter(prefixWithAlias("property.id"), id[0]);
		else
			addInFilter(prefixWithAlias("property.id"), id);
		return this;
	}

	/**
	 * Filter på verdi
	 * @param value verdi til egenskap som skal filtreres på. Du kan bruke jokertegn
	 * @return søk med dette filteret lagt til
	 */
	public PropertyValueFinder setValue(String value) {
		if (value == null)
			addNullFilter(prefixWithAlias("value"));
		else
			addLikeFilter(prefixWithAlias("value"), value, false);
		return this;
	}
	
	/**
	 * Filter på verdi
	 * @param value verdi til egenskap som skal filtreres på. Du kan bruke jokertegn
	 * @return søk med dette filteret lagt til
	 */
	public PropertyValueFinder setValueNotLike(String value) {
		if (value == null)
			addNotNullFilter(prefixWithAlias("value"));
		else
			addNotLikeFilter(prefixWithAlias("value"), value, false);
		return this;
	}
	
	/**
	 * Gir deg oppslags-tabell over verdi per egenskap.
	 * Også evt. arvede verdier blir tatt med, mens egenskaper som ikke er satt, blir ikke tatt med.
	 * NB! Du må selv passe på å sette filteret smalt nok, slik at du ikke risikerer å få treff på flere verdier
	 * på samme egenskap. Om dette vil skje, vil det kastes en RuntimeException.
	 * @return oppslags-tabell over verdi per egenskap. Om 
	 */
	public Map<PropertyStub, PropertyValue> getPropertyValueMap() {
		Map<PropertyStub, PropertyValue> resultMap = new HashMap<>();
        final List<PropertyValue> list = list();
        for (PropertyValue value : list) {
        	final PropertyValue existingValue = resultMap.get(value.getProperty());
        	if (existingValue == null) {
        		resultMap.put(value.getProperty(), value);
        	} else {
        		throw new RuntimeException("Søket returnerte flere verdier per egenskap og det er ikke tillatt " +
        				"Duplikater: " + value.getProperty() + ": Verdier" + value + ", " + existingValue); 
        	}
        }
        return resultMap;
	}
	
	/**
	 * Gives you property value keyed on property ID.
	 * Only the value of the value is returned, like if you called {@link PropertyValueStub#getValue()}
	 * @see PropertyValueFinder#getPropertyValueMap() 
	 */
	public Map<String, Object> getPropertyIdValueMap() {
		Map<String, Object> propertyIdValueMap = new HashMap<>();
		for (Map.Entry<PropertyStub, PropertyValue> entry : getPropertyValueMap().entrySet())
			propertyIdValueMap.put(entry.getKey().getId(), entry.getValue().getValue());
		return propertyIdValueMap;
	}

	/**
	 * @return en liste over pk til alle eiere som tilfredsstiller kriteriene
	 */
	@SuppressWarnings("unchecked")
	public List<Long> listOwnerPks() {
		setSelectFromClause("select new list(ownerPk) from PropertyValue v", "v");
        List<Object> resultList = getRepository().list(this, List.class);
        List<Long> pks = new ArrayList<>();
        for (Object object : resultList) {
       		List<Object> row = (List<Object>) object;
       		Long pk = (Long) row.get(0);
       		pks.add(pk);
        }
        return pks;
	}
	
	@Override
	public List<PropertyValue> list() {
		final List<PropertyValue> values = super.list();
		if (inheritanceUtils != null)
			return inheritanceUtils.applyInheritedStubValues(values);
		return values;
	}
	
	@Override
	public List<PropertyValue> loadList() {
		return list();
	}
	
	public PropertyValues getPropertyValues() {
		return new PropertyValues(list());
	}

	public Map<OwnerKey, List<PropertyValue>> getPropertiesByOwner() {
		Map<OwnerKey, List<PropertyValue>> resultMap = new HashMap<>();
        final List<PropertyValue> list = list();
        for (PropertyValue value : list) {
        	OwnerKey key = new OwnerKey(value);
        	List<PropertyValue> valuesForThisOwner = resultMap.get(key);
        	if (valuesForThisOwner == null)
        		valuesForThisOwner = new ArrayList<>();
    		valuesForThisOwner.add(value);
    		resultMap.put(key, valuesForThisOwner);
        }
        return resultMap;
	}

	/**
	 * Find only values to show in crosstab for given property group
	 * @param propertyClassId the property group that configures which properties to show
	 * @see PropertyMembership#isShowInCrosstab()
	 */
	public PropertyValueFinder setShowInCrosstab(String propertyClassId) {
		addJoin(prefixWithAlias("property.propertyMemberships m"));
		addEqualFilter("m.propertyClass.id", propertyClassId);
		addEqualFilter("m.showInCrosstab", true);
		addSortOrder("m.indexNo", true);
		return this;
	}
}
