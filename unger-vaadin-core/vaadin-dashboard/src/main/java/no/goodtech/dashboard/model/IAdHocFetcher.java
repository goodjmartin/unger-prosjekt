package no.goodtech.dashboard.model;

import no.goodtech.admin.tabs.report.IReportParameter;
import no.goodtech.dashboard.config.ui.SeriesConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Fetchers of this type can run ad hoc queries
 */
public interface IAdHocFetcher extends IDashboardFetcher {

	Map<SeriesConfig, SeriesInfo> getData(List<SeriesConfig> seriesConfigs, Map<String, IReportParameter> parameters);

	/**
	 * If the fetcher needs a hint of where to fetch data from, you will get a list of the sources here
	 * @return the list of sources this fetcher supports multiple sources
	 * Empty list if this fetcher have only one source
	 */
//	default List<ISourceType> listSourceTypes() {
//		return new ArrayList<>();
//	};

}
