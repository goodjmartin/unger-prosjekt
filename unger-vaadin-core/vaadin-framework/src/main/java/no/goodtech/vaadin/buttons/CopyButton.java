package no.goodtech.vaadin.buttons;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

public class CopyButton extends Button {

	private static final String BUTTON_CAPTION = ApplicationResourceBundle.getInstance("vaadin-core").getString("button.copy");

	public CopyButton(final ICopyListener copyListener) {
		setCaption(BUTTON_CAPTION);
		setIcon(VaadinIcons.COPY_O);
		addStyleName("copyButton");
		addClickListener((ClickListener) event -> copyListener.copyClicked());
	}

    public interface ICopyListener {
        void copyClicked();
    }

}
