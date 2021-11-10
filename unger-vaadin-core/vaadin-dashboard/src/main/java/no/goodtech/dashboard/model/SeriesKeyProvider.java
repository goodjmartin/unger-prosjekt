package no.goodtech.dashboard.model;

import no.goodtech.dashboard.config.ui.SeriesConfig;

public interface SeriesKeyProvider {

	default Object getKey(SeriesConfig seriesConfig) {
		return seriesConfig.getId();
	}
}
