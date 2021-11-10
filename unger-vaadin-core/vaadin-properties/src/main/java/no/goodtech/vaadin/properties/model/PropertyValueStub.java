package no.goodtech.vaadin.properties.model;

import no.goodtech.persistence.entity.Entity;
import no.goodtech.persistence.entity.EntityStub;

/**
 * En forenklet utgave av en {@link PropertyValue}. Bruk {@link #load()} for å få tilgang til mer funksjonalitet
 * @author oystein
 * @deprecated this is not needed because PropertyValue doesn't have any one-to-many-relationships
 */
public interface PropertyValueStub extends EntityStub<PropertyValue> {
	
	/**
	 * @return egenskapen som denne verdien hører til
	 */
	PropertyStub getProperty();

	/**
	 * @return verdien. Om datatypen er noe annet enn tekst, vil jeg forsøke å instansiere et objekt av korrekt type
	 */
	Object getValue();

	/**
	 * @return objekttypen til eier av denne verdien
	 */
	Class<?> getOwnerClass();
	
	/**
	 * @return primærnøkkel til eier av denne verdien
	 */
	Long getOwnerPk();
	
	/**
	 * @return true om verdien er arvet
	 */
	boolean isInherited();
	
	/**
	 * Angi om verdien er arvet eller ikke
	 * @param inherited arvet = true, ikke = false
	 */
	public void setInherited(boolean inherited);

	/**
	 * @return true hvis verdien er null eller tom eller kun består av mellomrom e.l.
	 */
	boolean isBlank();

	/**
	 * @param owner eieren du ønsker å sammenligne med
	 * @return true om angitt eier er samme som eier av denne verdien
	 */
	boolean isSameOwner(Entity owner);
	
	/**
	 * @return tekst-representasjon av verdien, formatert iht. Property#getFormatPattern()
	 */
	String getFormattedValue();

	public String getDescription();

}
