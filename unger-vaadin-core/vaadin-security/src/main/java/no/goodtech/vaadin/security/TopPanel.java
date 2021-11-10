package no.goodtech.vaadin.security;

import com.vaadin.server.ClassResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.main.Constants;
import no.goodtech.vaadin.main.LogoutLinkButton;
import no.goodtech.vaadin.main.VersionLabel;
import no.goodtech.vaadin.security.tabs.user.SecurityUtils;

class TopPanel extends HorizontalLayout {

	private static final String LABEL_MENU = ApplicationResourceBundle.getInstance("vaadin-security").getString("topPanel.menu.caption");
	private final Menu menu;

	TopPanel(final Menu menu) {

		this.menu = menu;

		addStyleName("topPanel");
		setMargin(false);
		setSpacing(false);
		setWidth(100, Unit.PERCENTAGE);
		setHeight(40, Unit.PIXELS);

		//User user = (User) VaadinSession.getCurrent().getAttribute(Constants.USER);
		User user = SecurityUtils.getCurrentUser();

		// Logout button
		Button logoutButton = new LogoutLinkButton(user.getFullName());

		// Version label
		VersionLabel versionLabel = new VersionLabel(null);

		// Goodtech logo
		Image image = new Image(null, new ClassResource(no.goodtech.vaadin.global.Globals.getGoodtechLogo()));

		// Add components
		Component toggleButton = buildToggleButton();
		if (Globals.getSecondaryTheme() != null && !Globals.getSecondaryTheme().isEmpty()) {
			Component switchThemeButton = buildSwitchThemeButton();
			addComponents(toggleButton, switchThemeButton, logoutButton, versionLabel, image);
			setComponentAlignment(switchThemeButton, Alignment.MIDDLE_LEFT);
		} else {
			addComponents(toggleButton, logoutButton, versionLabel, image);
		}
		setComponentAlignment(toggleButton, Alignment.MIDDLE_LEFT);
		setComponentAlignment(logoutButton, Alignment.MIDDLE_LEFT);
		setComponentAlignment(versionLabel, Alignment.MIDDLE_CENTER);
		setComponentAlignment(image,Alignment.MIDDLE_RIGHT);
		setExpandRatio(versionLabel, 1.0f);
	}

	private Component buildToggleButton() {
		Button menuToggleButton = new Button(LABEL_MENU, (Button.ClickListener) event -> {
			if (menu.isVisible()) {
				menu.setVisible(false);
			} else {
				menu.setVisible(true);
			}
		});

		menuToggleButton.setIcon(FontAwesome.LIST);
		menuToggleButton.addStyleName("topPanelTitleToggleButton");
		menuToggleButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		menuToggleButton.addStyleName(ValoTheme.BUTTON_SMALL);
		return menuToggleButton;
	}

	private Component buildSwitchThemeButton() {
		Button switchThemeButton = new Button("Tema");
		switchThemeButton.setIcon(FontAwesome.PAINT_BRUSH);
		switchThemeButton.addClickListener((Button.ClickListener) event -> {
			if (UI.getCurrent().getTheme().equals(Globals.getPrimaryTheme())) {
				UI.getCurrent().setTheme(Globals.getSecondaryTheme());
			} else {
				UI.getCurrent().setTheme(Globals.getPrimaryTheme());
			}
		});
		switchThemeButton.addStyleName("topPanelTitleToggleButton");
		switchThemeButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		switchThemeButton.addStyleName(ValoTheme.BUTTON_SMALL);
		return switchThemeButton;
	}


}
