package no.goodtech.vaadin.attachment.model;

import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.vaadin.event.model.EventFactory;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Service klasse som skaper hendelser når handlinger blir utført på vedlegg.
 */
public class AttachmentService implements IAttachmentService {
	public static final String EVENT_TYPE_ATTACHMENT_ADDED = ApplicationResourceBundle.getInstance("vaadin-attachment").getString("eventType.attachment.added");
	public static final String EVENT_TYPE_ATTACHMENT_EDITED = ApplicationResourceBundle.getInstance("vaadin-attachment").getString("eventType.attachment.edited");
	public static final String EVENT_TYPE_ATTACHMENT_DELETED = ApplicationResourceBundle.getInstance("vaadin-attachment").getString("eventType.attachment.deleted");

	private volatile boolean loggingEnabled;

	public AttachmentService(){

	}

	private AttachmentService(final boolean loggingEnabled) {
		this.loggingEnabled = loggingEnabled;
	}

	/**
	 * Oppretter et nytt vedlegg og registrerer en hendelse på at vedlegget har blitt opprettet
	 *
	 * @param owner           eier-klassen til vedlegget
	 * @param changedBy       hvem/hva som lastet det opp
	 * @param fileDescription beskrivelse av filen som har blitt lastet opp
	 * @param filePath        sti til filen
	 * @return det lagrede vedlegget
	 */
	@Transactional
	public Attachment registerAttachment(final EntityStub owner, final String changedBy, final String fileName, final String fileDescription, final String filePath) {
		if (loggingEnabled) {
			// Register creation event
			EventFactory.getEventService().registerEvent(owner, changedBy, EVENT_TYPE_ATTACHMENT_ADDED, fileDescription);
		}
		// Create attachment
		return new Attachment(owner, filePath, fileDescription, fileName, changedBy, new Date()).save();
	}

	/**
	 * Lagrer vedlegget og registrerer en hendelse på at vedlegget er redigert
	 *
	 * @param attachment vedlegget som har blitt redigert
	 * @return det lagrede vedlegget
	 */
	@Transactional
	public Attachment changeFileDescription(final Attachment attachment, final String changedBy, final String fileDescription) {
		if (loggingEnabled) {
			// Register change event
			EventFactory.getEventService().registerEvent(attachment.getOwnerClass(), attachment.getOwnerPk(), changedBy, EVENT_TYPE_ATTACHMENT_EDITED, fileDescription);
		}
		// Change 'file description' and 'changed by'
		attachment.setFileDescription(fileDescription);
		attachment.setChangedBy(changedBy);
		return attachment.save();
	}

	/**
	 * Sletter vedlegget og registrerer en hendelse på at vedlegget er slettet
	 *
	 * @param attachment vedlegget som skal slettes
	 * @return true hvis sletting gikk bra, false ellers
	 */
	@Transactional
	public boolean deleteAttachment(final Attachment attachment, final String changedBy) {
		if (loggingEnabled) {
			// Register deletion event
			EventFactory.getEventService().registerEvent(attachment.getOwnerClass(), attachment.getOwnerPk(), changedBy, EVENT_TYPE_ATTACHMENT_DELETED, attachment.getFileDescription());
			//Delete file
			File file = new File(attachment.getFilePath());
			if (file.exists() && !file.delete()) {
				return false;
			}
		}
		// Delete the attachment
		return attachment.delete();
	}

	/**
	 * Remove all attachments for the specified entity
	 */
	@Transactional
	public void deleteAllAttachments(final EntityStub entity) {
		List<Attachment> attachments = new AttachmentFinder().setOwner(entity).list();
		if (attachments != null) {
			for (Attachment attachment : attachments) {
				attachment.delete();
			}
		}
	}

}
