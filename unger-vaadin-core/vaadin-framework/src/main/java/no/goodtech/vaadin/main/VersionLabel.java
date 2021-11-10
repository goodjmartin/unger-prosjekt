package no.goodtech.vaadin.main;

import com.vaadin.v7.ui.Label;
import no.goodtech.vaadin.global.Globals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Viser versjonsnummer for applikasjonen
 */
public class VersionLabel extends Label {

	private static Logger LOGGER = LoggerFactory.getLogger(VersionLabel.class);
	/**
	 * Opprett komponenten og hent versjonsnummer fra property-fil. 
	 * @param defaultCaption tekst som vises hvis jeg ikke finner versjonsnummeret
	 */
	public VersionLabel(String defaultCaption) {
		setStyleName("versionLabel");
		setValue(Globals.getApplicationTitle() + " " + getVersion());
	}
	
	private static String getVersion() {
		String version = ApplicationResourceBundle.getInstance("system").getString("version");
		if ((version != null) && (!"PropertyNotMapped".equals(version))) {
			String prefix = ApplicationResourceBundle.getInstance("vaadin-core").getString("versionLabel.prefix");
			return prefix + " " + version;
		} else {
			LOGGER.info("No 'version' property set in system.properties");
			return "";
		}
	}
}
