package no.goodtech.vaadin.buttons;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

public class EditButton extends Button {

	private static final String BUTTON_CAPTION = ApplicationResourceBundle.getInstance("vaadin-core").getString("button.edit");

	public EditButton(final IEditListener editListener) {
		setCaption(BUTTON_CAPTION);
		setIcon(VaadinIcons.EDIT);
		addStyleName("editButton");
		addClickListener((ClickListener) event -> editListener.editClicked());
	}

    public interface IEditListener {
        void editClicked();
    }

}
