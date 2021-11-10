package no.goodtech.vaadin.main;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.themes.BaseTheme;

/**
 * Link for å logge deg ut av applikasjonen
 */
public class LogoutLinkButton extends Button {
	
	/**
	 * Opprett en knapp/link for å logge ut angiit bruker fra angitt applikasjon
	 * @param userName brukernavn
	 */
	public LogoutLinkButton(final String userName) {
		setCaption(ApplicationResourceBundle.getInstance("vaadin-core").getString("top.button.logout.caption") + " " + userName);
		setDescription(ApplicationResourceBundle.getInstance("vaadin-core").getString("top.buttoFn.logout.description"));
		setWidth(200, Unit.PIXELS);
		setStyleName(BaseTheme.BUTTON_LINK);
		addStyleName("logout");
		addClickListener((ClickListener) event -> {
			// Close the application (i.e. invalidate session)
			VaadinSession.getCurrent().close();
			Page.getCurrent().setLocation("https://mes.unger.no/gmi/saml/logout");
		});
	}
}
