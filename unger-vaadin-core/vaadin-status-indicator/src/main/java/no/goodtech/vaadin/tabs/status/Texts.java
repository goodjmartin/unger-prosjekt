package no.goodtech.vaadin.tabs.status;

import no.goodtech.vaadin.main.ApplicationResourceBundle;

import java.util.HashMap;
import java.util.Map;

/**
 * Gir tilgang til tekster lagret i ressursfilene som brukes av denne applikasjonen
 * Tekstene blir mellomlagret i minnet etter at de er hentet første gang
 * @author oystein
 */
public class Texts {

	private static final String RESOURCE_FILE_NAME = "vaadin-status-indicator";
	private static Map<String, String> texts = new HashMap<String, String>();
	
	/**
	 * Gir deg teksten som hører til angitt nøkkel
	 * @param key nøkkel
	 * @return teksten, eller "PropertyNotMapped" hvis den ikke finner noen tekst
	 * @throws RuntimeException hvis den ikke finner ressursfila
	 */
	public static String get(String key) {
		String text = texts.get(key);
		if (text == null) {
			text = ApplicationResourceBundle.getInstance(RESOURCE_FILE_NAME).getString(key);
			texts.put(key, text);
		}
		return text;
	}
}
