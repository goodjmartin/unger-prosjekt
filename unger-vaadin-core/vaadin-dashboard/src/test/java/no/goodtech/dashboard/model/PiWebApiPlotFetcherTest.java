package no.goodtech.dashboard.model;

import java.util.Date;

import javax.management.timer.Timer;

import org.junit.Assert;
import org.junit.Test;

public class PiWebApiPlotFetcherTest {

	
	@Test
	public void getIntervalsPerSecond() {
		Assert.assertEquals(1, createFetcher(100, 100).getIntervalsPerSecond(), 0.000001);
		Assert.assertEquals(10, createFetcher(1, 10).getIntervalsPerSecond(), 0.000001);
		Assert.assertEquals(0.25, createFetcher(3600, 900).getIntervalsPerSecond(), 0.000001);
	}
	
	@Test
	public void computeIntervals() {
		Assert.assertEquals(900, createFetcher(3600, 900).computeIntervals(last(Timer.ONE_HOUR)), 1.0);
		Assert.assertEquals(15, createFetcher(3600, 900).computeIntervals(last(Timer.ONE_MINUTE)), 1.0);
		Assert.assertEquals(90, createFetcher(864000, 900).computeIntervals(last(Timer.ONE_DAY)), 1.0);
	}
	
	private Date last(long millis) {
		return new Date(System.currentTimeMillis() - millis);
	}
	
	PiWebApiPlotFetcher createFetcher(long cacheRetentionInterval, Integer intervals) {
		return new PiWebApiPlotFetcher(null, false, cacheRetentionInterval, null, null, intervals);
	}

}
