package no.goodtech.dashboard.ui.admin;

import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.StyleGenerator;
import no.goodtech.dashboard.config.fetcher.FetcherConfig;
import no.goodtech.dashboard.config.ui.AxisConfig;
import no.goodtech.dashboard.config.ui.SeriesConfig;
import no.goodtech.dashboard.ui.Texts;
import no.goodtech.vaadin.lists.IMessyGrid;
import no.goodtech.vaadin.lists.MessyGrid;

public class AxisConfigGrid extends MessyGrid<AxisConfig> {
	public AxisConfigGrid() {
		this(null);
	}

	public AxisConfigGrid(IMessyGrid<AxisConfig> actionListener) {
		super(actionListener);

		final StyleGenerator<AxisConfig> rightAlignStyleGenerator = item -> "v-align-right";

		addColumn(AxisConfig::getName).setCaption(Texts.get("axisConfig.table.name.caption"));
		addColumn(AxisConfig::getMinValue).setCaption(Texts.get("axisConfig.table.minValue.caption"));
		addColumn(AxisConfig::getMaxValue).setCaption(Texts.get("axisConfig.table.maxValue.caption"));
		addColumn(AxisConfig::getOpposite).setCaption(Texts.get("axisConfig.table.opposite.caption"));
		addColumn(AxisConfig::getLineWidth).setCaption(Texts.get("axisConfig.table.lineWith.caption")).setHidable(true).setHidable(true).setHidden(true);
		addColumn(AxisConfig::getTickWidth).setCaption(Texts.get("axisConfig.table.tickWidth.caption")).setHidable(true).setHidden(true);
		addColumn(AxisConfig::getTickInterval).setCaption(Texts.get("axisConfig.table.tickInterval.caption")).setHidable(true).setHidden(true);
		addColumn(AxisConfig::getGridLineWidth).setCaption(Texts.get("axisConfig.table.gridLineWidth.caption")).setHidable(true).setHidden(true);
		addColumn(AxisConfig::isHideAlternateGrid).setCaption(Texts.get("axisConfig.table.hideAlternateGrid.caption")).setHidable(true).setHidden(true);
		setCaption(Texts.get("axisConfig.table.caption"));
		setHeightByRowsWithMaxLimit(2);
	}
}
