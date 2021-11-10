package no.goodtech.vaadin.event.model;

import no.goodtech.persistence.entity.AbstractEntityImpl;

import javax.persistence.Entity;

/**
 * En hendelsesestype for en bestemt type entitet med gitt id
 */
@Entity
public class EventType extends AbstractEntityImpl<EventType> {

	//Entiteten som bruker hendelsestypen
	private final Class<?> ownerClass;

	//Navnet på hendelsestypen
	private final String id;

	@SuppressWarnings("UnusedDeclaration")
	protected EventType() {        // Required by Hibernate
		this(null, null);
	}

	protected EventType(final Class<?> ownerClass, final String id) {
		this.ownerClass = ownerClass;
		this.id = id;
	}

	public Class<?> getOwnerClass() {
		return ownerClass;
	}

	public String getId() {
		return id;
	}

	public String getNiceClassName() {
		return "HendelseType";
	}

}
