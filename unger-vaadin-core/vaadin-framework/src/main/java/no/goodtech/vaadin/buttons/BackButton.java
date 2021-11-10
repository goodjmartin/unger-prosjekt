package no.goodtech.vaadin.buttons;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

public class BackButton extends Button {

	private static final String BUTTON_CAPTION = ApplicationResourceBundle.getInstance("vaadin-core").getString("button.back");

	public BackButton(final IBackListener backListener) {
		setCaption(BUTTON_CAPTION);
		setIcon(VaadinIcons.ARROW_LEFT);
		addStyleName("backButton");
		addClickListener((ClickListener) event -> backListener.backClicked());
	}

    public interface IBackListener {
        void backClicked();
    }

}
