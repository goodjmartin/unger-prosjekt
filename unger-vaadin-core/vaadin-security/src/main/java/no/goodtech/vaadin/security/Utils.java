package no.goodtech.vaadin.security;

import no.goodtech.vaadin.security.tabs.user.SecurityUtils;

public class Utils {

	/**
	 * @return user ID of current logged in user. May be null
	 * @deprecated use @{@link no.goodtech.vaadin.security.tabs.user.SecurityUtils}
	 */
	public static String getUsername() {
		return SecurityUtils.getCurrentUserId();
	}
}