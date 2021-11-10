package no.goodtech.vaadin.attachment.model;

import no.goodtech.persistence.jpa.AbstractFinder;

public class AttachmentOwnerFinder extends AbstractFinder<Attachment, Attachment, AttachmentOwnerFinder> {

	public AttachmentOwnerFinder() {
		super("select o from Owner o", "o");
	}

	public AttachmentOwnerFinder setId(final String id) {
		addEqualFilter(prefixWithAlias("id"), id);
		return this;
	}

}
