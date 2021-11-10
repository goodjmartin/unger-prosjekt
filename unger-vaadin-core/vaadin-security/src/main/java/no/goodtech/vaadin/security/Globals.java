package no.goodtech.vaadin.security;

import no.goodtech.vaadin.login.IAuthentication;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Globals {

	private static IAuthentication authentication;
	private static ForgotPasswordService forgotPasswordService;
	private static String primaryTheme;
	private static String secondaryTheme;
	private static boolean requireLogin;
	private static boolean requireMenuTranslation;
	private static String appURL;
	private static String loginURL;
	private static String loginProcessingURL;
	private static String logoutURL;
	private static PasswordEncoder passwordEncoder;

	public Globals(final IAuthentication authentication,final ForgotPasswordService forgotPasswordService,
				   final String primaryTheme, final String secondaryTheme, boolean requireLogin, boolean requireMenuTranslation,
				   final String appURL, final String loginURL, final String loginProcessingURL, final String logoutURL/*, final PasswordEncoder passwordEncoder*/)
	 {
		Globals.authentication = authentication;
		Globals.forgotPasswordService = forgotPasswordService;
		Globals.primaryTheme = primaryTheme;
		Globals.secondaryTheme = secondaryTheme;
		Globals.requireLogin = requireLogin;
		Globals.requireMenuTranslation = requireMenuTranslation;
		 Globals.appURL = appURL;
		 Globals.loginURL = loginURL;
		 Globals.loginProcessingURL = loginProcessingURL;
		 Globals.logoutURL = logoutURL;
		 //Globals.passwordEncoder = passwordEncoder;

	}

	public static IAuthentication getAuthentication() {
		return Globals.authentication;
	}

	static ForgotPasswordService getForgotPasswordService() {
		return forgotPasswordService;
	}

	static String getPrimaryTheme() {
		return primaryTheme;
	}

	static String getSecondaryTheme() {
		return secondaryTheme;
	}

	/**
	 * @return application requires the user to log in
	 */
	public static boolean isRequireLogin() {
		return requireLogin;
	}

	public static boolean isRequireMenuTranslation() {
		return requireMenuTranslation;
	}

	public static PasswordEncoder passwordEncoder(){
		return passwordEncoder;
	}

	public static String getAppURL() {
		return appURL;
	}

	public static String getLoginURL() {
		return loginURL;
	}

	public static String getLoginProcessingURL() {
		return loginProcessingURL;
	}

	public static String getLogoutURL() {
		return logoutURL;
	}

}
