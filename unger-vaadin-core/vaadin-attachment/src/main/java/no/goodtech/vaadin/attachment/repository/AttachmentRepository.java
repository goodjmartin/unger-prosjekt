package no.goodtech.vaadin.attachment.repository;

import no.goodtech.vaadin.attachment.model.Attachment;
import no.goodtech.vaadin.attachment.model.AttachmentFinder;
import no.goodtech.persistence.server.AbstractRepositoryImpl;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AttachmentRepository extends AbstractRepositoryImpl<Attachment, Attachment, AttachmentFinder> {
}
