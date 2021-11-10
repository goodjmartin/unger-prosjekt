package no.goodtech.vaadin.security;

import no.goodtech.vaadin.global.Globals;
import no.goodtech.vaadin.login.IAuthentication;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.security.model.AccessRole;
import no.goodtech.vaadin.security.model.UserFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Set;


//@Component(value = "authentication")
public class SpringSecurityAuthentication implements IAuthentication {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String MESSAGE_WRONG_PASSWORD = ApplicationResourceBundle.getInstance("vaadin-security").getString("loginPanel.failure.wrongPassword");
	private static final String MESSAGE_USER_BLOCKED = ApplicationResourceBundle.getInstance("vaadin-security").getString("authentication.lockedUser");
	private static final String MESSAGE_WRONG_USERNAME_PASSWORD = ApplicationResourceBundle.getInstance("vaadin-security").getString("authentication.authenticationError");

	@Autowired
	private AuthenticationProvider authenticationProvider;

	@Override
	public User isAuthenticated(String id, String password) {
		no.goodtech.vaadin.security.model.User userDTO = new UserFinder().setId(id).find();

		// TODO some of these checks can be handled by spring security directly
		// Internal checks
		if (id == null || userDTO == null) {
			logger.warn(MESSAGE_WRONG_USERNAME_PASSWORD);
			throw new UsernameNotFoundException(MESSAGE_WRONG_USERNAME_PASSWORD);
		}
		if ((password == null) || password.equalsIgnoreCase("")) {
			logger.warn(MESSAGE_WRONG_USERNAME_PASSWORD);
			throw new BadCredentialsException(MESSAGE_WRONG_USERNAME_PASSWORD);
		}
		if (userDTO.getLoginFailures() >= Globals.getMaxLoginFailureAttempts()) {
			logger.warn(MESSAGE_USER_BLOCKED);
			throw new LockedException(MESSAGE_USER_BLOCKED);
		}
		if (userDTO.isBlocked()) {
			logger.warn(MESSAGE_USER_BLOCKED);
			throw new LockedException(MESSAGE_USER_BLOCKED);
		}

		final Authentication auth = new UsernamePasswordAuthenticationToken(id, password);

		try {
			// spring security authentication in a programmatic way
			final Authentication authenticated = authenticationProvider.authenticate(auth);
			SecurityContextHolder.getContext().setAuthentication(authenticated);

			userDTO.setLoginFailures(0);
			userDTO = userDTO.save();

			Set<String> accessFunctionIds = new HashSet<>();
			for (AccessRole accessRole : userDTO.getAccessRoles()) {
				accessFunctionIds.addAll(accessRole.getAccessFunctionIds());
			}
			return new User(userDTO.getPk(), id, userDTO.getName(), accessFunctionIds);
		} catch (AuthenticationException authenticationException) {
			logger.warn(authenticationException.getMessage());

			// Increment login failures
			final int numFailures = userDTO.getLoginFailures() + 1;
			userDTO.setLoginFailures(numFailures);
			userDTO.save();

			// Re-throw exception (with I18N message)
			throw new BadCredentialsException(String.format(MESSAGE_WRONG_PASSWORD, Globals.getMaxLoginFailureAttempts() - numFailures));
		}
	}
}
