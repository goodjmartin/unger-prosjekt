package no.goodtech.vaadin.event.model;

import no.goodtech.persistence.entity.AbstractEntityImpl;

import javax.persistence.Entity;

@Entity
public class EventOwner extends AbstractEntityImpl<EventOwner> {

	private String id;

	protected EventOwner() {
	}
	
	public EventOwner(final String id) {
		this.id = id;
	}

	@Override
	public String getNiceClassName() {
		return "Owner";
	}

	public String getId() {
		return id;
	}

}
