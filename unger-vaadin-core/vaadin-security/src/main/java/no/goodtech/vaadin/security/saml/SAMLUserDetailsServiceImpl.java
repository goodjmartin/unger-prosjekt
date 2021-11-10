package no.goodtech.vaadin.security.saml;


import no.goodtech.vaadin.security.Globals;
import no.goodtech.vaadin.security.PasswordGenerator;
import no.goodtech.vaadin.security.model.AccessRole;
import no.goodtech.vaadin.security.model.AccessRoleFinder;
import no.goodtech.vaadin.security.model.UserFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;


@Service
@Profile(value = {"saml"})
public class SAMLUserDetailsServiceImpl implements SAMLUserDetailsService {

	@Value("${security.saml.defaultUserRole}")
	private String defaultUserRole;

	private static final Logger LOGGER = LoggerFactory.getLogger(SAMLUserDetailsServiceImpl.class);

	public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {

		if (credential == null) {
			LOGGER.error("No credential was given for SAML authentication!");
			return null;
		}

		LOGGER.debug("Loading user by SAML [" + credential.getNameID() + "] [" + credential.getLocalEntityID() + "] [" + credential.getRemoteEntityID() + "] [" + credential.getAdditionalData() + "]");

		// The method is supposed to identify local account of user referenced by
		// data in the SAML assertion and return UserDetails object describing the user.
		String userID = credential.getNameID().getValue();
		no.goodtech.vaadin.security.model.User gmiUser = new UserFinder().setId(userID).find();

		if (gmiUser == null) {

			// Create user if not found internally in GMI
			String generatedPsw = new PasswordGenerator.PasswordGeneratorBuilder().usePunctuation(false).build().generate(10);
			String lastName = credential.getAttributeAsString("LastName");
			String firstName = credential.getAttributeAsString("FirstName");
			String email = credential.getAttributeAsString("Email");

			List<AccessRole> roles = null;
			if (defaultUserRole != null && !defaultUserRole.isEmpty())
				roles = new AccessRoleFinder().setId(defaultUserRole).loadList();

			gmiUser = new no.goodtech.vaadin.security.model.User(userID,"foo", null);
			if (roles != null)
				gmiUser.setAccessRoles(new HashSet<>(roles));
			gmiUser.setEmail(email);
			gmiUser.setName(firstName + " " + lastName);
			gmiUser = gmiUser.save();
			LOGGER.info("No user was found based on SAML credentials. Creating new user for " + firstName + " " + lastName + " [" + email + "]");
		} else {
			// Update email if different from credentials

			String email = credential.getAttributeAsString("Email");
			if ((gmiUser.getEmail() == null || !gmiUser.getEmail().equals(email)) && email != null && !email.isEmpty()) {
				gmiUser.setEmail(email);
				gmiUser = gmiUser.save();
				LOGGER.info("Updating user's email from SAML login. User with GMI pk [" + gmiUser.getPk() + "] and GMI id [" + gmiUser.getId() + "] was given new email.");
			}
		}

		// Might have to look into how blocked users should receive a message that their user is blocked.
		if (gmiUser.isBlocked() != null && gmiUser.isBlocked()) {
			LOGGER.warn("A blocked user tried to log in via SAML. SAML User with GMI pk [" + gmiUser.getPk() + "] and GMI id [" + gmiUser.getId() + "]");
			throw new UsernameNotFoundException("Brukernavnet er ikke godkjent eller bruker er blokkert. " +
					"Hør med en administrator! " +
					"Oppgi: SAML User with GMI pk [" + gmiUser.getPk() + "] and GMI id [" + gmiUser.getId() + "]");
		}

		LOGGER.info("SAML User with GMI pk [" + gmiUser.getPk() + "] and GMI id [" + gmiUser.getId() + "] successfully logged in.");

		// Roles are set to empty set because this is handled internally and not by spring security
		return new User(gmiUser.getId(), gmiUser.getPassword(), Collections.EMPTY_SET);
	}


}
