package no.goodtech.vaadin.search;

import com.vaadin.v7.shared.ui.datefield.Resolution;
import no.goodtech.vaadin.utils.Utils;
import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DateFieldTest {

	private static final Locale LOCALE = new Locale("no", "NO");

	@Test
	public void resolution() {
		
		Date now = new Date();
		
		//Date todayAtMidnight = 
		//verify that default resolution is day
		DateField field = new DateField(null, null);
		Assert.assertEquals(Resolution.DAY, field.getResolution());
		Assert.assertEquals(todayAtMidnight(), field.getValue());

		//verify that resolution is minutes if mm is specified in format
		field = new DateField(null, "yyyy-MM-dd HH:mm");
		field.setValue(now);
		Assert.assertEquals(Resolution.MINUTE, field.getResolution());
		Assert.assertEquals(now, field.getValue());

		
		field = new DateField(null, "yyyy-MM-dd HH:mm:ss");
		field.setValue(now);
		Assert.assertEquals(Resolution.MINUTE, field.getResolution());
		Assert.assertEquals(now, field.getValue());		
	}

	@Test
	public void testSetValue() throws ParseException {
		DateField field = new DateField(null, null);
		field.setValue("2017-03");
		Assert.assertEquals(Utils.DATETIME_FORMATTER.parse("2017-03-01 00:00"), field.getValue());
	}
	
	/**
	 * Test of adjusting date and time by parsing date/time or offset
	 */
	@Test
	public void testOffSetValue() throws ParseException {
		Calendar now = Calendar.getInstance(new Locale("no", "NO"));
		now.setTime(new Date());
		
		//test that default date is set to today automatically
		DateField dateTimeField = new DateField(null, Utils.DATETIME_FORMAT);
		dateTimeField.setValue("06:15"); //set time of day
		now.set(Calendar.HOUR_OF_DAY, 6);
		now.set(Calendar.MINUTE, 15);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		Assert.assertEquals(now.getTime(), dateTimeField.getValue());

		//test that we can apply date and time as a text
		dateTimeField.setValue("2016-08-25 08:00");
		Assert.assertEquals(Utils.DATETIME_FORMATTER.parse("2016-08-25 08:00"), dateTimeField.getValue());

		dateTimeField.setValue("06:15"); //adjust time of day
		Assert.assertEquals(Utils.DATETIME_FORMATTER.parse("2016-08-25 06:15"), dateTimeField.getValue());

		dateTimeField.setValue("+60m"); //advance time one hour
		Assert.assertEquals(Utils.DATETIME_FORMATTER.parse("2016-08-25 07:15"), dateTimeField.getValue());

		dateTimeField.setValue("-1h"); //adjust time one hour backwards
		Assert.assertEquals(Utils.DATETIME_FORMATTER.parse("2016-08-25 06:15"), dateTimeField.getValue());

		dateTimeField.setValue("-720m"); //adjust time 12 hours backwards
		Assert.assertEquals(Utils.DATETIME_FORMATTER.parse("2016-08-24 18:15"), dateTimeField.getValue());

		dateTimeField.setValue("-1d@08:00"); //set time to 08:00 the day before
		Assert.assertEquals(Utils.DATETIME_FORMATTER.parse("2016-08-23 08:00"), dateTimeField.getValue());

		dateTimeField.setValue("+24h@09:30"); //set time to 09:30 the day after
		Assert.assertEquals(Utils.DATETIME_FORMATTER.parse("2016-08-24 09:30"), dateTimeField.getValue());

		dateTimeField.setValue("23:59:59.999"); //adjust time of day
		Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2016-08-24 23:59:59.999"), dateTimeField.getValue());

		dateTimeField.setValue("+1M@2016-08-25 08:00");
		Assert.assertEquals(Utils.DATETIME_FORMATTER.parse("2016-09-24 08:00"), dateTimeField.getValue());

		//test of setting date only (not time)
		DateField dateField = new DateField(null, Utils.DATE_FORMAT);
		dateField.setValue("2016-08-25");
		Assert.assertEquals(Utils.DATETIME_FORMATTER.parse("2016-08-25 00:00"), dateField.getValue()); //should be today at midnight


	}
	
	private Date todayAtMidnight() {
		Calendar calendar = Calendar.getInstance(LOCALE);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
}
