package no.goodtech.vaadin.properties.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import no.goodtech.persistence.entity.AbstractSimpleEntityImpl;
import no.goodtech.vaadin.utils.Utils;

/**
 * Baseklasse for en kunde-spesifikk verdi.
 * Brukes til å lagre allslags verdier.
 * Meta-data om verdien finner du i {@link Property}.
 *
 * @author oystein
 */
@MappedSuperclass
public abstract class AbstractPropertyValue extends AbstractSimpleEntityImpl {

	/**
	 * Maks lengde av tekst-representasjonen av dataverdi (slik den lagres i databasen)
	 */
	public static final int MAX_VALUE_SIZE = 8000;

	@ManyToOne
	protected Property property;

	protected String value;

	@Transient
	protected transient boolean inherited = false;

	/**
	 * Opprett en tom verdi
	 */
	public AbstractPropertyValue() {
		this(null, null);
	}

	/**
	 * For internt bruk
	 *
	 * @param pk primærnøkkel
	 */
	public AbstractPropertyValue(long pk) {
		super(pk);
	}

	/**
	 * Opprett en verdi som representerer angitt egenskap
	 *
	 * @param property egenskapen
	 */
	public AbstractPropertyValue(PropertyStub property) {
		this(property, null);
	}

	/**
	 * Opprett en verdi som representerer angitt egenskap
	 *
	 * @param property egenskapen
	 * @param value    verdien
	 */
	public AbstractPropertyValue(PropertyStub property, String value) {
		this.property = (Property) property;
		setValue(value);
	}

	/**
	 * Opprett en verdi som representerer angitt egenskap
	 *
	 * @param property egenskapen
	 * @param value    verdien
	 */
	public AbstractPropertyValue(PropertyStub property, double value) {
		this.property = (Property) property;
		setValue(value);
	}

	/**
	 * @return verdien. Om datatypen er noe annet enn tekst, vil jeg forsøke å instansiere et objekt av korrekt type
	 */
	public Object getValue() {
		if (property == null || property.isBlank(value))
			return null;
		
		return property.parseValue(value);
	}

	public String getFormattedValue() { 
		if (property == null)
			return null;
		return property.format(getValue());
	}

    /**
     * Angi verdi
     * @param value ny verdi
     * @throws RuntimeException hvis verdien ikke kan konverteres til riktig datatype
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Angi verdi om datatypen er numerisk
     * Om datatypen er boolsk, kan du angi 1 (true) eller 0 (false)
     *
     * @param value ny verdi
     */
    public void setValue(Double value) {
        this.value = String.valueOf(value);
    }
	
	/**
	 * Angi verdi
	 * @param value ny verdi. 
	 */
	public void setValue(Long value) {
		if (value == null) {
			clear();
        } else {
		    this.value = String.valueOf(value);
        }
	}

    /**
     * Angi verdi
     * @param value ny verdi
     */
    public void setValue(Boolean value) {
        if (value == null) {
            clear();
        } else {
            if (value.equals(true))
                setValue(1L);
            else
                setValue(0L);
        }
    }

    /**
     * Angi verdi
     * @param value ny verdi.
	 * @throws RuntimeException hvis verdien ikke kan konverteres til riktig datatype
     */
    public void setValue(Date value) {
        if (value == null) {
			clear();
		} else {
			SimpleDateFormat dateFormat;
			final String pattern = property.getFormatPattern();
			if (pattern != null)
				dateFormat = new SimpleDateFormat(pattern);
			else
				dateFormat = new SimpleDateFormat(Property.DATETIME_FORMAT);

			this.value = dateFormat.format(value);
		}
    }

	/**
	 * Use this if you don't know the type
	 * @throws ClassCastException if value is not one of the supported data types
	 * @see Property#getDatatypesSupported()
	 */
	public void setValueAsObject(Object value) {
		if (value == null) {
			clear();
		} else {
			final Class<?> dataType = value.getClass();
			if (dataType == String.class) {
				setValue((String) value);
			} else if (dataType == Double.class) {
				setValue((Double) value);
			} else if (dataType == Float.class) {
				setValue(Float.valueOf((Float)value).doubleValue());
			} else if (dataType == Long.class ) {
				setValue((Long) value);
			} else if (dataType == Integer.class) {
				setValue(Integer.valueOf((Integer)value).longValue());
			} else if (dataType == Short.class) {
				setValue(Short.valueOf((Short)value).longValue());
			} else if (dataType == Byte.class) {
				setValue(Byte.valueOf((Byte)value).longValue());
			} else if (dataType == Boolean.class) {
				setValue((Boolean) value);
			} else if (dataType == Date.class) {
				setValue((Date) value);
			} else {
				throw new ClassCastException(value + " has unsupported type");
			}
		}
	}

	private void clear() {
		value = null;
	}

	/**
	 * @return returnerer tekst-representasjonen av verdien. Blank tekst hvis verdien er null
	 */
	@Override
	public String toString() {
		if (value == null)
			return "";
		return value;
	}

	/**
	 * Gjør om verdi på evt. lokale-spesifikt format til java internt format, som egner seg bedre for lagring
	 */
	String toStringInOnInternalFormat() {
		if (property != null) {
			final Class<?> dataType = property.getDataType();
			if (dataType == Double.class) {
				Object value = getValue();
				if (value != null) {
					Double doubleValue;
					try {
						doubleValue = Double.valueOf(value.toString());
					} catch (ClassCastException e) {
						throw new RuntimeException(e.getMessage() + ": " + property + ", verdi='" + value + "'");
					}
					return doubleValue.toString();
				}
			}
		}
		return value;
	}

	void transformToInternalFormat() {
		value = toStringInOnInternalFormat();
	}

	/**
	 * @return egenskapen som denne verdien hører til
	 */
	public PropertyStub getProperty() {
		return property;
	}

	/**
	 * @param property angi egenskapen som denne verdien hører til
	 */
	public void setProperty(PropertyStub property) {
		this.property = (Property) property;
	}

	/**
	 * @return true om verdien er arvet. NB! Verdien skjønner ikke selv at den er arvet.
	 *         Dette er kun et flagg som du selv kan bruke for å angi arv, se {@link #setInherited(boolean)}
	 */
	public boolean isInherited() {
		return inherited;
	}

	/**
	 * Angi om verdien er arvet eller ikke
	 *
	 * @param inherited arvet = true, ikke = false
	 */
	public void setInherited(boolean inherited) {
		this.inherited = inherited;
	}

}