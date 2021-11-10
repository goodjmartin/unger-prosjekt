package no.goodtech.vaadin.buttons;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

public class SaveButton extends Button {

	private static final String BUTTON_CAPTION = ApplicationResourceBundle.getInstance("vaadin-core").getString("button.save");

	public SaveButton(final ISaveListener saveListener) {
		setCaption(BUTTON_CAPTION);
		setIcon(VaadinIcons.ARCHIVE);
		addStyleName("saveButton");
		addStyleName("primary");
		addClickListener((ClickListener) event -> saveListener.saveClicked());
	}

    public interface ISaveListener {
        void saveClicked();
    }

}
