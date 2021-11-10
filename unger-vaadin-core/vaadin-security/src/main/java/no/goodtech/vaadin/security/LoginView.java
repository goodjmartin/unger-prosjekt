package no.goodtech.vaadin.security;


import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import no.goodtech.vaadin.global.VaadinSpringContextHelper;
import no.goodtech.vaadin.login.IAuthentication;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.main.SimpleInputBox;
import no.goodtech.vaadin.security.model.AccessRole;
import no.goodtech.vaadin.security.model.UserFinder;
import no.goodtech.vaadin.security.tabs.user.UserEditForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSendException;
import org.springframework.transaction.CannotCreateTransactionException;

import java.util.Arrays;
import java.util.HashSet;

@UIScope
@SpringView(name = LoginView.VIEW_ID)
@SuppressWarnings("serial")
public class LoginView extends CssLayout implements View {

	public static final String VIEW_ID = "LoginView";

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginView.class);
	private static final String LABEL_TITLE = ApplicationResourceBundle.getInstance("vaadin-security").getString("loginPanel.title.caption");
	private static final String LABEL_WELCOME = ApplicationResourceBundle.getInstance("vaadin-security").getString("loginPanel.welcome.caption");
	private static final String LABEL_LOGIN = ApplicationResourceBundle.getInstance("vaadin-security").getString("loginPanel.username.caption");
	private static final String LABEL_PASSWORD = ApplicationResourceBundle.getInstance("vaadin-security").getString("loginPanel.password.caption");
	private static final String LABEL_SUBMIT = ApplicationResourceBundle.getInstance("vaadin-security").getString("loginPanel.submit.caption");
	private static final String LABEL_REMEMBER_ME = ApplicationResourceBundle.getInstance("vaadin-security").getString("loginPanel.rememberMe.caption");
	private static final String LABEL_FORGOT_PASSWORD = ApplicationResourceBundle.getInstance("vaadin-security").getString("loginPanel.forgotPassword.caption");
	private static final String LABEL_SELF_REGISTRATION = ApplicationResourceBundle.getInstance("vaadin-security").getString("loginPanel.selfRegistration.caption");
	private static final String LABEL_SELF_REGISTRATION_FORM_CAPTION = ApplicationResourceBundle.getInstance("vaadin-security").getString("loginPanel.selfRegistration.form.caption");
	private static final String LABEL_EMAIL_SENT = ApplicationResourceBundle.getInstance("vaadin-security").getString("loginPanel.emailSent.caption");
	private static final String LABEL_CONTACT_ADMIN = ApplicationResourceBundle.getInstance("vaadin-security").getString("loginPanel.contactAdmin.caption");
	private static final String LABEL_EMAIL_FAILURE = ApplicationResourceBundle.getInstance("vaadin-security").getString("loginPanel.failure.email");
	private static final IAuthentication AUTHENTICATION = Globals.getAuthentication();
	private final IMain main;
	private final Label message = new Label();

	private TextField username = new TextField(LABEL_LOGIN);
	private PasswordField password = new PasswordField(LABEL_PASSWORD);

	private String loginInformationString;

	public LoginView(final IMain main) {
		this.main = main;

		addStyleName("login-screen");

		// login form, centered in the available part of the screen
		Component loginForm = buildLoginForm();

		// layout to center login form when there is sufficient screen space
		// - see the theme for how this is made responsive for various screen
		// sizes
		VerticalLayout centeringLayout = new VerticalLayout();
		centeringLayout.setStyleName("centering-layout");
		centeringLayout.addComponent(loginForm);
		centeringLayout.setMargin(false);
		centeringLayout.setComponentAlignment(loginForm,
				Alignment.MIDDLE_CENTER);

		message.addStyleName("loginMessage");
		// information text about logging in

		addComponent(centeringLayout);

		// information text about logging in
		loginInformationString = (String) VaadinSpringContextHelper.getBean("loginInformation");
		if (!loginInformationString.isEmpty()) {
			addComponent(buildLoginInformation());
			centeringLayout.addStyleName("centering-layout-withinfotext");
		}
	}

	/**
	 *
	 * @return
	 */
	private CssLayout buildLoginInformation() {
		CssLayout loginInformation = new CssLayout();
		loginInformation.setStyleName("login-information");

		Label loginInfoText = new Label(loginInformationString, ContentMode.HTML);
		loginInfoText.setWidth("100%");
		loginInformation.addComponent(loginInfoText);
		return loginInformation;
	}

	// Add user object to session
	private void letUserIn(User user) {
		//VaadinSession.getCurrent().setAttribute(Constants.USER, user);
		main.userLoggedIn();
	}


	private Button createForgotPasswordButtonIfAllowed(TextField userNameField) {
		final ForgotPasswordService service = Globals.getForgotPasswordService();
		if (service.isAvailable()) {
			final Button button = new Button(LABEL_FORGOT_PASSWORD);
			button.addStyleName("forgotPasswordButton");
			button.addStyleName(ValoTheme.BUTTON_LINK);
			button.addClickListener((ClickListener) event -> {
				try {
					String result = service.resetPassword(userNameField.getValue());
					if (result == null)
						message.setValue(LABEL_EMAIL_SENT);
					else
						message.setValue(result);
				} catch (MailSendException e) {
					LOGGER.error("Feil ved nullstilling av passord", e);
					message.setValue(LABEL_EMAIL_FAILURE);
				}
			});
			button.setStyleName(ValoTheme.BUTTON_LINK);
			return button;
		}
		return null;
	}

	/**
	 * @return a link for self registration if the system allows it. Null if self registration is not allowed
	 */
	private Button createSelfRegistrationButtonIfAllowed() {
		SelfRegistration selfRegistration = VaadinSpringContextHelper.getBean(SelfRegistration.class);
		final AccessRole role = selfRegistration.getSelfRegistratationRole();
		if (role == null)
			return null;

		final Button button = new Button(LABEL_SELF_REGISTRATION);
		button.addStyleName("forgotPasswordButton");
		button.addStyleName(ValoTheme.BUTTON_LINK);
		button.addClickListener((ClickListener) event -> {
			final UserEditForm form = new UserEditForm();
			form.setPersonalMode();
			form.setCaption(LABEL_SELF_REGISTRATION_FORM_CAPTION);
			final no.goodtech.vaadin.security.model.User newEmptyUser = new no.goodtech.vaadin.security.model.User();
			newEmptyUser.setAccessRoles(new HashSet<>(Arrays.asList(role)));
			form.refresh(newEmptyUser);
			final SimpleInputBox popup = new SimpleInputBox(form, () -> {
				final no.goodtech.vaadin.security.model.User savedUser = new UserFinder().setId(form.getUserId()).find();
				if (savedUser != null) {
					letUserIn(savedUser.getLogin());
				}
			});
			UI.getCurrent().addWindow(popup);
		});
		button.setStyleName(ValoTheme.BUTTON_LINK);

		return button;
	}



	private Component buildLoginForm() {
		final VerticalLayout loginPanel = new VerticalLayout();
		loginPanel.setSizeUndefined();
		loginPanel.setMargin(false);
		Responsive.makeResponsive(loginPanel);
		loginPanel.addStyleName("login-panel");
		loginPanel.addComponent(buildLabels());
		loginPanel.addComponent(buildFields());

		CheckBox checkBox = new CheckBox(LABEL_REMEMBER_ME, true);

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSizeFull();
		horizontalLayout.addComponents(checkBox, message);
		horizontalLayout.setComponentAlignment(checkBox, Alignment.BOTTOM_LEFT);
		horizontalLayout.setComponentAlignment(message, Alignment.BOTTOM_RIGHT);
		horizontalLayout.setMargin(false);
		horizontalLayout.setSpacing(false);

		loginPanel.addComponent(horizontalLayout);
		return loginPanel;
	}

	private Component buildFields() {
		HorizontalLayout fields = new HorizontalLayout();
		fields.setMargin(false);
		fields.addStyleName("fields");

		final TextField username = new TextField(LABEL_LOGIN);
		username.setIcon(FontAwesome.USER);
		username.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
		username.focus();

		final PasswordField password = new PasswordField(LABEL_PASSWORD);
		password.setIcon(FontAwesome.LOCK);
		password.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);

		final Button submit = new Button(LABEL_SUBMIT);
		submit.addStyleName(ValoTheme.BUTTON_PRIMARY);
		submit.setClickShortcut(KeyCode.ENTER);

		fields.addComponents(username, password, submit);
		fields.setComponentAlignment(submit, Alignment.BOTTOM_LEFT);

		submit.addClickListener((ClickListener) event -> {
			try {
				User user = AUTHENTICATION.isAuthenticated(username.getValue(), password.getValue());
				if (user != null) {
					letUserIn(user);
				}
			} catch (LoginException e) {
				message.setValue(e.getMessage());
			}catch (CannotCreateTransactionException e){
				LOGGER.error("Could not connect to DB!");
				message.setValue("ERROR: Could not connect to database!");
			}
		});

		VerticalLayout verticalLayout = new VerticalLayout(fields);
		verticalLayout.setMargin(false);
		verticalLayout.setSpacing(false);

		Button forgotPassword = createForgotPasswordButtonIfAllowed(username);
		Button selfRegister = createSelfRegistrationButtonIfAllowed();

		verticalLayout.addComponent(new Label("&nbsp;", ContentMode.HTML));

		if (forgotPassword != null) {
			HorizontalLayout layOne = new HorizontalLayout(forgotPassword);
			forgotPassword.addStyleName(ValoTheme.BUTTON_LINK);
			verticalLayout.addComponent(layOne);
			verticalLayout.setComponentAlignment(layOne, Alignment.BOTTOM_RIGHT);
		}
		if (selfRegister != null) {
			HorizontalLayout layTwo = new HorizontalLayout(selfRegister);
			selfRegister.addStyleName(ValoTheme.BUTTON_LINK);
			verticalLayout.addComponent(layTwo);
			verticalLayout.setComponentAlignment(layTwo, Alignment.BOTTOM_RIGHT);
		}

		return verticalLayout;
	}


	private Component buildLabels() {
		CssLayout labels = new CssLayout();
		labels.addStyleName("labels");

		Label welcome = new Label(LABEL_WELCOME);
		welcome.setSizeUndefined();
		welcome.addStyleName(ValoTheme.LABEL_H4);
		welcome.addStyleName(ValoTheme.LABEL_COLORED);
		labels.addComponent(welcome);

		Label title = new Label(LABEL_TITLE);
		title.setSizeUndefined();
		title.addStyleName(ValoTheme.LABEL_H3);
		title.addStyleName(ValoTheme.LABEL_LIGHT);
		labels.addComponent(title);
		return labels;
	}

}
