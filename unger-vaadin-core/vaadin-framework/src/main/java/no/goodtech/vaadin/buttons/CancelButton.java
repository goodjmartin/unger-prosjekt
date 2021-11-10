package no.goodtech.vaadin.buttons;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

public class CancelButton extends Button {

    private static final String BUTTON_CAPTION = ApplicationResourceBundle.getInstance("vaadin-core").getString("button.cancel");

	public CancelButton(final ICancelListener cancelListener) {
		setCaption(BUTTON_CAPTION);
		setIcon(VaadinIcons.ROTATE_LEFT);
		addStyleName("cancelButton");
		addClickListener((ClickListener) event -> cancelListener.cancelClicked());
	}

	public interface ICancelListener {
		void cancelClicked();
	}
}
