package no.goodtech.vaadin.utils;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Link;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.timer.Timer;
import java.math.RoundingMode;
import java.text.*;
import java.util.Calendar;
import java.util.Date;

/**
 * Generic utils for date, urls and other handy functions.
 */
public class Utils {
	private static Logger LOGGER = LoggerFactory.getLogger(Utils.class);

	// TODO: add the specified formats to a resource file?
	public static String DATE_FORMAT = "yyyy-MM-dd";
	public static String TIME_FORMAT = "HH:mm";
	public static String DETAILTIME_FORMAT = "HH:mm:ss";
	public static String DATETIME_FORMAT = DATE_FORMAT + " " + TIME_FORMAT;
	public static String DATEDETAILTIME_FORMAT = DATE_FORMAT + " " + DETAILTIME_FORMAT;

	public static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);
	public static SimpleDateFormat DATETIME_FORMATTER = new SimpleDateFormat(DATETIME_FORMAT);
	public static SimpleDateFormat DATEDETAILTIME_FORMATTER = new SimpleDateFormat(DATEDETAILTIME_FORMAT);
	public static SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat(TIME_FORMAT);
	public static SimpleDateFormat DETAILTIME_FORMATTER = new SimpleDateFormat(DETAILTIME_FORMAT);

	public static final DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS = new DecimalFormatSymbols();
	public static DecimalFormat DECIMAL_FORMAT = createDecimalFormat("###,###,###,##0.000");
	public static DecimalFormat DECIMAL_SHORT_FORMAT = createDecimalFormat("###,###,###,##0.###");

	static {
		DECIMAL_FORMAT_SYMBOLS.setGroupingSeparator(' ');
		DECIMAL_FORMAT_SYMBOLS.setDecimalSeparator(',');
	}

	public static DecimalFormat createDecimalFormat(String pattern) {
        DecimalFormat format = new DecimalFormat(pattern, Utils.DECIMAL_FORMAT_SYMBOLS);
        format.setRoundingMode(RoundingMode.HALF_UP);
		return format;
	}

	private static PeriodFormatter DURATION_TEXT_FORMAT = new PeriodFormatterBuilder()
		.printZeroNever()
		.appendYears()
		.appendSuffix(getPeriodText("year"), getPeriodText("years"))
		.appendSeparator(", ")
		.appendMonths()
		.appendSuffix(getPeriodText("month"), getPeriodText("months"))
		.appendSeparator(", ")
		.appendWeeks()
		.appendSuffix(getPeriodText("week"), getPeriodText("weeks"))
		.appendSeparator(", ")
		.appendDays()
		.appendSuffix(getPeriodText("day"), getPeriodText("days"))
		.appendSeparator(", ")
		.appendHours()
		.appendSuffix(getPeriodText("hour"), getPeriodText("hours"))
		.appendSeparator(", ")
		.appendMinutes()
		.appendSuffix(getPeriodText("minute"), getPeriodText("minutes"))
		.appendSeparator(", ")
		.appendSeconds()
		.appendSuffix(getPeriodText("second"), getPeriodText("seconds"))
		.toFormatter();

	public static boolean isNumeric(String inputData) {
		return inputData != null && inputData.matches("[-+]?\\d+(\\.\\d+)?");
	}

	public static boolean isInteger(String inputData){
		return inputData != null && inputData.matches("^[+-]?\\d+$");
	}

	/**
	 * URL schemes supported: [http, https, file, ftp, ldap]
	 * @param stringLink the url which is converted to a Link
	 * @return the Link if the string matches the url-pattern and null if not an url
	 */
	public static Link convertString(final String stringLink) {
		String urlRegex = "\\b(https?|ftp|file|ldap)://" + "[-ÆØÅæøåA-Za-z0-9+&@#/%?=~_|!:,.;]" + "*[-ÆØÅæøåA-Za-z0-9+&@#/%=~_|]";
		if (stringLink != null && stringLink.matches(urlRegex)) {
			Link link = new Link(stringLink, new ExternalResource(stringLink.toString()));
			link.setTargetName("_blank");
			return link;
		}
		return null;
	}

	/**
	 * @param stringLink url as a string
	 * @return true: the string is an valid url, false: the string does not match an url-pattern
	 */
	public static boolean convertStringOk(String stringLink) {
		return (convertString(stringLink) != null);
	}

	/**
	 * Formats a time period like this: HH:mm:ss
	 * @param millis the time period in millis
	 * @return the time period on this format: HH:mm:ss
	 */
	public static String formatDuration(long millis) {
		long second = millis / Timer.ONE_SECOND % 60;
		long minute = millis / Timer.ONE_MINUTE % 60;
		long hour = millis / Timer.ONE_HOUR;
		return String.format("%02d:%02d:%02d", hour, minute, second);
	}

	/**
	 * Formats a time period like this: x days, y hours, z minutes, 1 second
	 * @param millis the time period in millis
	 * @return the time period on this format: x days, y hours, z minutes, 1 second
	 */	
	public static String formatDurationAsText(long millis) {
		return DURATION_TEXT_FORMAT.print(new Period(millis).normalizedStandard());
	}

	/**
	 * @return the date object of yesterday
	 */
	public static Date getYesterday(){
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);
			return cal.getTime();
	}

	/**
	 * Gets the formatted diff between two dates. If one of the dates are null, it becomes 'new Date()'
	 * @param fromDate the start date
	 * @param toDate the end date
	 * @return a formatted string for the diff. Format: HH:mm:ss
	 */
	public static String getFormattedDateDiff(Date fromDate, Date toDate){
		Date fromDateFormatted = (fromDate != null) ? fromDate : new Date();
		Date toDateFormatted = (toDate != null) ? toDate : new Date();
		long millis = toDateFormatted.getTime() - fromDateFormatted.getTime();
		return Utils.formatDuration(millis);
	}
	
	public static FontAwesome findIcon(String iconName) {
		for (FontAwesome font : FontAwesome.values())
			if (font.name().equals(iconName))
				return font;
		return null;
	}
	
	private static String getPeriodText(String key) {
		return " " + getText("period." + key);
	}

	public static String getText(String key) {
		return ApplicationResourceBundle.getInstance("vaadin-core").getString(key);
	}
	
	public static int parseOffset(String value) {
		if (value != null && (value.startsWith("-") || value.startsWith("+")))
			return Integer.valueOf(value);
		return 0;
	}
	
	/**
	 * Parse a time offset
	 * @param timeOfDay you may use these formats: HH:mm:ss.sss, HH:mm:ss, HH:mm, HH
	 * @return a date on 1/1-1970 with given time offset. Null if not able to parse 
	 * @see Utils#parseTime(String, String...)
	 */
	public static Date parseTime(String timeOfDay) {
		return parseTime(timeOfDay, "HH:mm:ss.SSS", "HH:mm:ss", "HH:mm", "HH");
	}

	/**
	 * Parse a time offset in one of the formats you specify
	 * @return a date on 1/1-1970 with given time offset. Null if not able to parse 
	 * @see Utils#parseTime(String)
	 */
	public static Date parseTime(String timeOfDay, String... formats) {
		for (String format : formats) {
			DateFormat formatter = new SimpleDateFormat(format);
			try {
			  return formatter.parse(timeOfDay);
			} catch (ParseException e) {
			}
		}
		return null;
	}

	/**
	 * Fetch a defined caption from a resource bundle for a given class/entity
	 * @param clazz the class/entity to fetch a caption for
	 * @return a defined caption fetched from the correct resource bundle for a specific class/entity
	 */
	public static String getEntityCaption(Class<?> clazz){
		// Check if the bean is annotated with '@Bundle'
		if (clazz.isAnnotationPresent(Bundle.class)) {
			Bundle bundleFile = clazz.getAnnotation(Bundle.class);
			return ApplicationResourceBundle.getInstance(bundleFile.resourceBundle()).getString(clazz.getName());
		}

		LOGGER.error("Could not find caption for " + clazz.getName() + ". Try using @Bundle");
		return null;
	}
}
