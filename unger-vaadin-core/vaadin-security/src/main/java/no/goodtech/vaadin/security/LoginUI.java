package no.goodtech.vaadin.security;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import no.goodtech.vaadin.login.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import no.goodtech.vaadin.security.tabs.user.SecurityUtils;

/**
 * This is the form login page.
 */
@SuppressWarnings("serial")
@SpringUI(path = "${security.url.login}")
@Theme("admin")
@Widgetset("vaadinWidgetSet")
//@PushStateNavigation // TODO Navigation by URL does not work with this on. There might be a way to get this working.
public class LoginUI extends UI {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginUI.class);

	@Override
	protected void init(final VaadinRequest request) {
		if (!Globals.isRequireLogin()){
			LOGGER.info("Redirecting to app cause login is not required");
			Page.getCurrent().setLocation(Globals.getAppURL());
		}

		// Make sure user is not logged in
		//final User user = SecurityUtils.getCurrentUser();
		final User user = null;

		if (user != null) {
			Page.getCurrent().setLocation(Globals.getAppURL());
		}

		LoginView lf = new LoginView(() -> getPage().setLocation(Globals.getAppURL()));
		addStyleName("loginView");
		setContent(lf);
	}
}
