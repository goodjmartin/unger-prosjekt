package no.goodtech.dashboard.ui.admin;

import no.goodtech.dashboard.config.ui.AxisConfig;
import no.goodtech.dashboard.config.ui.ChartConfig;
import no.goodtech.dashboard.config.ui.SeriesConfig;
import no.goodtech.vaadin.chart.VaadinColorProvider;
import no.goodtech.vaadin.main.SimpleInputBox;
import no.goodtech.vaadin.ui.ListProvider;
import no.goodtech.vaadin.ui.SimpleCrudGridInMemoryPanel;

import java.util.LinkedHashSet;
import java.util.Set;

public class SeriesConfigCrudPanel extends SimpleCrudGridInMemoryPanel<SeriesConfig> {

	public interface IPalette {
		String getNextColor();
	}

	private final ListProvider<AxisConfig> axes;

	public SeriesConfigCrudPanel(ChartConfig chartConfig, ListProvider<AxisConfig> axes) {
		super(null, chartConfig.getSeriesConfigs(), new SeriesConfigGrid(), false);
		this.axes = axes;
		refresh();
	}

	@Override
	protected SimpleInputBox.IinputBoxContent createDetailForm(SeriesConfig entity, boolean isNew) {
		return new SeriesConfigForm(entity, axes, new IPalette() {
			@Override
			public String getNextColor() {
				final int numSeries = items.size();
				VaadinColorProvider colorProvider = new VaadinColorProvider();
				String color = null;
				for (int i = 0; i <= numSeries; i++) {
					color = colorProvider.getNextColorAsHex();
				}
				return color;
			}
		});
	}

	@Override
	protected SeriesConfig create() {
		return new SeriesConfig();
	}

	@Override
	protected Set<SeriesConfig> clone(Set<SeriesConfig> items) {
		Set<SeriesConfig> result = new LinkedHashSet<>();
		for (SeriesConfig seriesConfig : items) {
			SeriesConfig clone = seriesConfig.clone();
			result.add(clone);
		}
		return result;
	}
}
