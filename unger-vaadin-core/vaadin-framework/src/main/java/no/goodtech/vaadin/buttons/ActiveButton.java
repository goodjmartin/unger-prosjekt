package no.goodtech.vaadin.buttons;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

public class ActiveButton extends Button {

	private static final String BUTTON_CAPTION = ApplicationResourceBundle.getInstance("vaadin-core").getString("button.active");

	public ActiveButton(final IActiveListener activeListener) {
		setCaption(BUTTON_CAPTION);
		setIcon(VaadinIcons.FLAG_CHECKERED);
		addStyleName("activeButton");
		addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				activeListener.activeClicked();
			}
		});
	}

    public static interface IActiveListener {
        public void activeClicked();
    }

}
