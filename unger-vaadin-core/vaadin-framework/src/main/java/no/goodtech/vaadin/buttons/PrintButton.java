package no.goodtech.vaadin.buttons;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

public class PrintButton extends Button{
	private static final String BUTTON_CAPTION = ApplicationResourceBundle.getInstance("vaadin-core").getString("button.print");

	public PrintButton(final IRefreshListener refreshListener) {
		setCaption(BUTTON_CAPTION);
		setIcon(VaadinIcons.PRINT);
		addStyleName("printButton");
		addClickListener(event -> refreshListener.refreshClicked());
	}

	public interface IRefreshListener {
		void refreshClicked();
	}
}
