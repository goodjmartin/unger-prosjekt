package no.goodtech.vaadin.chart;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class TimeSeriesTest {

	@Test
	public void testLongestGap() {
		TimeSeries series = new TimeSeries("test", null);
		Assert.assertNull(series.getLongestGapEnd());
		Assert.assertEquals(0, series.getLongestGap());
		
		series.add(new Date(0), 1);
		Assert.assertNull(series.getLongestGapEnd());
		Assert.assertEquals(0, series.getLongestGap());
		
		series.add(new Date(1000), 2);
		Assert.assertEquals(1000, series.getLongestGapEnd().getTime());
		Assert.assertEquals(1000, series.getLongestGap());
		
		series.add(new Date(2000), 3); //same gap as last point, longest gap is not changed
		Assert.assertEquals(1000, series.getLongestGapEnd().getTime());
		Assert.assertEquals(1000, series.getLongestGap());

		series.add(new Date(4000), 4); //longer gap
		Assert.assertEquals(4000, series.getLongestGapEnd().getTime());
		Assert.assertEquals(2000, series.getLongestGap());
		
		series.add(new Date(4500), 5); //shorter gap, longest gap is keeped
		Assert.assertEquals(4000, series.getLongestGapEnd().getTime());
		Assert.assertEquals(2000, series.getLongestGap());
	}
}
