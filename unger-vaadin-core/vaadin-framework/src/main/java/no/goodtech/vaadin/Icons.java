package no.goodtech.vaadin;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.server.ThemeResource;

/**
 * Oppslagsverk for ikoner
 * Vil cache ikoner etter første gang de er lastet
 */
public class Icons {
	private static Map<String, ThemeResource> icons = new HashMap<String, ThemeResource>();
	
	/**
	 * Henter ikonet med angitt navn i images-mappa. Fila må ende på .png
	 * @param fileName skriv kun filnavn uten endelse
	 */
	public static ThemeResource get(String fileName) {
		ThemeResource icon = icons.get(fileName);
		if (icon == null) {
			icon = new ThemeResource("images/" + fileName + ".png");
			icons.put(fileName, new ThemeResource("images/" + fileName + ".png"));
		}
		return icon; 
	}
}

