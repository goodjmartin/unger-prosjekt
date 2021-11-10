package no.goodtech.vaadin.properties.model;

import no.goodtech.persistence.PersistenceFactory;
import no.goodtech.persistence.jpa.Repository;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityNotFoundException;

/**
 * Verdien til en egenskap.
 * Brukes gjerne til å lagre kunde-spesifikke opplysninger på et data-objekt.
 * Eksempel: Farge = gul for bil 1 og svart for bil 2. (Egenskap = farge, verdier er gul og svart)
 * @author oystein
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PropertyValue extends AbstractPropertyValue implements PropertyValueStub {

	public static final class Fields {
		public static final String DESCRIPTION = "description";
	}

	private Long ownerPk = null;

	private Class<?> ownerClass = null;

	@Column
	private String description;

	@Override
	public String getNiceClassName() {
		return "Egenskaps-verdi";
	}

	/**
	 * For internt bruk
	 */
	public PropertyValue() {
		this(null, null);
	}

	/**
	 * Opprett en verdi
	 * @param property egenskapen du vil angi verdi for
	 */
	public PropertyValue(PropertyStub property) {
		this(null, property, null);
	}

	/**
	 * Opprett en verdi
	 *
	 * @param property egenskapen du vil angi verdi for
	 * @param owner    eier av verdien
	 */
	public PropertyValue(PropertyStub property, no.goodtech.persistence.entity.Entity owner) {
		this(null, property, owner);
	}

	/**
	 * Opprett en verdi
	 *
	 * @param value    verdien
	 * @param property egenskapen du vil angi verdi for
	 * @param owner    eier av verdien
	 */
	public PropertyValue(String value, PropertyStub property, no.goodtech.persistence.entity.Entity owner) {
		this.value = value;
		if (property != null)
			this.property = (Property) property;
		setOwner(owner);
	}

	/**
	 * Sjekker om verdien er god nok for lagring. Flg. sjekkes:
	 * - kan ikke være null, blank eller kun inneholde blanke tegn
	 * - eier må være satt
	 * - verdien kan ikke være lengre enn MAX_VALUE_SIZE
	 *
	 * @return feilmelding, eller null hvis alt er ok
	 */
	public String validate() {
		final String errorMessage = property.validate(this);
		if (errorMessage != null)
			return errorMessage;

		if (ownerClass == null || ownerPk == null)
			return "Verdien må ha en eier";
//It is not very user friendly to reject empty values for integration code etc., but the UI should take care of this 
//		if (isBlank())
//			return "Tomme eller blanke verdier kan ikke lagres. Slett heller verdien";
		if (value != null && value.length() > MAX_VALUE_SIZE)
			return "Lengden på verdien er " + value.length() + " tegn. Maks-lengde er " + MAX_VALUE_SIZE;

		return null;
	}

	/**
	 * @return true om object har samme property som denne
	 */
	@Override
	public boolean equals(Object object) {
		if (object == null || !(object instanceof PropertyValue))
			return false;
		AbstractPropertyValue otherValue = (AbstractPropertyValue) object;
		if (property == null)
			return false;
		final Property otherProperty = otherValue.property;
		if (otherProperty == null)
			return false;
		return property.equals(otherProperty);
	}

	@Override
	public int hashCode() {
		if (property == null)
			return 42;
		return new HashCodeBuilder()
				.append(property.getPk())
				.toHashCode();
	}

	public Long getOwnerPk() {
		return ownerPk;
	}

	@Override
	public boolean isBlank() {
		return value == null || value.trim().equals("");
	}

	/**
	 * Angi eier av denne verdien
	 *
	 * @param owner eier. Om denne er null, slettes nåværende eier
	 */
	public void setOwner(no.goodtech.persistence.entity.Entity owner) {
		if (owner != null) {
			this.ownerClass = owner.getClass();
			this.ownerPk = owner.getPk();
		} else {
			this.ownerClass = null;
			this.ownerPk = null;
		}
	}

	/**
	 * @param ownerPk angi primærnøkkel til eier av denne verdien
	 */
	public void setOwnerPk(Long ownerPk) {
		this.ownerPk = ownerPk;
	}

	public Class<?> getOwnerClass() {
		return ownerClass;
	}

	/**
	 * @param ownerClass angi datatype til eier av denne verdien
	 */
	public void setOwnerClass(Class<?> ownerClass) {
		this.ownerClass = ownerClass;
	}

	public boolean isSameOwner(no.goodtech.persistence.entity.Entity owner) {
		if (owner == null || ownerPk == null || ownerClass == null)
			return false;
		if (ownerClass.equals(ownerClass) && ownerPk.equals(owner.getPk()))
			return true;
		return false;
	}

	/**
	 * Lagrer objektet. NB! Du bør kaste den instansen du hadde og heller bruke instansen som returneres herfra.
	 * Validering blir kjørt automatisk. Evt. valideringsfeil vil trigge en {@link RuntimeException}.
	 * Kjør {@link #validate()} på forhånd om du ikke vil risikere dette.
	 *
	 * @return en fersk utgave av objektet.
	 */
	@SuppressWarnings("unchecked")
	public PropertyValue save() {
		String errorMessage = validate();
		if (errorMessage != null)
			throw new RuntimeException(errorMessage);
		return (PropertyValue) getRepository().save(this);
	}

	public PropertyValue load() {
        return (PropertyValue) getRepository().load(getPk());
	}

    @SuppressWarnings("rawtypes")
	private Repository getRepository() {
		return (Repository)PersistenceFactory.getPersistenceRepositoryManager().getRepository(this.getClass().getName());
	}

    /**
     * Sletter objektet og alle under-objekter som eies av dette
     * @return true om det gikk bra å slette, false om objektet har blitt slettet siden du brukte det sist
     */
    @SuppressWarnings("unchecked")
	public boolean delete() {
        boolean success = false;
        try {
        	getRepository().delete(this);
            success = true;
        } catch (EntityNotFoundException e) {
        	//objekt er slettet siden det ble lastet
        }
		return success;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public PropertyValue copy() {
		PropertyValue copy = new PropertyValue();
		copy.property = property;
		copy.ownerClass = ownerClass;
		copy.ownerPk = ownerPk;
		copy.value = value;
		copy.setPk(getPk());
		return copy;
	}
}
