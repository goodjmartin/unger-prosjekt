package no.goodtech.dashboard.model;

import no.goodtech.admin.tabs.report.IReportParameter;
import no.goodtech.dashboard.config.ui.PanelConfig;
import no.goodtech.dashboard.config.ui.SeriesConfig;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IDashboardSeriesManager {

	/**
	 * Clear cache, read configuration and initialize fetchers
	 */
    void refresh();

	/**
	 * Get values from cache
	 * @param seriesConfig the series to fetch data for
	 * @param startTime timestamp for latest value
	 * @return all values newer than startTime
	 */
    SeriesInfo getSamplePoints(final SeriesConfig seriesConfig, final Date startTime);

	/**
	 * @return all series for given panel, including auto-generated series
	 */
	Set<SeriesConfig> getSeriesConfigs(PanelConfig panelConfig);

	/**
	 * Fetch all data for multiple series based on given parameters
	 * @param seriesConfigs the series we need data for
	 * @param parameters query parameters, e.g. orderNumber -> 4242
	 * @return data for all series
	 */
	Map<SeriesConfig, SeriesInfo> getData(List<SeriesConfig> seriesConfigs, Map<String, IReportParameter> parameters);

}
