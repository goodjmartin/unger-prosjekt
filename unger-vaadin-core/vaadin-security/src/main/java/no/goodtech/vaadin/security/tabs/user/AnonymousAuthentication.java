package no.goodtech.vaadin.security.tabs.user;

import no.goodtech.vaadin.login.IAuthentication;
import no.goodtech.vaadin.login.User;

public class AnonymousAuthentication implements IAuthentication {

	@Override
	public User isAuthenticated(final String id, final String password) {
		return getUser();
	}

	public User getUser() {
		return new User(0l, "anonymous", "Anonymous", null);
	}

}
