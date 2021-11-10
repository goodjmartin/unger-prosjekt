package no.goodtech.vaadin.buttons;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

public class RefreshButton extends Button {

	private static final String BUTTON_CAPTION = ApplicationResourceBundle.getInstance("vaadin-core").getString("button.refresh");
	public static final String BUTTON_ID = "refreshButton";

	public RefreshButton(final IRefreshListener refreshListener) {
		setCaption(BUTTON_CAPTION);
		setIcon(VaadinIcons.REFRESH);
		setId(BUTTON_ID);
		addStyleName("refreshButton");
		addStyleName("primary");
		addClickListener((ClickListener) event -> refreshListener.refreshClicked());
	}

    public interface IRefreshListener {
        void refreshClicked();
    }

}
