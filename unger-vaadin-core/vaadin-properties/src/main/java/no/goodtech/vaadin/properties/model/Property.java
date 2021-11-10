package no.goodtech.vaadin.properties.model;

import com.vaadin.ui.Link;
import no.cronus.common.utils.CollectionFactory;
import no.cronus.common.utils.ParseUtils;
import no.goodtech.persistence.entity.AbstractEntityImpl;
import no.goodtech.vaadin.lists.ICopyable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * En egenskap.
 * Brukes gjerne til å lagre kunde-spesifikke opplysninger på et domene-objekt.
 * Inneholder datatype og evt. valideringsregler for egenskapen
 * Selve verdien av egenskapen for hvert data-objekt ligger i {@link PropertyValue}
 * Eksempel: Farge = gul for bil 1 og svart for bil 2. (Egenskap = farge, verdier er gul og svart)
 *
 * @author oystein
 */
@Entity
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class Property extends AbstractEntityImpl<Property> implements PropertyStub, ICopyable<Property> {

	private static final Logger LOGGER = LoggerFactory.getLogger(Property.class);
	private static final String OPTION_SEPARATOR = "|";
	public static String DATE_FORMAT = "yyyy-MM-dd";
	public static String TIME_FORMAT = "HH:mm";
	public static String DATETIME_FORMAT = DATE_FORMAT + " " + TIME_FORMAT;

	/**
	 * Gir deg navn på felter som du kan bruke i JavaBeans-interfacet
	 */
	public static final class Fields {
		public static final String PK = "pk";
		public static final String ID = "id";
		public static final String NAME = "name";
		public static final String DESCRIPTION = "description";
		public static final String DATATYPE = "dataType";
		public static final String UNIT_OF_MEASURE = "unitOfMeasure";
		public static final String FORMAT_PATTERN = "formatPattern";
		public static final String OPTIONS = "options";	
		
		public static List<String> getAll() {
			return Arrays.asList(PK, ID, NAME, DESCRIPTION, DATATYPE, UNIT_OF_MEASURE, FORMAT_PATTERN, OPTIONS);
		}
	}

	private Class<?> dataType = String.class;
	private String id, name, description, formatPattern, unitOfMeasure, options;

	@OneToMany(cascade=CascadeType.ALL, mappedBy="property", orphanRemoval=true)
	private Set<PropertyMembership> propertyMemberships = CollectionFactory.getHashSet();

	/**
	 * Opprett egenskap
	 */
	public Property() {
		this(null);
	}

	/**
	 * Opprett egenskap med angitt ID
	 *
	 * @param id ID til egenskapen. Må være unik.
	 */
	public Property(String id) {
		this.id = id;
	}

	public Property copy() {
		Property copy = new Property(id);
		copy.dataType = dataType;
		copy.name = name;
		copy.description = description;
		copy.formatPattern = formatPattern;
		copy.unitOfMeasure = unitOfMeasure;
		copy.options = options;
		return copy;
	}

	public PropertyValue createValue() {
		return new PropertyValue(this, null);
	}

	@Override
	public String getNiceClassName() {
		return "Egenskap";
	}

	/**
	 * Angi tillate verdier
	 * Denne er fin å bruke hvis du skal la bruker velge vha en nedtrekksliste e.l.
	 * Det er ingen validering mot datatype hvis du velger denne
	 *
	 * @param options verdier som man kan velge. Tomme eller blanke enkelt-verdier i lista gir ingen mening og vil kaste exception,
	 *                men tom liste eller null er tillatt.
	 * @throws IllegalArgumentException hvis ett av valgene er blanke eller null.
	 */
	public void setOptionsAsList(List<String> options) {
		if (options == null || options.size() == 0)
			this.options = null;
		else
			this.options = toCommaSeparatedString(options);
	}

	private String toCommaSeparatedString(List<String> options) {
		StringBuilder optionString = new StringBuilder();
		for (String option : options) {
			if (option == null)
				optionString.append("");
			else
				optionString.append(option);
			optionString.append(OPTION_SEPARATOR);
		}
		optionString.deleteCharAt(optionString.length() - 1); //fjern siste skilletegn
		return optionString.toString();
	}

	public Class<?> getDataType() {
		return dataType;
	}

	/**
	 * Angi datatype
	 *
	 * @param dataType ny datatype
	 * @see #getDatatypesSupported()
	 * @throws IllegalArgumentException hvis du angir en type som ikke er støttet
	 */
	public void setDataType(Class<?> dataType) {
		throwIfUnsupportedDataType(dataType);
		this.dataType = dataType;
	}

	static void throwIfUnsupportedDataType(Class<?> dataType) {
		if (!getDatatypesSupported().contains(dataType))
			throw new IllegalArgumentException(dataType + " er ikke støttet. Se Property.getDatatypesSupported()");
	}
	
//	boolean isDataTypeSupported(Class<?> dataType) {
//		for (Class<?> type : getDatatypesSupported())
//			if (type.isAssignableFrom(dataType))
//				return true;
//		return false;
//	}

	public String getId() {
		return id;
	}

	/**
	 * Angi unik ID
	 *
	 * @param id ny ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Angi beskrivelse av egenskapen
	 *
	 * @param description beskrivelsen
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public String getFormatPattern() {
		return formatPattern;
	}

	/**
	 * Angi format-streng som styrer hvordan verdien skal vises
	 *
	 * @param formatPattern ny format-streng
	 */
	public void setFormatPattern(String formatPattern) {
		this.formatPattern = formatPattern;
	}

	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}

	/**
	 * Angi måleenhet
	 *
	 * @param unitOfMeasure ny måleenhet
	 */
	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	/**
	 * Sjekker om verdien er god nok for lagring. Flg. sjekkes:
	 * - kan ikke være null, blank eller kun inneholde blanke tegn
	 *
	 * @param value verdien som skal sjekkes
	 * @return feilmelding, eller null hvis alt er ok
	 */
	public String validate(AbstractPropertyValue value) {
		try {
			value.getValue();
		} catch (Exception e) {
			String message = "Jeg greier ikke tolke feltet '" + id +
					"'.\nVennligst angi en verdi av type " + getNiceDatatypeName(dataType);
			if (dataType == Double.class)
				message = message + ".\nBruk . eller , som desimalskilletegn og ikke bruk tusenskilletegn";
			return message;
		}
		return null;
	}

	/**
	 * Gir deg et norsk synonym for angitt datatype, som du kan formidle til bruker
	 * @param type datatypen
	 * @return et norsk synonym for angitt datatype
	 */
	public static String getNiceDatatypeName(Class<?> type) {
		final String unknown = "<ukjent>";
		if (!getDatatypesSupported().contains(type))
			return unknown;
		if (type.equals(String.class))
			return "Tekst";
		if (type.equals(Double.class))
			return "Desimaltall";
		if (type.equals(Long.class))
			return "Heltall";
		if (type.equals(Date.class))
			return "Dato";
		if (type.equals(Boolean.class))
			return "Boolsk (ja/nei)";
		if (type.equals(Link.class))
			return "Link";
		return unknown;
	}

	@Override
	public List<String> getOptionsList() {
		return commaSeparatedStringToList(options);
	}

	public String getOptions() {
		return options;
	}

	private List<String> commaSeparatedStringToList(String options) {
		if (options != null) {
			final String niceSeparator = "<skilletegn>";
			final String niceOptions = options.replace(OPTION_SEPARATOR, niceSeparator);
			final String[] optionArray = niceOptions.split(niceSeparator);
			final List<String> result = new ArrayList<String>(Arrays.asList(optionArray));
			if (niceOptions.endsWith(niceSeparator))
				result.add("");
			return result;

		}
		return new ArrayList<String>();
	}

	public void setOptions(String options) {
		if (options != null && "".equals(options.trim()))
			this.options = null;
		else
			this.options = options;
	}

	@Override
	public String toString() {
		return getNiceClassName() + " " + id;
	}

	public String getName() {
		return name;
	}

	/**
	 * Angi navn for visning på skjerm
	 *
	 * @param name kort-navn
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * This Property is added to a PropertyClass
	 * @param propertyClass
	 * @return true: if the property is saved to the propertyclass. false: if it already exists in the propertyclass or
	 * if the propertyClass is null
	 */
	public boolean addPropertyClass(final PropertyClass propertyClass) {
		if(propertyClass != null && !propertyClass.getProperties().contains(this)) {
			PropertyMembership propertyMembership = new PropertyMembership();
			propertyMembership.setProperty(this);
			propertyMembership.setPropertyClass(propertyClass);
			propertyMemberships.add(propertyMembership);
			return true;
		}

		return false;
	}

	public Set<PropertyMembership> getPropertyMemberships(){
		return propertyMemberships;
	}

	public PropertyMembership getPropertyMembership(PropertyClass propertyClass){
		for (PropertyMembership membership : propertyMemberships) {
			if(membership.getPropertyClass().equals(propertyClass)){
				return membership;
			}
		}
		return null;
	}

	/**
	 * @return alle datatyper som lov å bruke
	 */
	public static Set<Class<?>> getDatatypesSupported() {
		Class<?>[] types = new Class<?>[]{String.class, Double.class, Long.class, Boolean.class, Date.class, Link.class};
		return new HashSet<Class<?>>(Arrays.asList(types));
	}

	/**
	 * @return alle datatyper som lov å bruke
	 */
	public static Class<?>[] getDatatypesSupportedArray() {
		return new Class<?>[]{String.class, Double.class, Long.class, Boolean.class, Date.class, Link.class};
	}

	public Object parseValue(String value) {
		if (isBlank(value))
			return null;
		final Class<?> dataType = getDataType();
		if (dataType == Double.class)
			return ParseUtils.parseDoubleDoNotAcceptGroupingSeparators(value);
		if (dataType == Long.class)
			return ParseUtils.parseDoubleDoNotAcceptGroupingSeparators(value).longValue();
		if (dataType == Boolean.class) {
			if (value.equals("true") || value.equals("false")) {
				return Boolean.valueOf(value);
			} else if (value.equals("0")) {
				return false;
			} else if (value.equals("1"))
				return true;
			else {
				return value;
			}
		}

        if (dataType == Date.class) {
            SimpleDateFormat dateFormat;
            final String pattern = getFormatPattern();
            if (pattern != null)
                dateFormat = new SimpleDateFormat(pattern);
            else
                dateFormat = new SimpleDateFormat(DATETIME_FORMAT);
            
            try {
                return dateFormat.parseObject(value);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }


        return value;
    }

	/**
	 * Sjekker om angitt verdi tilfredsstiller kravene til denne egenskapen
	 * @param value verdien som skal sjekkes
	 * @return null om verdien er ok, eller feilmelding om den ikke er det
	 */
	public String validate(String value) {
		try {
			parseValue(value);
			return null;
		} catch (RuntimeException e) {
			StringBuilder message = new StringBuilder("Kunne ikke tolke verdien '");
			message.append(value);
			message.append("'. Datatypen skal være: ");
			message.append(getNiceDatatypeName(getDataType()));
			if (formatPattern != null) {
				message.append(" og formatet skal være '");
				message.append(formatPattern);
				message.append("'");
			}
			return message.toString();
		}
	}


	/**
	 * @param value verdien som skal kontrolleres
	 * @return true hvis verdien er null eller tom eller kun består av mellomrom e.l.
	 */
	public boolean isBlank(String value) {
		return value == null || value.trim().equals("");
	}

	public String format(Object value) {
		return Property.format(value, dataType, formatPattern);
	}

	/**
	 * Formats a property value based on the formatPattern of the property
	 * @return the string representation of value
	 */
	public static String format(Object value, Class<?> dataType, String formatPattern) {
		if (formatPattern != null && !formatPattern.isEmpty()) {
			try {
				if(dataType != Double.class && dataType != Long.class) {
					if(dataType == Date.class) {
						DateFormat dateFormatter = new SimpleDateFormat(formatPattern);
						return dateFormatter.format(value);
					} else {
						return value.toString();
					}
				} else {
					DecimalFormat decimalFormat = new DecimalFormat(formatPattern);
					return value instanceof String?decimalFormat.format(Double.parseDouble((String)value)):decimalFormat.format(value);
				}
			} catch (IllegalArgumentException var3) {
				LOGGER.warn("format(): Kan ikke formatere '{}' iht. formatet '{}'. Bruker standard-formatet", value, formatPattern);
				return value.toString();
			}
		} else {
			return value == null?"":value.toString();
		}
	}

	public static boolean isTypeAnyOf(Class<?> type, Class<?>... types) {
		for (Class<?> t : types)
			if (type.isAssignableFrom(t))
				return true;
		return false;
	}
}
