package no.goodtech.vaadin.main;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Ressurs-oppslags-tjeneste.
 * Brukes for å hente ressurser fra ressursfiler, f.eks. tekster som ikke skal hardkodes i applikasjonen.
 * Foreløpig støttes bare lokale for norsk bokmål.
 * Tekster blir automatisk konvertert til UTF-8.
 * Klassen er en singleton, så bruk {@link #getInstance(String)} for å få tak i objektet
 */
public class ApplicationResourceBundle {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationResourceBundle.class);
	private static final Map<String, ApplicationResourceBundle> applicationResourceBundles = new HashMap<>();
	private volatile ResourceBundle resourceBundle = null;

	private ApplicationResourceBundle(final String id) {
        Locale locale = Locale.getDefault();
        try {
            resourceBundle = PropertyResourceBundle.getBundle(id, locale);
            applicationResourceBundles.put(id, this);
        } catch (MissingResourceException e) {
            logger.warn("Resource bundle not found: id='{}', locale='{}'", id, locale);
        }
	}

	/**
	 * Få tilgang til ressurser i fil med angitt id
	 * @param id denne id'en tilsvarer første del av filnavnet i fila med dette mønsteret: <id>_<locale>.properties,
	 * eksempel for norsk bokmål: <id>_nb_NO.properties
	 * @return ressurs-oppslags-tjeneste for angitt fil
	 */
	public static synchronized ApplicationResourceBundle getInstance(final String id) {
		ApplicationResourceBundle applicationResourceBundle = applicationResourceBundles.get(id);

		if (applicationResourceBundle == null) {
			applicationResourceBundle = new ApplicationResourceBundle(id);
		}

		return applicationResourceBundle;
	}

	/**
	 * Hent tekst med angitt nøkkel
	 * @param key nøkkel
	 * @return teksten, eller "PropertyNotMapped" hvis den ikke finner noen tekst
	 * @throws RuntimeException hvis den ikke finner ressursfila
	 */
    public String getString(final String key) {
        String value = "PropertyNotMapped";

        if (resourceBundle != null) {
            try {
				value = new String (resourceBundle.getString(key).getBytes("ISO-8859-1"), "UTF-8");
            } catch (MissingResourceException e) {
                logger.error("MissingResourceException - " + e.getMessage());
			} catch (UnsupportedEncodingException e) {
                logger.error("UnsupportedEncodingException - " + e.getMessage());
			}
        } else {
            logger.error(String.format("Resource bundle not found: key=%s", key));
        }
        logger.debug(String.format("Fetched resource: key=%s, value=%s", key, value));
		return value;
    }
}
