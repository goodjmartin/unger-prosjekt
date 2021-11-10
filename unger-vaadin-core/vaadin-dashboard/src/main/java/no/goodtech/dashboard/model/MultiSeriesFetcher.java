package no.goodtech.dashboard.model;

import no.goodtech.dashboard.config.ui.SeriesConfig;
import no.goodtech.dashboard.config.ui.SeriesConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * A dashboard data provider that can fetch data for multiple series in one query
 */
public abstract class MultiSeriesFetcher implements IMultiSeriesLiveFetcher {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected final String id;
	private final MultiSeriesCache cache;
	private final HashSet<SeriesConfig> seriesConfigs = new LinkedHashSet<>();

	/**
	 * Create fetcher
	 * @param id unique name of fetcher
	 * @param maxAge max age of cache in seconds
	 * @param fullFetch true = fetch data for whole cache period every time, false = fetch incrementally
	 */
	protected MultiSeriesFetcher(String id, boolean fullFetch, long maxAge, SeriesKeyProvider keyProvider) {
		this.id = id;
		cache = new MultiSeriesCache(fullFetch, maxAge, keyProvider);
	}

	protected MultiSeriesFetcher(String id, boolean fullFetch, long maxAge) {
		this(id, fullFetch, maxAge, new SeriesKeyProvider() {});
	}

	@Override
	public SeriesInfo getSamplePoints(SeriesConfig seriesConfig, Date fromTime) {
		addSeriesConfig(seriesConfig);
		return cache.getSamplePoints(seriesConfig, fromTime);
	}

	/**
	 * Fetch data and fill cache. Remove data older than max age of cache
	 */
	@Override
	public void run() {
		try {
			final Date startTime = cache.getStartTimeForNextFetch();

			Map<SeriesConfig, List<SampleDTO>> valuesPerSeries = fetchNewSamplePoints(startTime);
			for (SeriesConfig seriesConfig : valuesPerSeries.keySet()) {
				addSeriesConfig(seriesConfig);
			}
			cache.addValues(removeFreakPoints(valuesPerSeries, startTime));
//			logger.debug(String.format("Fetched: id=%25s, new=%3d, cache=%3d" + ", time(ms)=%4d", id, valuesPerSeries.size(), sampleSeriesCache.size(), (System.currentTimeMillis() - now.getTime())));
		} catch (Throwable t) {
			logger.error(String.format("Unable to fetch data: id=%s, message=%s", id, t.getMessage()), t);
		}
	}

	private Map<SeriesConfig, List<SampleDTO>> removeFreakPoints(Map<SeriesConfig, List<SampleDTO>> valuesPerSeries, Date fromTime) {
		// Delete freak sample points (older than 'lastSampleTime'). Looks like we get some older points from Historian.
		if (!cache.isFullFetch()) {
			Date nearFuture = new Date(CurrentTimeProvider.getTime() + 10000);
			for (Map.Entry<SeriesConfig, List<SampleDTO>> entry : valuesPerSeries.entrySet()) {
				List<SampleDTO> newValues = new ArrayList<>(entry.getValue());
				while (true) {
					SampleDTO sampleDTO = null;
					if (newValues.size() > 0) {
						sampleDTO = newValues.get(0);
					}
					if ((sampleDTO != null) && (sampleDTO.getCreated().before(fromTime) || sampleDTO.getCreated().after(nearFuture))) {
						logger.debug("Ignoring data point outside timeframe: id=" + id + ", sampleDTO=" + sampleDTO.getCreated() + ", lastSampleTime=" + fromTime);
						newValues.remove(sampleDTO);
					} else {
						break;
					}
				}
			}
		}
		return valuesPerSeries;
	}


	/**
	 * To be implemented by all concrete fetchers
	 *
	 * @param startTime Start time for fetching
	 *
	 * @return The list of sample points matching the specified start / end time
	 */
	public abstract Map<SeriesConfig, List<SampleDTO>> fetchNewSamplePoints(final Date startTime);

	public SeriesConfigs getSeriesConfigs() {
		synchronized (seriesConfigs) {
			return new SeriesConfigs(seriesConfigs);
		}
	}

	protected void addSeriesConfig(SeriesConfig seriesConfig) {
		synchronized (seriesConfigs) {
			seriesConfigs.add(seriesConfig);
		}
	}
}
