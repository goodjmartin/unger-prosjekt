package no.goodtech.admin.tabs.report;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import no.goodtech.admin.report.DateParameterType;

public class DateParameterTest {
	
	@Test
	public void testConfigureTime() {
		DateParameterType type = new DateParameterType();
		type.setFormat("yyyy-MM-dd");
		type.setTime("23:59:59.999");
		String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		
		DateParameter parameter = new DateParameter(type);
		Assert.assertEquals(today + " 23:59:59.999", parameter.getValueAsString());
	}

}
