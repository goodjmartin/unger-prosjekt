package no.goodtech.vaadin.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PiWebApiUtils {

	public static final SimpleDateFormat PI_QUERY_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	public static final SimpleDateFormat PI_SIMPLIFIED_POINT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

	/**
	 * Parse a date fetched from PI WEB API 
	 * The dates are almost ISO 8601, but the number of fractional seconds can vary from 0 to at least 7.
	 * Since Java Date only support milliseconds, we truncate the date if more than than 3 digits in the fractions part 
	 * @param piDate a date from PI
	 * @return a Java date
	 */
	public static Date parseDate(String piDate) {
		
		//convert date string to standardized string: yyyy-MM-dd'T'HH:mm:ss.SSS 
		piDate = piDate.substring(0, piDate.length() - 1); //remove Z
		if (piDate.length() == 19)
			piDate += "."; 
		if (piDate.length() < 23) {
			//pad with .000
			while (piDate.length() < 23)
				piDate += "0";
		} else if (piDate.length() > 23) {
			piDate = piDate.substring(0, 23); //skip the rest
		}
		
		try {
			return PI_SIMPLIFIED_POINT_DATE_FORMAT.parse(piDate);
		} catch (ParseException e) {
//			logger.warn("Point ignored", e);
			return null;
		}
	}
}
