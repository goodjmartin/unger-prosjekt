package no.goodtech.vaadin.search;

/**
 * A date selector with better support for configuration of format and offset.
 * Date/time is set automatically to current date/time
 * Default resolution is date, and will give you time at midnight on selected date
 */
public class DateTimeField extends com.vaadin.ui.DateTimeField {

//	private static final Logger LOGGER = LoggerFactory.getLogger(DateField.class);
//	protected static final Locale LOCALE = new Locale("no", "NO");
//	protected final SimpleDateFormat dateFormat;
//	private static Map<String, Long> FACTORS = new HashMap<>();
//
//	static {
//		FACTORS.put("s", Timer.ONE_SECOND);
//		FACTORS.put("m", Timer.ONE_MINUTE);
//		FACTORS.put("h", Timer.ONE_HOUR);
//		FACTORS.put("d", Timer.ONE_DAY);
//		FACTORS.put("w", Timer.ONE_WEEK);
//		FACTORS.put("M", Timer.ONE_DAY * 30);
//		FACTORS.put("y", Timer.ONE_DAY * 365);
//	}
//
//	/**
//	 * Create field
//	 *
//	 * @param caption
//	 *            caption
//	 * @param format
//	 *            date format that is shown in the field. Expects same format
//	 *            when parsing. The format controls the resolution. If null,
//	 *            defaults to "yyyy-MM-dd". If formats contains "mm", resolution
//	 *            will be in minutes or else the resolution will be in days
//	 */
//	public DateTimeField(String caption, String format) {
//		super(caption, LocalDateTime.now());
//		if (format == null) {
//			format = Utils.DATE_FORMAT;
//		}
//		dateFormat = new SimpleDateFormat(format);
//
//		// Set the correct resolution
//		if (format.contains("mm")) {
//			setResolution(DateTimeResolution.MINUTE);
//		} else {
//			setValue(getValue().withHour(0).withMinute(0).withSecond(0).withNano(0));
//			setResolution(DateTimeResolution.DAY);
//			setShowISOWeekNumbers(true);
//		}
//		setDateFormat(format);
//		setLocale(LOCALE);
//	}
//
//	/**
//	 * Creates dateField that shows only date (and not time)
//	 */
//	public DateTimeField(String caption) {
//		this(caption, null);
//	}
//	/**
//	 * Adjust time. If date/time not set before, time is set to current date/time before adjusting
//	 * @param offsetInMinutes offset value in minutes
//	 */
//	public void adjustTime(int offsetInMinutes) {
//		adjustTime(offsetInMinutes * Timer.ONE_MINUTE);
//	}
//
//	/**
//	 * Adjust time. If date/time not set before, time is set to current date/time before adjusting
//	 * @param offsetInMillis offset value in millis
//	 */
//	public void adjustTime(long offsetInMillis) {
//		if (getValue() == null) {
//			setValue(LocalDateTime.now());
//		}
//
//		if (offsetInMillis > 0) {
//			getValue().plus(offsetInMillis, ChronoUnit.MILLIS);
//		} else if (offsetInMillis < 0) {
//			getValue().minus(offsetInMillis, ChronoUnit.MILLIS);
//		}
//	}
//
//	private void setTime(String datetime) {
//		Calendar timeOffsetCalendar = Calendar.getInstance();
//
//		//if datetime also includes date (not only time)
//		Date parsedDate = Utils.parseTime(datetime, "yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH", "yyyy-MM-dd", "yyyy-MM");
//		if (parsedDate != null) {
//			setValue(parsedDate);
//		} else {
//			Date parsedTime = Utils.parseTime(datetime);
//			if (parsedTime != null) {
//				Calendar calendar = Calendar.getInstance();
//				if (getValue() == null)
//					calendar.setTime(new Date()); //setting date to today if date not set earlier
//				else
//					calendar.setTime(getValue());
//
//				//adjust time
//				timeOffsetCalendar.setTime(parsedTime);
//				calendar.set(Calendar.HOUR_OF_DAY, timeOffsetCalendar.get(Calendar.HOUR_OF_DAY));
//				calendar.set(Calendar.MINUTE, timeOffsetCalendar.get(Calendar.MINUTE));
//				calendar.set(Calendar.SECOND, timeOffsetCalendar.get(Calendar.SECOND));
//				calendar.set(Calendar.MILLISECOND, timeOffsetCalendar.get(Calendar.MILLISECOND));
//				setValue(calendar.getTime());
//			} else {
//				LOGGER.warn("Kan ikke tolke flg. tidspunkt i dato-felt: '" + datetime + "'");
//			}
//		}
//	}
//
//	public static void main(String[] args) throws ParseException {
//		DateFormat formatter = new SimpleDateFormat("HH");
//		System.out.println(formatter.parse("7"));
//	}
//
//	/**
//	 * Set or adjust date/time
//	 * If date/time not set before, it will be set to current time before adjusting
//	 * @param value time offset, exact date or time OR a combination of offset and time of day.
//	 * Offset must start with + or - and possibly end with s, m, h, d, w, M or y for seconds, minutes, hours, days, weeks, months or years.
//	 * Valid date/time formats are: yyyy-MM-dd HH:mm:ss.SSS, yyyy-MM-dd HH:mm:ss, yyyy-MM-dd HH:mm, yyyy-MM-dd HH, yyyy-MM-dd, HH:mm:ss.SSS, HH:mm:ss, HH:mm, HH.
//	 * A combination of offset and time of day must be splitted by "@".
//	 * Examples:
//	 * -1d = yesterday at same time as now,
//	 * 07 = today at 07:00,
//	 * -1d@07 = yesterday at 07:00,
//	 * 2015-12-25 = christmas day in 2015 at 00:00
//	 */
//	public void setValue(String value) {
//		if (value != null) {
//			int indexOfDelimiter = value.indexOf("@");
//			if (indexOfDelimiter != -1) {
//				setTime(value.substring(indexOfDelimiter + 1));
//				adjustTime(parseOffset(value.substring(0, indexOfDelimiter)));
//			} else {
//				long offset = parseOffset(value);
//				if (offset != 0) {
//					adjustTime(offset);
//				} else {
//					setTime(value);
//				}
//			}
//		}
//	}
//
//	protected long parseOffset(String value) {
//		// TODO should probably check which month when offset is based on months
//		if (value != null && (value.startsWith("-") || value.startsWith("+"))) {
//			String content = value.substring(0, value.length() - 1);
//			String tail = value.substring(value.length() - 1, value.length());
//			if (FACTORS.keySet().contains(tail)) {
//				Long offset = Long.valueOf(content);
//				return offset * FACTORS.get(tail);
//			} else {
//				return Long.valueOf(value);
//			}
//		}
//		return 0L;
//	}
}
