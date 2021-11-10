package no.goodtech.vaadin.buttons;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

public class ChangeStatusButton extends Button {

	private static final String BUTTON_CAPTION = ApplicationResourceBundle.getInstance("vaadin-core").getString("button.changeStatus");

	public ChangeStatusButton(final IChangeStatusListener changeStatusListener) {
		setCaption(BUTTON_CAPTION);
		setIcon(new ThemeResource("images/changeStatus-17.png"));
		addClickListener((ClickListener) event -> changeStatusListener.changeStatusClicked());
	}

    public interface IChangeStatusListener {
        void changeStatusClicked();
    }

}
