package no.goodtech.vaadin.ui;

import no.goodtech.vaadin.main.ApplicationResourceBundle;

public class Texts {
	/**
	 * Hent en tekst-ressurs
	 * @param key ID til ressursen
	 * @return teksten, eller "PropertyNotMapped" hvis den ikke finner noen tekst
	 */
	public static String get(String key) {
		return ApplicationResourceBundle.getInstance("vaadin-core").getString(key);
	}
}
