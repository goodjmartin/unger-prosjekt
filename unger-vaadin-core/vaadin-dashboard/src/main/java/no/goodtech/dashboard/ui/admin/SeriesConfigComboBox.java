package no.goodtech.dashboard.ui.admin;

import no.goodtech.dashboard.config.ui.SeriesConfig;
import no.goodtech.dashboard.config.ui.ChartConfig;
import no.goodtech.vaadin.lists.v7.MessyComboBox;

import java.util.ArrayList;

public class SeriesConfigComboBox extends MessyComboBox<SeriesConfig> {

	private final ChartConfig chartConfig;

	public SeriesConfigComboBox(String caption, ChartConfig chartConfig) {
		super(caption);
		this.chartConfig = chartConfig;
	}

	@Override
	protected String getId(SeriesConfig item) {
		return String.valueOf(item.getPk());
	}

	@Override
	protected String getName(SeriesConfig item) {
		return item.getName();
	}

	public void refresh() {
		super.refresh(new ArrayList<>(chartConfig.getSeriesConfigs()));
	}
}
