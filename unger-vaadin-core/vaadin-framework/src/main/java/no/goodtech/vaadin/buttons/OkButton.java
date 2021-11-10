package no.goodtech.vaadin.buttons;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

public class OkButton extends Button {

	private static final String BUTTON_CAPTION = ApplicationResourceBundle.getInstance("vaadin-core").getString("button.ok");

	public OkButton(final IOkListener okListener) {
		setCaption(BUTTON_CAPTION);
		setIcon(VaadinIcons.CHECK_CIRCLE);
		addStyleName("okButton");
		addStyleName("primary");
		addClickListener((ClickListener) event -> okListener.okClicked());
	}

	public interface IOkListener {
		void okClicked();
	}

}
