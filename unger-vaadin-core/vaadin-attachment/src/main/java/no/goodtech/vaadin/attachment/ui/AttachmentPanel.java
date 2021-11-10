package no.goodtech.vaadin.attachment.ui;

import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Table;
import no.goodtech.persistence.entity.AbstractChildEntityImpl;
import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.vaadin.attachment.model.Attachment;
import no.goodtech.vaadin.attachment.model.AttachmentFactory;
import no.goodtech.vaadin.attachment.model.AttachmentFinder;
import no.goodtech.vaadin.buttons.AddButton;
import no.goodtech.vaadin.buttons.CustomFileDownloader;
import no.goodtech.vaadin.buttons.EditButton;
import no.goodtech.vaadin.buttons.RemoveButton;
import no.goodtech.vaadin.lists.v7.IMessyTableActionListener;
import no.goodtech.vaadin.lists.v7.MessyButtonLayout;
import no.goodtech.vaadin.lists.v7.MessyTable;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.main.ConfirmWindow;
import no.goodtech.vaadin.main.Constants;
import no.goodtech.vaadin.search.FilterPanel;
import no.goodtech.vaadin.search.OwnerColumnGenerator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

/**
 * Class responsible for displaying UI components for attachment and any changes on attachments
 */
public class AttachmentPanel extends VerticalLayout implements IAttachmentListener {
	private static final String ATTACHMENT_DELETION_FAILURE_NOTIFICATION = ApplicationResourceBundle.getInstance("vaadin-attachment").getString("attachment.delete.failure.notification");
	private static final String ATTACHMENT_DOWNLOAD_FAILURE_NOTIFICATION = ApplicationResourceBundle.getInstance("vaadin-attachment").getString("attachment.download.failure.notification");
	private static final String CREATED_CAPTION = ApplicationResourceBundle.getInstance("vaadin-attachment").getString("attachmentTable.column.created.caption");
	private static final String OWNER_CLASS_CAPTION = ApplicationResourceBundle.getInstance("vaadin-attachment").getString("attachmentTable.column.ownerClass.caption");
	private static final String OWNER_CAPTION = ApplicationResourceBundle.getInstance("vaadin-attachment").getString("attachmentTable.column.owner.caption");
	private static final String CHANGED_BY_CAPTION = ApplicationResourceBundle.getInstance("vaadin-attachment").getString("attachmentTable.column.changedBy.caption");
	private static final String FILE_NAME_CAPTION = ApplicationResourceBundle.getInstance("vaadin-attachment").getString("attachmentTable.column.fileName.caption");
	private static final String FILE_DESCRIPTION_CAPTION = ApplicationResourceBundle.getInstance("vaadin-attachment").getString("attachmentTable.column.fileDescription.caption");
	private static final String FILE_PATH_CAPTION = ApplicationResourceBundle.getInstance("vaadin-attachment").getString("attachmentTable.column.filePath.caption");
	private static final String ATTACHMENT_CONFIRM_WINDOW_CAPTION = ApplicationResourceBundle.getInstance("vaadin-attachment").getString("attachment.ConfirmWindow.caption");
	private static final String ATTACHMENT_DOWNLOAD_BUTTON_CAPTION = ApplicationResourceBundle.getInstance("vaadin-attachment").getString("attachment.downloadButton.caption");
	private static final String FILE_DOWNLOAD_BUTTON = "fileDownloadButton";

	//Filterpanel for filtering attachments (optional)
	private final AttachmentFilterPanel filterPanel;
	//Table containing all attachments owner by the selected ownerEntity
	private final MessyTable<Attachment> table;
	//Buttons for adding, deleting and changing attachments
	private final MessyButtonLayout buttonLayout;
	private final Button fileDownloadButton;
	private final AttachmentWindow attachmentWindow;
	private final ConfirmWindow confirmWindow;

	//The owner of the attachments
	private volatile EntityStub ownerEntity;
	//The current selected attachment
	private volatile Attachment attachment;

