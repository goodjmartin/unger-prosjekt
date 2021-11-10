package no.goodtech.vaadin.attachment.model;

import no.goodtech.persistence.server.AbstractRepositoryImpl;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AttachmentOwnerRepository extends AbstractRepositoryImpl<AttachmentOwner, AttachmentOwner, AttachmentOwnerFinder> {
}
