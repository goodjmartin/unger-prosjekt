package no.goodtech.vaadin.buttons;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import no.goodtech.vaadin.buttons.CancelButton.ICancelListener;
import no.goodtech.vaadin.buttons.EditButton.IEditListener;
import no.goodtech.vaadin.buttons.SaveButton.ISaveListener;

/**
 * En endre/avbryt-knapp. 
 * I vanlig modus ser den ut som en "endre-knapp".
 * Når du klikker på den går den i edit-modus, og knappen blir til to knapper: En "avbryt-knapp" og en "lagre-knapp".
 * @author oystein
 */
public class EditCancelSaveButton extends HorizontalLayout {

	private boolean inEditMode = false;
	protected final Button editButton, cancelButton, saveButton;

	public interface IEditCancelSaveListener {
		void editClicked();
		
		/**
		 * Use this to stop the user from saving
		 * @return false to disable save, true to enable save
		 */
		boolean validate();
		void saveClicked();
		void cancelClicked();
	}

	public EditCancelSaveButton(final IEditCancelSaveListener listener) {
		editButton = new EditButton(new IEditListener() {
			public void editClicked() {
				setInEditMode(true);
				listener.editClicked();
			}
		});
		cancelButton = new CancelButton(new ICancelListener() {
			public void cancelClicked() {
				setInEditMode(false);
				listener.cancelClicked();
			}
		});
		saveButton = new SaveButton(new ISaveListener() {
			public void saveClicked() {
				if (listener.validate()) {
					setInEditMode(false);
					listener.saveClicked();
				}
			}
		});

		addComponents(editButton, saveButton, cancelButton);
		cancelButton.setVisible(false);
		saveButton.setVisible(false);
		saveButton.addStyleName("primary");
		setMargin(false);
		setSizeUndefined();
	}

	public boolean isInEditMode() {
		return inEditMode;
	}

	public void setInEditMode(boolean inEditMode) {
		editButton.setVisible(!inEditMode);
		cancelButton.setVisible(inEditMode);
		saveButton.setVisible(inEditMode);
		this.inEditMode = inEditMode;
	}
}