	private final OwnerColumnGenerator columnGenerator;

	protected boolean isReadOnly;

	public AttachmentPanel() {
		setSizeFull();

		filterPanel = new AttachmentFilterPanel(new FilterPanel.FilterActionListener<AttachmentFinder>() {
			@Override
			public Integer pleaseSearch(AttachmentFinder attachmentFinder) {
				List<Attachment> attachments = attachmentFinder.list();
				columnGenerator.refreshMap(Attachment.class, (List<AbstractChildEntityImpl>)(List<?>)attachments);
				table.refresh(attachments);
				return attachments.size();
			}
		});
		filterPanel.setVisible(false); // Should this be default off/on?

		table = new MessyTable<>(new BeanItemContainer<>(Attachment.class), new IMessyTableActionListener<Attachment>() {
			@Override
			public void objectSelected(Attachment attachment) {
				AttachmentPanel.this.attachment = attachment;
				buttonLayout.enableTableSelectionButtons(attachment != null);
				fileDownloadButton.setEnabled(attachment != null);
			}

			@Override
			public void objectsSelected(Set<Attachment> objects) {
			}

			@Override
			public void doubleClick(Attachment object) {
				Page.getCurrent().getJavaScript().execute("document.getElementById('" + FILE_DOWNLOAD_BUTTON + "').click();");
			}

			@Override
			public void pleaseDelete(Attachment object) {
				deleteAttachment();
			}
		});

		columnGenerator = new OwnerColumnGenerator();
		table.addGeneratedColumn(OwnerColumnGenerator.OWNER_CLASS_CAPTION, columnGenerator);
		table.addGeneratedColumn(OwnerColumnGenerator.OWNER_PK_CAPTION, columnGenerator);
		table.setVisibleColumns("created", OwnerColumnGenerator.OWNER_CLASS_CAPTION, OwnerColumnGenerator.OWNER_PK_CAPTION, "changedBy", "fileName", "filePath",
				"fileDescription");
		table.setColumnHeaders(CREATED_CAPTION, OWNER_CLASS_CAPTION, OWNER_CAPTION, CHANGED_BY_CAPTION, FILE_NAME_CAPTION,
				FILE_PATH_CAPTION, FILE_DESCRIPTION_CAPTION);
		table.setNullSelectionAllowed(false);
		table.setColumnAlignment(OwnerColumnGenerator.OWNER_PK_CAPTION, Table.Align.LEFT);
		table.setColumnCollapsed("filePath", true);

		attachmentWindow = new AttachmentWindow(this);

		confirmWindow = new ConfirmWindow(ATTACHMENT_CONFIRM_WINDOW_CAPTION, new ConfirmWindow.IConfirmWindowListener() {
			@Override
			public void onAccept() {
				AttachmentPanel.this.deleteAttachment();
			}

			@Override
			public void onCancel() {
				//Do nothing, just close the window
			}
		});

		fileDownloadButton = createFileDownloadButton();
		buttonLayout = createButtonLayout();

		addComponents(filterPanel, table, buttonLayout);
		setExpandRatio(table, 1.0F);
	}

	private MessyButtonLayout createButtonLayout() {
		MessyButtonLayout buttonLayout = new MessyButtonLayout();
		buttonLayout.setAddButton(new AddButton(() -> {
			attachmentWindow.setEditMode(false);
			getUI().addWindow(attachmentWindow);
		}));
		buttonLayout.setEditButton(new EditButton(() -> {
			attachmentWindow.setEditMode(true);
			getUI().addWindow(attachmentWindow);
		}));
		buttonLayout.setRemoveButton(new RemoveButton(() -> getUI().addWindow(confirmWindow)));
		buttonLayout.addButton(fileDownloadButton);
		buttonLayout.enableTableSelectionButtons(false);
		fileDownloadButton.setEnabled(false);
		return buttonLayout;
	}

