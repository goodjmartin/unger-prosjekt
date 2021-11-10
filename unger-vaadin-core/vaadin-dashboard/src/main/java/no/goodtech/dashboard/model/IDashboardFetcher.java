package no.goodtech.dashboard.model;

import java.util.HashMap;
import java.util.Map;

/**
 * All dashboard data sources implements this
 */
public interface IDashboardFetcher extends Runnable {

	/**
	 * Implement this if you want to provide a list of which series your fetcher can provide data for.
	 *
	 * @return a map of ID's of the series that this fetcher can provide. Key = ID, value = user-friendly name.
	 * Empty map if it cannot provide this metadata
	 */
	default Map<String, String> listSeriesIds() {
		return new HashMap<>();
	}
}
