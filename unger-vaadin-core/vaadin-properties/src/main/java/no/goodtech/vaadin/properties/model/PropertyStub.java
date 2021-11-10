package no.goodtech.vaadin.properties.model;

import java.util.List;

import no.goodtech.persistence.entity.EntityStub;

/**
 * En forenklet utgave av {@link Property}.
 * Bruk {@link #load()} for å få tilgang til alle funksjoner
 * @author oystein
 */
public interface PropertyStub extends EntityStub<Property> {

//	List<PropertyImplementation> getImplementations();
	/**
	 * @return måleenhet
	 */
	String getUnitOfMeasure();

	/**
	 * @return format-streng, styrer hvordan verdien skal vises
	 */
	String getFormatPattern();

	/**
	 * @return beskrivelse av egenskapen
	 */
	String getDescription();

	/**
	 * @return unik ID
	 */
	String getId();
	
	/**
	 * @return kort-navn. Brukes gjerne til visning på skjerm
	 */
	String getName();

	/**
	 * @return datatypen til verdien
	 */
	Class<?> getDataType();

	String getOptions();
	
	/**
	 * @return aktuelle valg for verdien, om dette er begrenset 
	 */
	List<String> getOptionsList();

//	/**
//	 * @return en oppslagsliste over aktuelle valg for verdien om dette er begrenset. 
//	 * Hver nøkkel er et valg og verdien til nøkkelen er en beskrivelse av valget
//	 * @deprecated denne forsvinner snart
//	 */
//	Map<String, String> getOptionDescriptions();
	
	/**
	 * Opprett en ny verdi av denne egenskapen
	 * @return den nye verdien. Husk å sette eier og selve verdien før du lagrer!
	 */
	PropertyValue createValue();
	
	/**
	 * Sjekker om angitt verdi tilfredsstiller kravene til denne egenskapen
	 * @param value verdien som skal sjekkes
	 * @return null om verdien er ok, eller feilmelding om den ikke er det
	 */
	String validate(String value);
	
	/**
	 * Prøver å tolke angitt tekst og konverterer denne til rett datatype
	 * @param value teksten som skal tolkes
	 * @return verdien på rett datatype
	 * @throws RuntimeException om jeg ikke klarer det
	 */
	Object parseValue(String value);
	
	/**
	 * Formaterer angitt verdi iht. #getFormatPattern()
	 * @param value verdien som skal formateres
	 * @return formatert verdi. Hvis verdien er null, returneres "".
	 */
	String format(Object value);
}
