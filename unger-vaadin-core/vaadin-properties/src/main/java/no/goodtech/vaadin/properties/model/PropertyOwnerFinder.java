package no.goodtech.vaadin.properties.model;

import java.util.ArrayList;
import java.util.List;

import no.cronus.common.utils.ReflectionUtils;
import no.goodtech.persistence.entity.Entity;
import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.persistence.jpa.AbstractFinder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Baseklasse for søk på entiteter på bakgrunn av deres egenskaper 
 * @author oystein
 *
 * @param <ENTITY> entiteten
 * @param <ENTITYSTUB> kort-versjonen av entiteten
 * @param <FINDER> finder-klassen som arver fra denne klassen 
 */
public abstract class PropertyOwnerFinder<ENTITY extends Entity, ENTITYSTUB extends EntityStub<?>, FINDER> 
extends AbstractFinder<ENTITY, ENTITYSTUB, FINDER> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Class<ENTITY> entityType;
	private List<EntityStub<?>> inheritanceSources = new ArrayList<EntityStub<?>>();

	@SuppressWarnings("unchecked")
	protected PropertyOwnerFinder(String selectFromClause, String alias) {
		super(selectFromClause, alias);
		if (ReflectionUtils.hasParameterizedType(this, 0))
			entityType = (Class<ENTITY>) ReflectionUtils.getParameterizedType(this, 0);
	}

	/**
	 * Finn entiteter på bakgrunn av angitt egenskap
	 * @param property egenskapen
	 * @param value verdien til egenskapen
	 * @return søk med filter på denne egenskapen
	 */
	public FINDER setProperty(Property property, String value) {
		return setProperty(new PropertyValueFinder().setProperty(property), value);
	}
	
	/**
	 * Finn entiteter på bakgrunn av angitt egenskap
	 * @param propertyId ID til egenskapen
	 * @param value verdien til egenskapen
	 * @return søk med filter på denne egenskapen
	 */
	@Override
	protected FINDER setProperty(String propertyId, String value) {
		return setProperty(new PropertyValueFinder().setPropertyId(propertyId), value);
	}
	
	@SuppressWarnings("unchecked")
	private FINDER setProperty(PropertyValueFinder propertyValueFinder, String value) {
		final List<Long> ownerPks = propertyValueFinder.setValue(value).setOwnerClass(entityType).listOwnerPks(); 
		if (ownerPks.size() == 0) {
			logger.debug("Fant ingen eiere som matchet " + propertyValueFinder);
			addNullFilter(prefixWithAlias("pk"));
		} else {
			addInFilter(prefixWithAlias("pk"), ownerPks);
		}
		return (FINDER) this;
	}
	
	/**
	 * Søk også i arvede verdier
	 * @param inheritanceSources objekter man kan arve verdier fra. Rekkefølgen i lista styrer arverekkefølgen.
	 * Nærmeste kilde først, deretter de fjernere kildene. Hvis en egenskap eies av flere kilder, 
	 * er det egenskapen til den nærmeste kilden (den som ligger først i lista) som blir returnert.
	 * TODO: Arv fra flere likestilte kilder er ikke støttet ennå
	 * @return søk med dette filteret lagt til
	 */
	@SuppressWarnings("unchecked")
	public FINDER setMayInheritFrom(List<? extends EntityStub<?>> inheritanceSources) {
		this.inheritanceSources.addAll(inheritanceSources);
		return (FINDER) this;
	}
	
//	/**
//	 * Override denne hvis du ønsker å søke også i arvede egenskaper.
//	 * TODO! 
//	 * @return søk med dette filteret lagt til
//	 */
//	@SuppressWarnings("unchecked")
//	public FINDER setFindInheritedProperties() {
//		return (FINDER) this;
//	}
}
