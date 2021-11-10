package no.goodtech.vaadin.security.tabs.user;

import java.util.HashSet;
import java.util.Set;

import no.goodtech.vaadin.global.Globals;
import no.goodtech.vaadin.security.model.AccessRole;
import no.goodtech.vaadin.security.model.UserFinder;

import no.goodtech.vaadin.login.IAuthentication;
import no.goodtech.vaadin.security.LoginException;
import no.goodtech.vaadin.security.LoginException.ErrorCode;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.security.MD5Hash;

/**
 * This class is responsible for authentication of the logged in user
 *
 * @author bakke
 */
@Deprecated
public class LoginViewAuthentication implements IAuthentication {

    @Override
    public User isAuthenticated(final String id, final String password) {
        User user;

        // Find the MD5 hash for the password
        String md5Password = MD5Hash.convert(password);

        // Find user by identity
		UserFinder userFinder = new UserFinder();
        no.goodtech.vaadin.security.model.User userDTO = userFinder.setId(id).find();

        if (userDTO != null) {
            if (userDTO.getLoginFailures() >= Globals.getMaxLoginFailureAttempts())
                throw new LoginException(ErrorCode.NO_MORE_ATTEMPTS, null, id);
            
            if (md5Password.equals(userDTO.getPassword())) {
                // Reset the login failure counter
                userDTO.setLoginFailures(0);
                userDTO.save();

                // Collect the access functions assigned to the user via assigned roles (duplicates will be stripped off)
                Set<String> accessFunctionIds = new HashSet<String>();
                for (AccessRole accessRole : userDTO.getAccessRoles()) {
                    accessFunctionIds.addAll(accessRole.getAccessFunctionIds());
                }

                // Create the new user object
                user = new User(userDTO.getPk(), id, userDTO.getName(), accessFunctionIds);
            } else {
                final int numFailures = userDTO.getLoginFailures() + 1;
				userDTO.setLoginFailures(numFailures);
                userDTO.save();
                throw new LoginException(ErrorCode.WRONG_PASSWORD, Globals.getMaxLoginFailureAttempts() - numFailures, id);
            }
        } else {
            throw new LoginException(ErrorCode.UNKNOWN_USERNAME, null, id);
        }
        return user;
    }

}
