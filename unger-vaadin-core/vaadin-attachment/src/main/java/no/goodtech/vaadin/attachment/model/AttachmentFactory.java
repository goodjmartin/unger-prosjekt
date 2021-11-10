package no.goodtech.vaadin.attachment.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AttachmentFactory {

	private static volatile String directoryPath;
	private static volatile IAttachmentService attachmentService;

	public AttachmentFactory(final String directoryPath, final IAttachmentService attachmentService) {
		AttachmentFactory.directoryPath = directoryPath;
		AttachmentFactory.attachmentService = attachmentService;
	}

	public static String getDirectoryPath() {
		//Create directory if it doesnt exist
		if (!Files.isDirectory(Paths.get(directoryPath))) {
			try {
				Files.createDirectory(Paths.get(directoryPath));
			} catch (IOException e) {
				//something else went wrong
				e.printStackTrace();
			}
		}
		return directoryPath;
	}

	public static IAttachmentService getAttachmentService() {
		return attachmentService;
	}

}
