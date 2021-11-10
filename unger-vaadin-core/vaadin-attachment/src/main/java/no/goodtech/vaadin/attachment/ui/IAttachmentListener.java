package no.goodtech.vaadin.attachment.ui;

import java.io.InputStream;

public interface IAttachmentListener {

	void registerAttachment(final String filename, final String value, final String absolutePath);

	void editAttachment(final String value);

	void deleteAttachment();

	String getFileDescription();

	String getFileName();

	InputStream getAttachmentFile();
}
