package no.goodtech.vaadin.security;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Viewport;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import no.goodtech.vaadin.breadcrumb.BreadCrumbManager;
import no.goodtech.vaadin.breadcrumb.ViewTitle;
import no.goodtech.vaadin.layoutDefinition.LayoutDefinition;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.main.Constants;
import no.goodtech.vaadin.security.tabs.user.SecurityUtils;
import no.goodtech.vaadin.tabs.IMenuView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


@SuppressWarnings("unused")
@Theme("admin")
@PreserveOnRefresh
@SpringUI()
@Widgetset("vaadinWidgetSet")
@Push(PushMode.AUTOMATIC)
@Viewport("width=device-width,initial-scale=1.0")
public class Main extends UI implements IMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	public static final String SHOW_MENU_PARAMETER = "menu";
	private final String INSUFFICIENT_PRIVILEGES = ApplicationResourceBundle.getInstance("vaadin-security").getString("loginPanel.failure.insufficientPrivileges");

	private static final String LOGIN_MESSAGE = ApplicationResourceBundle.getInstance("vaadin-security").getString("main.loginMessage");
	private static final LayoutDefinition layoutDefinition = no.goodtech.vaadin.global.Globals.getLayoutManager().getLayoutDefinition();
	private final VerticalLayout contentPanel = new VerticalLayout();
	private final ViewTitle viewTitle = new ViewTitle();
	private final SpringViewProvider viewProvider;
	private final SpringNavigator navigator;

	private MainView mainView;

	private String previousState;

	@Autowired
	public Main(final SpringViewProvider viewProvider, final SpringNavigator navigator) {
		this.viewProvider = viewProvider;
		this.navigator = navigator;
	}

	@Override
	protected void init(final VaadinRequest request) {

		Responsive.makeResponsive(this);
		addStyleName(ValoTheme.UI_WITH_MENU);

		// Content panel
		contentPanel.setSizeFull();
		contentPanel.setMargin(false);
		contentPanel.setSpacing(false);

		// Create navigator
		navigator.init(getUI(), contentPanel);
		setNavigator(navigator);

		navigator.addViewChangeListener(new ViewChangeListener() {
			@Override
			public boolean beforeViewChange(final ViewChangeEvent event) {
				// Handle user / view authorization
				if (event.getNewView() instanceof IMenuView) {

					User user = SecurityUtils.getCurrentUser();

					if (user == null){
						SecurityUtils.logOut();
						return true;
					}

					// Check if user is authorized
					if (!((IMenuView)event.getNewView()).isAuthorized(user, event.getParameters())) {
						Notification.show(INSUFFICIENT_PRIVILEGES, Notification.Type.WARNING_MESSAGE);
						// TODO: Seems like view is shown anyway (bug in Vaadin):
						// TODO: - afterViewChange() not called (according to documentation)
						return false;
					}
				}
				return (mainView == null) || mainView.getTopMenu().handleTopMenu(event, navigator);
			}

			@Override
			public void afterViewChange(ViewChangeEvent viewChangeEvent) {
				BreadCrumbManager.createViewTitle(viewTitle, viewChangeEvent, viewProvider);

				// Show/hide menu by using a URL parameter
				String showMenuValue = viewChangeEvent.getParameterMap().get(SHOW_MENU_PARAMETER);
				if (mainView != null && mainView.getMenu() != null &&
						showMenuValue != null && !showMenuValue.isEmpty() &&
						(showMenuValue.equals("false") || showMenuValue.equals("true"))){
					boolean showMenu = Boolean.parseBoolean(showMenuValue.toLowerCase());
					mainView.getMenu().setVisible(showMenu);
				}
			}

		});

		if (layoutDefinition == null) {
			throw new IllegalStateException("Finner ingen layoutDefinition. Sjekk layout-definition.xml");
		}

		try {
			// Set default view
			View defaultView = viewProvider.getView(layoutDefinition.getMenu().getDefaultViewId());
			if (defaultView != null) {
				getNavigator().addView(Constants.DEFAULT_VIEW, defaultView);
			}
			updateContent(contentPanel);
		}catch (Exception e){
			LOGGER.error(" (navigating to LoginView): " + e.getMessage());
			SecurityUtils.logOut();
		}
	}

	/**
	 * Updates the correct content for this UI based on the current user status.
	 * If the user is logged in with appropriate privileges, main view is shown.
	 * Otherwise login view is shown.
	 */
	private void updateContent(final VerticalLayout contentPanel) {
		User user = SecurityUtils.getCurrentUser();

		if (user != null) {
			// Authenticated user
			final ProgressBar progressBar = showProgressSpinner();


			showContent(progressBar,user);
		}else{
			SecurityUtils.logOut();
		}
	}

	private void showContent(final ProgressBar progressBar, final User user) {
		new Thread(() -> getUI().access(() ->  {
			String userId = null;
			if (user != null) {
				userId = user.getId();
			}

			try {
				VaadinSession.getCurrent().setAttribute(Constants.USER, user);
			}catch (Exception e){

			}

			setContent(mainView = new MainView(contentPanel, viewTitle, navigator, viewProvider, layoutDefinition, userId));
			try {
				getNavigator().navigateTo(getNavigator().getState());
			}catch (IllegalArgumentException unknownState){
				Notification.show(INSUFFICIENT_PRIVILEGES, Notification.Type.WARNING_MESSAGE);
				getNavigator().navigateTo(layoutDefinition.getMenu().getDefaultViewId());
			}
			progressBar.setEnabled(false);
		})).start();
	}

	private ProgressBar showProgressSpinner() {
		ProgressBar progressBar = new ProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setEnabled(true);

		Label label = new Label(LOGIN_MESSAGE);

		HorizontalLayout infoLayout = new HorizontalLayout();
		infoLayout.setMargin(false);
		infoLayout.addComponents(progressBar, label);

		HorizontalLayout layoutLayout = new HorizontalLayout();
		layoutLayout.setSizeFull();
		layoutLayout.setSpacing(false);
		layoutLayout.setMargin(false);
		layoutLayout.addComponent(infoLayout);
		layoutLayout.setComponentAlignment(infoLayout, Alignment.MIDDLE_CENTER);
		setContent(layoutLayout);

		return progressBar;
	}

	@Override
	public void userLoggedIn() {
		// Make sure default view is added
		getNavigator().removeView(Constants.DEFAULT_VIEW);
		getNavigator().addView(Constants.DEFAULT_VIEW, viewProvider.getView(layoutDefinition.getMenu().getDefaultViewId()));

		// Navigate to default view if previous state is empty (or null) or equal to LoginView's view-id
		getNavigator().navigateTo(Constants.DEFAULT_VIEW);
		updateContent(contentPanel);
	}

	/**
	 * Use this method to programmatically navigate toe the default view with a certain viewId.
	 * If multiple views with this viewId exists, this selects the first one without any index
	 * @param viewId the viewId for the view you want to navigate to
	 */
	public void navigateInMenu(String viewId){
		navigateInMenu(viewId, null);
	}

	/**
	 * Use this method to programmatically navigate in the menu.
	 * If you have multiple views with viewId in your layout definition, you can differentiate them by setting the index
	 * @param viewId the viewId for the view you want to navigate to
	 * @param index optional (null if not important) - if multiple views with same viewId is used, this can be used to differentiate them
	 */
	public void navigateInMenu(String viewId, String index, Object ... parameters){
		mainView.getMenu().navigateInMenu(viewId, index, parameters);
	}

	@Override
	protected void refresh(VaadinRequest request) {
		super.refresh(request);

		//In case user has clicked F5 to refresh page, this will trigger a call to enter() method of the current open page.
		// For this refresh functionality to work ok, make sure the enter() method calls its filter search
		UI.getCurrent().getNavigator().getCurrentView().enter(
				new ViewChangeListener.ViewChangeEvent(navigator, navigator.getCurrentView(),
						navigator.getCurrentView(), "dummy", UI.getCurrent().getPage().getUriFragment())
		);
	}

}
