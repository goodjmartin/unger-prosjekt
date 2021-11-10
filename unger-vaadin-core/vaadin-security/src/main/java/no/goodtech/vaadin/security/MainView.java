package no.goodtech.vaadin.security;


import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import no.goodtech.vaadin.breadcrumb.ViewTitle;
import no.goodtech.vaadin.exception.ErrorNotifier;
import no.goodtech.vaadin.exception.UncaughtExceptionHandler;
import no.goodtech.vaadin.layoutDefinition.LayoutDefinition;
import no.goodtech.vaadin.main.notification.NotificationShowPanel;


class MainView extends VerticalLayout {

	private final TopMenu topMenu;
	private final Menu menu;

	MainView(final VerticalLayout contentPanel, final ViewTitle viewTitle, final SpringNavigator navigator, final SpringViewProvider viewProvider, final LayoutDefinition layoutDefinition,final String userId) {

		setSizeFull();
		setSpacing(false);
		setMargin(false);

		// Check if layout definition is found
		if (no.goodtech.vaadin.global.Globals.getLayoutManager() == null) {
			throw new IllegalStateException("Finner ingen layoutManager. Sjekk layout-definition.xml");
		}

		// Create left menu
		menu = new Menu(layoutDefinition, navigator, viewProvider);

		// Create top menu
		topMenu = new TopMenu(viewProvider);

		// Create the top panel
		final TopPanel topPanel = new TopPanel(menu);

		// Main panel
		VerticalLayout mainPanel = new VerticalLayout();
		mainPanel.setSizeFull();
		mainPanel.addComponents(topMenu, viewTitle, contentPanel);
		mainPanel.setExpandRatio(contentPanel, 1.0f);
		mainPanel.setSpacing(false);
		mainPanel.setMargin(false);

		// Application panel
		HorizontalLayout applicationPanel = new HorizontalLayout();
		applicationPanel.setSizeFull();
		applicationPanel.addComponents(menu, mainPanel);
		applicationPanel.setExpandRatio(mainPanel, 1.0f);
		applicationPanel.setSpacing(false);
		applicationPanel.setMargin(false);



		// Set custom error handler
		UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler(new ErrorNotifier());
		UI.getCurrent().setErrorHandler(uncaughtExceptionHandler);

		addComponents(topPanel, new NotificationShowPanel(), applicationPanel);
		setExpandRatio(applicationPanel, 1.0f);
	}

	public Menu getMenu() {
		return menu;
	}

	TopMenu getTopMenu() {
		return topMenu;
	}
}