	private Button createFileDownloadButton() {
		Button fileDownloadButton = new Button(ATTACHMENT_DOWNLOAD_BUTTON_CAPTION);
		fileDownloadButton.setId(FILE_DOWNLOAD_BUTTON);
		fileDownloadButton.setIcon(new ThemeResource("../admin/images/download-16.png"));

		final CustomFileDownloader.OnDemandStreamResource onDemandStreamResource = new CustomFileDownloader.OnDemandStreamResource() {
			@Override
			public String getFilename() {
				return AttachmentPanel.this.getFileName();
			}

			@Override
			public InputStream getStream() {
				return AttachmentPanel.this.getAttachmentFile();
			}
		};

		new CustomFileDownloader(onDemandStreamResource).extend(fileDownloadButton);
		return fileDownloadButton;
	}

	public void refresh() {
		if (ownerEntity != null) {
			List<Attachment> attachments = new AttachmentFinder().setOwner(ownerEntity).orderByCreated(false).list();
			columnGenerator.refreshMap(Attachment.class, (List<AbstractChildEntityImpl>)(List<?>)attachments);
			table.refresh(attachments);
		} else if (filterPanel.isVisible()) {
			filterPanel.refresh();
		}
	}

	public void refreshFilterPanel() {
		filterPanel.refresh();
	}

	/**
	 * Refresh the table when a entity is selected
	 *
	 * @param entity the selected entity
	 */
	public void ownerEntitySelected(final EntityStub entity) {
		this.ownerEntity = entity;
		refresh();
	}

	/**
	 * Create the attachment and refresh the table
	 *
	 * @param filename        the filename
	 * @param fileDescription editable description of the file
	 * @param absolutePath    the complete filePath
	 */
	@Override
	public void registerAttachment(final String filename, final String fileDescription, final String absolutePath) {
		AttachmentFactory.getAttachmentService().registerAttachment(ownerEntity, ((User) VaadinSession.getCurrent().getAttribute(Constants.USER)).getId(), filename, fileDescription, absolutePath);
		refresh();
	}

	/**
	 * Save the attachment and refresh the table
	 *
	 * @param fileDescription editable description of the file
	 */
	@Override
	public void editAttachment(final String fileDescription) {
		AttachmentFactory.getAttachmentService().changeFileDescription(attachment, ((User) VaadinSession.getCurrent().getAttribute(Constants.USER)).getId(), fileDescription);
		refresh();
	}

	/**
	 * Delete the attachmentand refresh the table
	 */
	@Override
	public void deleteAttachment() {
		if (AttachmentFactory.getAttachmentService().deleteAttachment(attachment, ((User) VaadinSession.getCurrent().getAttribute(Constants.USER)).getId())) {
			refresh();
		} else {
			Notification.show(ATTACHMENT_DELETION_FAILURE_NOTIFICATION);
		}
	}

	/**
	 * Returns the file description
	 *
	 * @return fileDescription
	 */
	@Override
	public String getFileDescription() {
		return attachment.getFileDescription();
	}

	@Override
	public String getFileName() {
		return attachment.getFileName();
	}

	@Override
	public InputStream getAttachmentFile() {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(attachment.getFilePath());
		} catch (FileNotFoundException e) {
			Notification.show(ATTACHMENT_DOWNLOAD_FAILURE_NOTIFICATION, Notification.Type.WARNING_MESSAGE);
		}
		return inputStream;
	}

	public void showFilterPanel(boolean show) {
		filterPanel.setVisible(show);
	}

	public Button getDeleteAttachmentButton() {
		return buttonLayout.getRemoveButton();
	}

	public Button getEditAttachmentButton() {
		return buttonLayout.getEditButton();
	}

	public Button getAddAttachmentButton() {
		return buttonLayout.getAddButton();
	}

	public int getAttachmentCount() {
		return table.size();
	}

	@Override
	protected boolean isReadOnly() {
		return isReadOnly;
	}

	@Override
	protected void setReadOnly(boolean readOnly) {
		isReadOnly = readOnly;
	}
}
