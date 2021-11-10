package no.goodtech.dashboard.ui.admin;

import no.goodtech.dashboard.config.ui.AxisConfig;
import no.goodtech.dashboard.config.ui.ChartConfig;
import no.goodtech.vaadin.main.SimpleInputBox;
import no.goodtech.vaadin.ui.SimpleCrudGridInMemoryPanel;

import java.util.LinkedHashSet;
import java.util.Set;

public class AxisConfigCrudPanel extends SimpleCrudGridInMemoryPanel<AxisConfig> {

	private final ChartConfig chartConfig;

	public AxisConfigCrudPanel(ChartConfig chartConfig) {
		super(null, chartConfig.getyAxes(), new AxisConfigGrid(), false);
		this.chartConfig = chartConfig;
		refresh();
	}

	@Override
	protected SimpleInputBox.IinputBoxContent createDetailForm(AxisConfig axisConfig, boolean isNew) {
		return new AxisConfigForm(axisConfig, chartConfig);
	}

	@Override
	protected AxisConfig create() {
		return new AxisConfig(chartConfig);
	}

	@Override
	protected Set<AxisConfig> clone(Set<AxisConfig> items) {
		Set<AxisConfig> result = new LinkedHashSet<>();
		for (AxisConfig axisConfig : items) {
			AxisConfig clone = axisConfig.clone();
			result.add(clone);
		}
		return result;
	}
}
