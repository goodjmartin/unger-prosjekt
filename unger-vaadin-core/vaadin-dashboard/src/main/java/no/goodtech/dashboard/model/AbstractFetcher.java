package no.goodtech.dashboard.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class AbstractFetcher implements ISeriesFetcher, Runnable {

	private static final Logger logger = LoggerFactory.getLogger(AbstractFetcher.class);
	private final String id;
	private final long cacheRetentionInterval;
	private final List<SampleDTO> sampleSeriesCache = new ArrayList<>();
	private final boolean fullFetch;
	private volatile Date lastSampleTime = null;

	protected AbstractFetcher(final String id, final boolean fullFetch, final long cacheRetentionInterval) {
		this.id = id;
		this.fullFetch = fullFetch;
		this.cacheRetentionInterval = cacheRetentionInterval;
	}

	public SeriesInfo getSamplePoints(final Date startTime) {
		List<SampleDTO> newSampleDTOs = new ArrayList<>();

		Double runningAverage = null;

		synchronized (sampleSeriesCache) {
			if ((startTime != null) && (!fullFetch)) {
				for (SampleDTO sampleDTO : sampleSeriesCache) {
					// Copy sample points arrived after previous call
					if (sampleDTO.getCreated().after(startTime)) {
						newSampleDTOs.add(sampleDTO);
					}
				}
			} else {
				newSampleDTOs.addAll(sampleSeriesCache);
			}
		}
		return new SeriesInfo(fullFetch, newSampleDTOs);
	}

	@Override
	public void run() {

		try {
			// Determine start / end time for query
			Date now = new Date(System.currentTimeMillis());
			Date nearFuture = new Date(now.getTime() + 10000); 
			Date cacheStartTime = new Date(now.getTime() - cacheRetentionInterval * 1000);
			Date loopStartTime = (lastSampleTime == null) ? cacheStartTime : lastSampleTime;

			// Fetch new sample points
			List<SampleDTO> newSampleDTOs = fetchNewSamplePoints(fullFetch ? cacheStartTime : loopStartTime);

			if (newSampleDTOs != null) {
				// Delete freak sample points (older than 'lastSampleTime'). Looks like we get some older points from Historian.
				if (!fullFetch && (lastSampleTime != null)) {
					boolean freakSampleDetected = false;
					while (true) {
						SampleDTO sampleDTO = (newSampleDTOs.size() > 0) ? newSampleDTOs.get(0) : null;

						if ((sampleDTO != null) && (sampleDTO.getCreated().before(lastSampleTime) || sampleDTO.getCreated().after(nearFuture))) {
							freakSampleDetected = true;
							newSampleDTOs.remove(sampleDTO);
						} else {
							break;
						}
					}
					if (freakSampleDetected)
						logger.warn("At least one freak sample point encountered and removed: id=" + id + ", lastSampleTime=" + lastSampleTime + ", cacheStartTime=" + cacheStartTime + ", loopStartTime=" + loopStartTime);
				}

				// Remember last end time for incremental fetch
				if (newSampleDTOs.size() > 0) {
					lastSampleTime = newSampleDTOs.get(newSampleDTOs.size() - 1).getCreated();
				}

				//noinspection SynchronizationOnLocalVariableOrMethodParameter
				synchronized (sampleSeriesCache) {
					if (fullFetch) {
						// Clear old sample points
						sampleSeriesCache.clear();
					} else {
						// Remove sample points older than cacheStartTime
						while (true) {
							SampleDTO sampleDTO = (sampleSeriesCache.size() > 0) ? sampleSeriesCache.get(0) : null;

							if ((sampleDTO != null) && (sampleDTO.getCreated().before(cacheStartTime))) {
								sampleSeriesCache.remove(sampleDTO);
							} else {
								break;
							}
						}
					}

					// Add new sample points to current sample points
					sampleSeriesCache.addAll(newSampleDTOs);

					logger.debug(String.format("Fetched: id=%25s, new=%3d, cache=%3d" + ", time(ms)=%4d", id, newSampleDTOs.size(), sampleSeriesCache.size(), (System.currentTimeMillis() - now.getTime())));
				}
			}
		} catch (Throwable t) {
			logger.error(String.format("Unable to fetch data: id=%s, message=%s", id, t.getMessage()));
		}
	}

	/**
	 * Abstract method to be implemented by all concrete fetchers
	 *
	 * @param startTime Start time for fetching
	 *
	 * @return The list of sample points matching the specified start / end time
	 */
	public abstract List<SampleDTO> fetchNewSamplePoints(final Date startTime);
}
