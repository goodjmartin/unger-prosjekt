package no.goodtech.vaadin.buttons;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

/**
 * En endre/avbryt-knapp. 
 * I vanlig modus ser den ut som en "endre-knapp".
 * Når du klikker på den går den i edit-modus, og knappen blir en "avbryt-knapp"
 * Neste gang du trykker på den går den tilbake til vanlig modus.
 * @author oystein
 */
public class EditModeSwitchButton extends Button {

	private boolean inEditMode = false;
	
	private static final ApplicationResourceBundle RESOURCE_BUNDLE = ApplicationResourceBundle.getInstance("vaadin-core");
	private static final String BUTTON_CAPTION_EDIT = RESOURCE_BUNDLE.getString("button.edit");
	private static final String BUTTON_CAPTION_CANCEL = RESOURCE_BUNDLE.getString("button.cancel");

	public interface IEditModeSwitchListener {
		void editClicked();
		void cancelClicked();
	}

	public EditModeSwitchButton(final IEditModeSwitchListener editListener) {
		setCaption(BUTTON_CAPTION_EDIT);
		refreshIconAndCaption();

		addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (inEditMode)
					editListener.cancelClicked();
				else
					editListener.editClicked();
				
				inEditMode = !inEditMode;
				refreshIconAndCaption();
			}
		});
	}

	private void refreshIconAndCaption() {
		if (inEditMode) {
			setIcon(VaadinIcons.ROTATE_LEFT);
			setCaption(BUTTON_CAPTION_CANCEL);
			setStyleName("cancelButton");
		} else {
			setIcon(VaadinIcons.EDIT);
			setCaption(BUTTON_CAPTION_EDIT);
			setStyleName("editButton");
		}
	}

	public boolean isInEditMode() {
		return inEditMode;
	}

	public void setInEditMode(boolean inEditMode) {
		this.inEditMode = inEditMode;
	}
}
