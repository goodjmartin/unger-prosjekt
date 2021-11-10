package no.goodtech.vaadin.event.model;

import no.goodtech.persistence.entity.AbstractEntityImpl;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * En hendelse koblet mot en hendelsestype.
 * Kobles mot en entitet via ownerClass og ownerPk
 */
@Entity
public class Event extends AbstractEntityImpl<Event> {
	//Klassen til eieren av hendelsen
	private final Class<?> ownerClass;
	//PK til eieren av hendelsen
	private final Long ownerPk;
	//Hvem/Hva som opprettet/endret hendelsen
	private final String changedBy;
	//Beskrivelse av hendelsen
	@Length(max = 8000)
	private final String eventDetail;
	//Når hendelsen ble opprettet
	private final Date created;

	//Hendelsestypen
	@ManyToOne
	@JoinColumn(name = "eventype_pk", referencedColumnName = "pk")
	private EventType eventType;

	@SuppressWarnings("UnusedDeclaration")
	protected Event() {        // Required by Hibernate
		this(null, null, null, null, null);
	}

	protected Event(final no.goodtech.persistence.entity.Entity owner, final String changedBy, final EventType eventType, final String eventDetail, final Date created) {
		this((owner != null) ? owner.getClass() : null, (owner != null) ? owner.getPk() : null, changedBy, eventType, eventDetail, created);
	}

	protected Event(final Class<?> ownerClass, final Long ownerPk, final String changedBy, final EventType eventType, final String eventDetail, final Date created) {
		this.ownerClass = ownerClass;
		this.ownerPk = ownerPk;
		this.changedBy = changedBy;
		this.eventType = eventType;
		this.eventDetail = eventDetail;
		this.created = created;
	}

	public Class<?> getOwnerClass() {
		return ownerClass;
	}

	public Long getOwnerPk() {
		return ownerPk;
	}

	public String getChangedBy() {
		return changedBy;
	}

	public EventType getEventType() {
		return eventType;
	}

	public String getEventDetail() {
		return eventDetail;
	}

	@SuppressWarnings("UnusedDeclaration")
	public Date getCreated() {
		return created;
	}

	@Override
	public String getNiceClassName() {
		return "Hendelse";
	}

}
