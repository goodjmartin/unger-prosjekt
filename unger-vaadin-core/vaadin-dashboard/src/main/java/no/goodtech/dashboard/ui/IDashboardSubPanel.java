package no.goodtech.dashboard.ui;

import no.goodtech.dashboard.config.ui.SeriesConfig;
import no.goodtech.dashboard.model.SeriesInfo;

import java.util.Map;

public interface IDashboardSubPanel {

	/**
	 * Ask panel to refresh itself
	 */
	void refresh();

	/**
	 * Refresh panel with given data
	 * @param data
	 */
	void refresh(Map<SeriesConfig, SeriesInfo> data);
}
