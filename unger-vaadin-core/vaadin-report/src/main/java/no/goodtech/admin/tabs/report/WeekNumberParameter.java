package no.goodtech.admin.tabs.report;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.ComboBox;
import no.goodtech.admin.report.WeekNumberType;
import no.goodtech.vaadin.utils.Utils;

import java.util.Calendar;

/**
 * Creates two ComboBoxes, one for the year and one for the week number.
 * Returns the parameter of the first day of the week.
 *
 * How to set the period to a week:
 * 	declare @startDate smalldatetime = ?
 * 	select * from BilletReport where reportTime between @startDate and DATEADD(day, 7, @startDate)
 */
public class WeekNumberParameter extends CustomComponent implements IReportParameterComponent {
	private ComboBox year;
	private ComboBox weekNumber;
	private int hourOfDay;
	private final String name;

	public WeekNumberParameter(WeekNumberType type) {
		this(type.getYearLabel(), type.getWeekLabel(), type.getNumberOfYears(), type.getWeekOffset(), 
				type.getHourOfDay(), type.getName());
	}
	
	/**
	 * @param yearLabel caption for the year's ComboBox
	 * @param weekLabel caption for the week's ComboBox
	 * @param numberOfYears number of years shown in the year ComboBox. The default number is 30.
	 * @param weekOffset set an offset of the start week. weekOffset=-2 makes the week ComboBox start 2 weeks earlier.
	 * @param hourOfDay the specified hour of day can be set. The default value is 0 (00:00).
	 */
	public WeekNumberParameter(String yearLabel, String weekLabel, int numberOfYears, int weekOffset, int hourOfDay, String name) {
		this.hourOfDay = hourOfDay;
		this.name = name;

		initComboBoxes(yearLabel, weekLabel, numberOfYears, weekOffset);

		HorizontalLayout layout = new HorizontalLayout(year, weekNumber);
		layout.setMargin(false);
		setCompositionRoot(layout);
	}

	private void initComboBoxes(String yearLabel, String weekLabel, int numberOfYears, int weekOffset) {
		year = new ComboBox(yearLabel);
		year.setImmediate(true);
		year.setNullSelectionAllowed(false);

		Calendar calendar = Calendar.getInstance();
		int thisYear = calendar.get(Calendar.YEAR);

		for (int i = thisYear-numberOfYears; i < thisYear; i++) {
			int yearValue = i+1;
			year.addItem(yearValue);
			year.setItemCaption(yearValue, String.valueOf(yearValue));
		}
		year.setValue(thisYear);

		weekNumber = new ComboBox(weekLabel);
		weekNumber.setImmediate(true);
		weekNumber.setNullSelectionAllowed(false);

		for (int i = 0; i < 52; i++) {
			weekNumber.addItem(i+1);
		}
		weekNumber.setValue(calendar.get(Calendar.WEEK_OF_YEAR) + weekOffset);
	}

	@Override
	public void focus(){
		weekNumber.focus();
	}

	@Override
	public int getTabIndex() {
		return 0;
	}

	@Override
	public void setTabIndex(int i) {
	}

	@Override
	public String getValueAsString() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, (Integer) year.getValue());
		cal.set(Calendar.WEEK_OF_YEAR, (Integer) weekNumber.getValue());
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);

		return Utils.DATEDETAILTIME_FORMATTER.format(cal.getTime());
	}
	
	public void setValueAsString(String value) {
		int offset = Utils.parseOffset(value);
		if (offset != 0) {
			Calendar calendar = Calendar.getInstance();
			weekNumber.setValue(calendar.get(Calendar.WEEK_OF_YEAR) + offset);			
		} else {
			//TODO: Support absolute week numbers e.g. "2015/43" or only "43" for current year?
		}
	}
	
	@Override
	public String getNiceValue() {
		return year.getValue().toString() + "/" + weekNumber.getValue().toString();
	}

	public String getName() {
		return name;
	}

	@Override
	public void addValueChangeListener(Property.ValueChangeListener valueChangeListener) {
		// TODO
	}
}
