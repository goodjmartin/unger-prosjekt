package no.goodtech.vaadin.security.tabs.user;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinSession;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.security.Globals;
import no.goodtech.vaadin.security.model.AccessRole;
import no.goodtech.vaadin.security.model.UserFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.util.*;

public class SecurityUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityUtils.class);

	public static final String ANONYMOUS_USER_ID = "anonymous";

	/**
	 * Update users matching oldPassword to the new password with safer encryption
	 * @param oldPassword the old password
	 * @param newPassword the newly encrypted password
	 */
	public static void updateLegacyPasswords(String oldPassword, String newPassword) {
		List<no.goodtech.vaadin.security.model.User> usersWithOldPassword = new UserFinder().setPassword(oldPassword).loadList();
		for (no.goodtech.vaadin.security.model.User user : usersWithOldPassword) {
			user.setPassword(newPassword);
			user.save();
		}
	}

	/**
	 * Log out current user and navigate to login screen.
	 */
	public static void logOut() {
		final User user = getCurrentUser();
		if (user != null)
			LOGGER.info("User with pk [" + user.getPk() + "] + and id [" + user.getId() + "] logged out.");

		try {
			((VaadinServletRequest) VaadinService.getCurrentRequest()).getHttpServletRequest().logout();
			VaadinSession.getCurrent().close();
			SecurityContextHolder.clearContext();

			String entryPoint = Globals.isRequireLogin() ? Globals.getLoginURL() : Globals.getAppURL();
			Page.getCurrent().setLocation(entryPoint);
			Page.getCurrent().setLocation(Globals.getLogoutURL());
		} catch (ServletException e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * Get the user id of the currently logged in user
	 */
	public static String getCurrentUserId() {
		if (!Globals.isRequireLogin()) {
			LOGGER.debug("Returning anonymous user id");
			return ANONYMOUS_USER_ID;
		}

		VaadinServletRequest request = (com.vaadin.server.VaadinServletRequest) VaadinService.getCurrentRequest();

		if (request == null) {
			LOGGER.debug("No Vaadin request is set.");
			return null;
		}



		HttpSession sess = request.getHttpServletRequest().getSession();
		if (sess == null) {
			LOGGER.debug("Could not find any session for the current request.");
			return null;
		}

		SecurityContextImpl context = (SecurityContextImpl) sess.getAttribute("SPRING_SECURITY_CONTEXT");
		if (context == null) {
			LOGGER.debug("Could not find any spring security context in the current session");
			return null;
		}

		Authentication auth = context.getAuthentication();
		if (auth == null) {
			LOGGER.debug("No authentication is registered for the current session");
			return null;
		}

		Object cred = auth.getPrincipal();
		if (cred == null) {
			LOGGER.debug("No credentials registered in the authentication token for the current session");
			return null;
		}

		if (cred instanceof org.springframework.security.core.userdetails.User) {
			String userName = ((org.springframework.security.core.userdetails.User)cred).getUsername();
			LOGGER.debug("Current username (as User): " + userName);
			return userName;
		}else if (cred instanceof String){
			LOGGER.debug("Current username (as String): " + cred);
			return (String) cred;
		}
		return null;
	}

	/**
	 * Get the currently logged in user
	 */
	public static User getCurrentUser() {

		if (!Globals.isRequireLogin()) {
			LOGGER.debug("Returning anonymous user");
			return new User(0L, "anonymous", "Anonymous", null);
		}

		String userId = getCurrentUserId();

		if (userId != null) {
			no.goodtech.vaadin.security.model.User userDTO = new UserFinder().setId(userId).find();
			if (userDTO == null) {
				LOGGER.warn("No user for user id [" + userId + "] was found");
				return null;
			}

			Set<String> accessFunctionIds = new HashSet<>();
			for (AccessRole accessRole : userDTO.getAccessRoles()) {
				accessFunctionIds.addAll(accessRole.getAccessFunctionIds());
			}
			return new User(userDTO.getPk(), userDTO.getId(), userDTO.getName(), accessFunctionIds);
		}
		LOGGER.warn("Could not find any user id for the current request");
		return null;
	}

}

