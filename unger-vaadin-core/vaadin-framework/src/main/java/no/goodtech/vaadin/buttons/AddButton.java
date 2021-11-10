package no.goodtech.vaadin.buttons;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

public class AddButton extends Button {

	private static final String BUTTON_CAPTION = ApplicationResourceBundle.getInstance("vaadin-core").getString("button.add");

	public AddButton(final IAddListener addListener) {
		setCaption(BUTTON_CAPTION);
		setIcon(VaadinIcons.PLUS_CIRCLE);
		addStyleName("addButton");
 		addClickListener((ClickListener) event -> addListener.addClicked());
	}

    public interface IAddListener {
        void addClicked();
    }

}
