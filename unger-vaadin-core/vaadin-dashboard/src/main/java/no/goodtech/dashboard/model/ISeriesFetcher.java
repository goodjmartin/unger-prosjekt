package no.goodtech.dashboard.model;

import java.util.Date;

/**
 * This interface should be implemented by all series fetchers
 * TODO: Rename to ISingleSeriesLiveFetcher
 */
public interface ISeriesFetcher extends IDashboardFetcher {

	/**
	 * This method should be called to obtain all sample points newer than provided start time
	 * @param startTime Start time for sample point retrieval
	 * @return The series data
	 */
	SeriesInfo getSamplePoints(final Date startTime);

}
