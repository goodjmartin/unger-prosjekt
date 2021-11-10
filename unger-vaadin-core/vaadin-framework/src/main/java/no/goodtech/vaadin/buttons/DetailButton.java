package no.goodtech.vaadin.buttons;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

public class DetailButton extends Button {

	private static final String BUTTON_CAPTION = ApplicationResourceBundle.getInstance("vaadin-core").getString("button.detail");

	public DetailButton(final IDetailListener detailListener) {
		setCaption(BUTTON_CAPTION);
		setIcon(VaadinIcons.PENCIL);
		addStyleName("detailButton");
		addClickListener((ClickListener) event -> detailListener.detailsClicked());
	}

    public interface IDetailListener {
        void detailsClicked();
    }

}
