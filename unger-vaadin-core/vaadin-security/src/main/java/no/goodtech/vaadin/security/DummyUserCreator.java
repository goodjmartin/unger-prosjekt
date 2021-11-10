package no.goodtech.vaadin.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.goodtech.vaadin.security.model.AccessRole;
import no.goodtech.vaadin.security.model.User;


/**
 * Tjeneste for å generere opp en test-bruker f.eks. mot en minne-database
 * @author oystein
 */
public class DummyUserCreator {

	private static Logger LOGGER = LoggerFactory.getLogger(DummyUserCreator.class);
	
	public void run() {
		final String password = "Goodtech";
        final String md5Password = MD5Hash.convert(password);
		User user = new User("messy", md5Password, null);
		user.setEmail("oeystein.myhre@goodtech.no");
		user = user.save();
		LOGGER.info("Opprettet dummy-bruker: '{}' med flg. passord: '{}'", user.getId(), password);
		
		AccessRole role = new AccessRole();
		role.setId("DummyRole");
		role = role.save();
		LOGGER.info("Opprettet dummy-rolle: '{}'", role.getId());
	}
}
