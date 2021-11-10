package no.goodtech.vaadin.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.goodtech.vaadin.security.model.AccessRole;
import no.goodtech.vaadin.security.model.AccessRoleFinder;

/**
 * Innstillinger for om brukere skal kunne registrere seg selv eller ikke
 * Dette er konfigurerbart via innstillingen "security.selfRegistrationRoleId"
 */
public class SelfRegistration {
	private static Logger LOGGER = LoggerFactory.getLogger(SelfRegistration.class);
    public final String roleId;

    public SelfRegistration(String roleId) {
    	this.roleId = roleId;
	}
    
    /**
     * Gir deg rollen selv-registrerte brukere skal ha 
     * @return hvilken rolle selv-registrerte bruker skal ha eller null om selv-registrering ikke er tillatt
     */
	public AccessRole getSelfRegistratationRole() {
		if (roleId != null && roleId.length() > 0) {
			final AccessRole role = new AccessRoleFinder().setId(roleId).find();
			if (role == null)
				LOGGER.warn("Fant ingen rolle med id = '{}' for selv-registrering", roleId);
			return role;
		}
		return null;
	}
}
