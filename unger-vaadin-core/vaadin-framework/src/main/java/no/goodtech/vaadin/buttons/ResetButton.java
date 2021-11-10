package no.goodtech.vaadin.buttons;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

public class ResetButton extends Button {

	private static final String BUTTON_CAPTION = ApplicationResourceBundle.getInstance("vaadin-core").getString("button.reset");

	public ResetButton(final IResetListener resetListener) {
		setCaption(BUTTON_CAPTION);
		setIcon(VaadinIcons.ROTATE_LEFT);
		addStyleName("resetButton");
		addClickListener((ClickListener) event -> resetListener.resetClicked());
	}

    public interface IResetListener {
        void resetClicked();
    }

}
