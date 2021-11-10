package no.goodtech.admin.tabs.report;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.management.timer.Timer;

import no.goodtech.admin.report.DateParameterType;
import no.goodtech.vaadin.search.DateField;
import no.goodtech.vaadin.utils.Utils;

public class DateParameter extends DateField implements IReportParameterComponent {

	private static final SimpleDateFormat DATE_QUERY_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//	private static final Logger LOGGER = LoggerFactory.getLogger(DateParameter.class);
	private final String name;

	public DateParameter(DateParameterType type) {
		super(type.getLabel(), type.getFormat());
		
		if (type.getFormat() == null)
			setDateFormat(Utils.DATE_FORMAT);
		
		name = type.getName();
		addStyleName("dateParameter");

		// Set the current date as the default value, and apply offset
		if (type.getTime() != null)
			setValue(type.getTime());
		
		adjustTime(type.getOffsetInMinutes() * Timer.ONE_MINUTE);
	}

	public static void main(String[] args) throws ParseException {
		DateFormat formatter = new SimpleDateFormat("HH");
		System.out.println(formatter.parse("7"));
	}
	
	/**
	 * This method should be called to obtain the selected date
	 *
	 * @return The selected date
	 */
	@Override
	public String getValueAsString() {
		return getValue() != null ? DATE_QUERY_FORMAT.format(getValue()) : "";
	}

	public void setValueAsString(String value) {
		setValue(value);
	}

	@Override
	public String getNiceValue() {
		if (getValue() == null)
			return "";
		
		return new SimpleDateFormat(getDateFormat()).format(getValue());
	}
	
	public String getName() {
		return name;
	}
}
