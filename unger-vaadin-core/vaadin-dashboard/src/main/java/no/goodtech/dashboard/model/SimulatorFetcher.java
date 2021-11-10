package no.goodtech.dashboard.model;

import no.goodtech.admin.tabs.report.IReportParameter;
import no.goodtech.dashboard.config.ui.SeriesConfig;

import javax.management.timer.Timer;
import java.util.*;

/**
 * Generates random data for dashboard testing
 */
public class SimulatorFetcher extends MultiSeriesFetcher implements IAdHocFetcher {

	private final Double minValue; 
	private final Double maxValue;
	
	protected SimulatorFetcher(String id, boolean fullFetch, long cacheRetentionInterval) {
		this(id, null, null, fullFetch, cacheRetentionInterval);
	}

	@Override
	public Map<SeriesConfig, List<SampleDTO>> fetchNewSamplePoints(Date startTime) {
		Map<SeriesConfig, List<SampleDTO>> result = new LinkedHashMap<>();
		for (SeriesConfig seriesConfig : getSeriesConfigs()) {
			result.put(seriesConfig, createPoint(new Date()));
		}
		return result;
	}

	public SimulatorFetcher(String id, Double minValue, Double maxValue, boolean fullFetch, long cacheRetentionInterval) {
		super(id, fullFetch, cacheRetentionInterval);
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	private List<SampleDTO> createPoint(Date date) {
		//a sine-curve with length of 60s
//		int sec = Calendar.getInstance().get(Calendar.SECOND);
//		double value = Math.sin(2 * Math.PI * sec / 60);
		final double randomFactor = Math.random();
		double range = 100;
		double value;
		if (maxValue != null)
			range = maxValue;
		value = range * randomFactor;

		return Arrays.asList(new SampleDTO(date, value));
	}

	@Override
	public Map<SeriesConfig, SeriesInfo> getData(List<SeriesConfig> seriesConfigs, Map<String, IReportParameter> parameters) {
		Map<SeriesConfig, SeriesInfo> results = new HashMap<>();
		for (SeriesConfig seriesConfig : seriesConfigs) {
			List<SampleDTO> points = new ArrayList<>();
			for (int i = 60; i > 0; i--) {
				points.addAll(createPoint(new Date(System.currentTimeMillis() - i * Timer.ONE_SECOND)));
			}
			final SeriesInfo seriesInfo = new SeriesInfo(true, points);
			results.put(seriesConfig, seriesInfo);
		}
		return results;
	}
}
