package no.goodtech.vaadin.attachment.model;

import no.goodtech.persistence.entity.AbstractEntityImpl;

import javax.persistence.Entity;

@Entity
public class AttachmentOwner extends AbstractEntityImpl<AttachmentOwner> {

	private String id;

	protected AttachmentOwner() {
	}

	public AttachmentOwner(final String id) {
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
