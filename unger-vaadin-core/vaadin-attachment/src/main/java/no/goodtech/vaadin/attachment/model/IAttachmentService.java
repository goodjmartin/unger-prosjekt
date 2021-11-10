package no.goodtech.vaadin.attachment.model;

import no.goodtech.persistence.entity.EntityStub;

public interface IAttachmentService {

	public Attachment registerAttachment(final EntityStub owner, final String changedBy, final String fileName, final String fileDescription, final String filePath);

	public Attachment changeFileDescription(final Attachment attachment, final String changedBy, final String fileDescription);

	public boolean deleteAttachment(final Attachment attachment, final String changedBy);

	public void deleteAllAttachments(final EntityStub entity);

}
