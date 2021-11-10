package no.goodtech.vaadin.attachment.ui;


import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.Upload;
import no.goodtech.vaadin.attachment.model.AttachmentFactory;
import no.goodtech.vaadin.buttons.SaveButton;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.UUID;

/**
 * Window for editing and creating attachments
 */
public class AttachmentWindow extends Window {
	private static final String WINDOW_CAPTION = ApplicationResourceBundle.getInstance("vaadin-attachment").getString("attachmentWindow.caption");
	private static final String DESCRIPTION_TEXT_AREA_CAPTION = ApplicationResourceBundle.getInstance("vaadin-attachment").getString("attachmentWindow.descriptionTextArea.caption");
	private static final String UPLOAD_FAILED_NOTIFICATION = ApplicationResourceBundle.getInstance("vaadin-attachment").getString("attachmentWindow.failed.upload.notification");
	private static final String UPLOAD_BUTTON_CAPTION = ApplicationResourceBundle.getInstance("vaadin-attachment").getString("attachmentWindow.uploadButton.caption");

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Upload upload;
	private final TextField fileDescriptionTextArea;
	private final SaveButton saveButton;
	private final IAttachmentListener attachmentListener;

	public AttachmentWindow(final IAttachmentListener attachmentListener) {
		this.attachmentListener = attachmentListener;

		setModal(true);
		setCaption(WINDOW_CAPTION);
		setWidth(350, Unit.PIXELS);
		setHeight(170, Unit.PIXELS);

		//Create the fileDescriptionTextArea
		fileDescriptionTextArea = new TextField(DESCRIPTION_TEXT_AREA_CAPTION);
		fileDescriptionTextArea.setWidth("100%");

		//Create the upload component
		upload = createFileUploader();

		// Create save button
		saveButton = new SaveButton(() -> {
			attachmentListener.editAttachment(fileDescriptionTextArea.getValue());
			close();
		});

		//Create the layout which this window displays
		VerticalLayout attachmentWindowLayout = new VerticalLayout();
		attachmentWindowLayout.addComponents(fileDescriptionTextArea, upload, saveButton);

		//Set the content of the window to be attachmentWindowLayout
		setContent(attachmentWindowLayout);
	}

	public void setEditMode(boolean editMode) {
		if (editMode) {
			//Editing the file of an existing attachment is forbidden
			upload.setVisible(false);
			saveButton.setVisible(true);
			fileDescriptionTextArea.setValue(attachmentListener.getFileDescription());
		} else {
			//If the attachment is new, the upload button serves as the save button, so only show the saveButton in edit-mode
			saveButton.setVisible(false);
			upload.setVisible(true);
			//Clear the textfield
			fileDescriptionTextArea.setValue("");
		}
	}

	private Upload createFileUploader() {
		final AbsoluteFileNameHolder absoluteFileNameHolder = new AbsoluteFileNameHolder();

		final Upload upload = new Upload("", (Upload.Receiver) (filename, mimeType) -> {
			if (filename == null || filename.length() == 0) {
				return null;
			}
			FileOutputStream fileOutputStream; // Output stream to write to

			//Create directory it it doesn't exist
			File dir = new File(AttachmentFactory.getDirectoryPath());
			if (!dir.exists())
				//noinspection ResultOfMethodCallIgnored
				dir.mkdir();

			File file = new File(AttachmentFactory.getDirectoryPath() + "/" + UUID.randomUUID().toString() + filename);
			absoluteFileNameHolder.setAbsolutePath(file.getAbsolutePath());
			try {
				// Open the file for writing.
				fileOutputStream = new FileOutputStream(file);
			} catch (final FileNotFoundException e) {
				logger.error(e.getMessage(), e);
				return null;
			}
			return fileOutputStream;
		});

		/**
		 * Show a notification if the upload fails
		 */
		upload.addFailedListener((Upload.FailedListener) event -> {
			Notification.show(UPLOAD_FAILED_NOTIFICATION);
			close();
		});

		/**
		 * If successful register a new attachment and empty the fileDescriptionTextArea
		 */
		upload.addSucceededListener((Upload.SucceededListener) event -> {
			attachmentListener.registerAttachment(event.getFilename(), fileDescriptionTextArea.getValue(), absoluteFileNameHolder.getAbsolutePath());
			fileDescriptionTextArea.setValue("");
			close();
		});
		upload.setButtonCaption(UPLOAD_BUTTON_CAPTION);
		return upload;
	}

	private class AbsoluteFileNameHolder {
		volatile String absolutePath;

		public void setAbsolutePath(final String absolutePath) {
			this.absolutePath = absolutePath;
		}

		public String getAbsolutePath() {
			return absolutePath;
		}
	}

}
