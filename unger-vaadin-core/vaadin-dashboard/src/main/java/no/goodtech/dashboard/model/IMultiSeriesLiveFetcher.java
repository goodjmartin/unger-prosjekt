package no.goodtech.dashboard.model;

import no.goodtech.dashboard.config.ui.SeriesConfig;

import java.util.Date;

/**
 * This fetcher will fetch live values for multiple series in one query
 */
public interface IMultiSeriesLiveFetcher extends IDashboardFetcher  {

	/**
	 * This method should be called to obtain all sample points newer than provided start time
	 * @param seriesConfig the series to fetch data for
	 * @param fromTime timestamp for latest value per series
	 * @return all values newer than fromTime for this series
	 */
	SeriesInfo getSamplePoints(SeriesConfig seriesConfig, final Date fromTime);
}
