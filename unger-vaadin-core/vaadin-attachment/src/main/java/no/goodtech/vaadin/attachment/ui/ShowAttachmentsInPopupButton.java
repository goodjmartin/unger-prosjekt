package no.goodtech.vaadin.attachment.ui;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseListener;
import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.vaadin.attachment.model.AttachmentFinder;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

/**
 * Button that opens a modal dialog box with list of attachments for a specific owner
 * Call {@link #setOwner(EntityStub)} before you use the button and
 * call {@link #refresh()} to show how many attachments there are in the button caption
 */
public class ShowAttachmentsInPopupButton extends Button {

	private String BUTTON_CAPTION = getText("attachment.popup.button.caption");
	private EntityStub owner;
	
	public ShowAttachmentsInPopupButton() {
		super();
		addClickListener((ClickListener) event -> {
			AttachmentPanel content = new AttachmentPanel();
			content.ownerEntitySelected(owner);
			Window window = new Window(getText("attachment.popup.window.caption"), content);
			window.setModal(true);
			UI.getCurrent().addWindow(window);
			window.focus();
			window.setWidth("65%");
			window.setHeight("30%");
			window.addCloseListener((CloseListener) e -> refreshCaption(content.getAttachmentCount()));
		});
		setCaption(BUTTON_CAPTION);
		setIcon(FontAwesome.PAPERCLIP);
	}


	public void setButtonCaption(String caption) {
		BUTTON_CAPTION = caption;
	}

	/**
	 * Provide owner of the attachments
	 */
	public void setOwner(EntityStub owner) {
		this.owner = owner;
	}

	private void refreshCaption(int count) {
		if (count == 0)
			setCaption(BUTTON_CAPTION);
		else
			setCaption(String.format("%s (%d)", BUTTON_CAPTION, count));
	}


	public void refresh() {
		refreshCaption(new AttachmentFinder().setOwner(owner).listPks().size());
	}
	
	private static String getText(String key) {
		return ApplicationResourceBundle.getInstance("vaadin-attachment").getString(key);
	}
}
