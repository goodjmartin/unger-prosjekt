package no.goodtech.dashboard.ui.admin;

import com.vaadin.ui.StyleGenerator;
import no.goodtech.dashboard.config.ui.SeriesConfig;
import no.goodtech.dashboard.ui.Texts;
import no.goodtech.vaadin.lists.IMessyGrid;
import no.goodtech.vaadin.lists.MessyGrid;

import java.util.List;

public class SeriesConfigGrid extends MessyGrid<SeriesConfig> {

	public SeriesConfigGrid() {
		this(null);
	}

	public SeriesConfigGrid(IMessyGrid<SeriesConfig> actionListener) {
		super(actionListener);

		final StyleGenerator<SeriesConfig> rightAlignStyleGenerator = item -> "v-align-right";

		addColumn(SeriesConfig::getId).setCaption(Texts.get("seriesConfig.table.id.caption"));
		addColumn(SeriesConfig::getName).setCaption(Texts.get("seriesConfig.table.name.caption"));

		addColumn(column -> {
			if (column.getFetcherConfig() != null) {
				return column.getFetcherConfig().getId();
			}
			return null;
		}).setCaption(Texts.get("seriesConfig.table.fetcherConfig.caption"));

		addColumn(column -> {
			if (column.getAxisConfig() != null) {
				return column.getAxisConfig().getName();
			}
			return null;
		}).setCaption(Texts.get("seriesConfig.table.axisConfig.caption"));

		addColumn(SeriesConfig::getColor).setCaption(Texts.get("seriesConfig.table.color.caption"));
		addColumn(SeriesConfig::isShowMarker).setCaption(Texts.get("seriesConfig.table.showMarker.caption"));

		setCaption(Texts.get("seriesConfig.table.caption"));
		setHeightByRowsWithMaxLimit(6);
	}

	@Override
	public void refresh(List<? extends SeriesConfig> objects) {
		super.refresh(objects);
	}
}
