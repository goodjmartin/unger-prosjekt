package no.goodtech.vaadin.buttons;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

public class RemoveButton extends Button {

	private static final String BUTTON_CAPTION = ApplicationResourceBundle.getInstance("vaadin-core").getString("button.remove");

	public RemoveButton(final IRemoveListener removeListener) {
		setCaption(BUTTON_CAPTION);
		setIcon(VaadinIcons.CLOSE_CIRCLE);
		addStyleName("removeButton");
		addClickListener((ClickListener) event -> removeListener.removeClicked());
	}

    public interface IRemoveListener {
        void removeClicked();
    }

}
