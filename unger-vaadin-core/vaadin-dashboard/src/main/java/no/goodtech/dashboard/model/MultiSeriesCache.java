package no.goodtech.dashboard.model;

import no.goodtech.dashboard.config.ui.SeriesConfig;

import javax.management.timer.Timer;
import java.util.*;

/**
 * Cache for "live" dashboards for a set of series.
 * The series are mapped by ID so multiple charts can share data if they show the same series
 */
public class MultiSeriesCache {

	private final Map<Object, List<SampleDTO>> cache = new LinkedHashMap<>();
	private final boolean fullFetch;
	private final long maxAge; //in seconds
	private final SeriesKeyProvider keyProvider;

	public MultiSeriesCache(boolean fullFetch, long maxAge, SeriesKeyProvider keyProvider) {
		this.fullFetch = fullFetch;
		this.maxAge = maxAge;
		this.keyProvider = keyProvider;
	}

	/**
	 * Get values from cache
	 * @param seriesConfig the series to fetch data for
	 * @param fromTime timestamp for latest value
	 * @return all values newer than fromTime
	 */
	public SeriesInfo getSamplePoints(SeriesConfig seriesConfig,  Date fromTime) {
		List<SampleDTO> valuesToReturn = new ArrayList<>();
		synchronized (cache) {
			List<SampleDTO> valuesInCache = cache.get(keyProvider.getKey(seriesConfig));
			if (valuesInCache != null) {
				if (fromTime != null && !fullFetch) {
					for (SampleDTO sampleDTO : valuesInCache) {
						// Copy sample points arrived after previous call
						if (sampleDTO.getCreated().after(fromTime)) {
							valuesToReturn.add(sampleDTO);
						}
					}
				} else {
					valuesToReturn.addAll(valuesInCache);
				}
			}
		}
		return new SeriesInfo(fullFetch, valuesToReturn);
	}

	/**
	 * TODO: Maybe refactor with {@link #getSamplePoints(SeriesConfig, Date)} if this flexibility is not needed
	 * Get values from cache
	 * @param fromTimePerSeries timestamp for latest value per series
	 * @return all values newer than fromTime (per series)
	 */
//	private Map<SeriesConfig, SeriesInfo> getSamplePoints(Map<SeriesConfig, Date> fromTimePerSeries) {
//
//		Map<SeriesConfig, SeriesInfo> results = new LinkedHashMap<>();
//
//		synchronized (cache) {
//			for (Map.Entry<SeriesConfig, List<SampleDTO>> entry : cache.entrySet()) {
//				List<SampleDTO> valuesInCache = entry.getValue();
//				final SeriesConfig seriesConfig = entry.getKey();
//
//				List<SampleDTO> valuesToReturn = new ArrayList<>();
//				final Date fromTime = fromTimePerSeries.get(seriesConfig);
//
//				if ((fromTime != null) && (!fullFetch)) {
//					for (SampleDTO sampleDTO : valuesInCache) {
//						// Copy sample points arrived after previous call
//						if (sampleDTO.getCreated().after(fromTime)) {
//							valuesToReturn.add(sampleDTO);
//						}
//					}
//				} else {
//					valuesToReturn.addAll(valuesInCache);
//				}
//				results.put(seriesConfig, new SeriesInfo(fullFetch, valuesToReturn));
//			}
//		}
//		return results;
//	}

	/**
	 * Add values to cache
	 */
	public void addValues(Map<SeriesConfig, List<SampleDTO>> valuesPerSeries) {
		synchronized (cache) {
			if (fullFetch) {
 				clear();
			} else {
				removeOldData();
			}
//				final Map<SeriesConfig, Date> latestTimePerSeries = getLatestTimePerSeries();
			for (Map.Entry<SeriesConfig, List<SampleDTO>> entry : valuesPerSeries.entrySet()) {
				List<SampleDTO> newValues = entry.getValue();
				final SeriesConfig seriesConfig = entry.getKey();
				List<SampleDTO> valuesInCache = cache.get(keyProvider.getKey(seriesConfig));
//					for (SampleDTO newValue : newValues) {
//						final Date cacheEndTime = latestTimePerSeries.get(seriesConfig);
//						if (newValue.getCreated().after(cacheEndTime)) {
//							valuesInCache.add(newValue);
//						}
//					}
				if (valuesInCache == null) {
					valuesInCache = new ArrayList<>();
				}
				valuesInCache.addAll(newValues);
				cache.put(keyProvider.getKey(seriesConfig), valuesInCache);
			}
		}
	}

	/**
	 * Checks the time of the last point for each series and return the earliest of them
	 * @return the earliest time of last point for each series
	 * TODO: It might be a problem that empty series in cache force the fetcher to fetch data that it have fetched earlier
	 */
	public Date getStartTimeForNextFetch() {
		if (fullFetch) {
			return getStartTime(); //start time of cache
		}
		long earliestTime = getStartTime().getTime();

		synchronized (cache) {
			for (List<SampleDTO> valuesForSeries : cache.values()) {
				if (valuesForSeries != null && valuesForSeries.size() > 0) {
					SampleDTO lastValue = valuesForSeries.get(valuesForSeries.size() - 1);
					final long pointTime = lastValue.getCreated().getTime();
					if (pointTime < earliestTime) {
						earliestTime = pointTime;
					}
				}
			}
		}
		return new Date(earliestTime);
	}

	private Date getStartTime() {
		return new Date(CurrentTimeProvider.getTime() - maxAge * Timer.ONE_SECOND);
	}

	/**
	 * Remove all values from cache
	 */
	public void clear() {
		synchronized (cache) {
			cache.clear();
		}
	}

	/**
	 * Remove data older than max age
	 */
	public void removeOldData() {
		final Date cacheStartTime = getStartTime();
		synchronized (cache) {
			for (List<SampleDTO> valuesForSeries : cache.values()) {
				while (true) {
					SampleDTO sampleDTO = (valuesForSeries.size() > 0) ? valuesForSeries.get(0) : null;
					if ((sampleDTO != null) && (sampleDTO.getCreated().before(cacheStartTime))) {
						valuesForSeries.remove(sampleDTO);
					} else {
						break;
					}
				}
			}
		}
	}

	public long getMaxAge() {
		return maxAge;
	}

	public boolean isFullFetch() {
		return fullFetch;
	}
}
