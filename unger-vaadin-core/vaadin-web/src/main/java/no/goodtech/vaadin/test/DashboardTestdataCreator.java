package no.goodtech.vaadin.test;

import no.goodtech.dashboard.Globals;
import no.goodtech.dashboard.config.fetcher.FetcherConfig;
import no.goodtech.dashboard.config.fetcher.SimulatorFetcherConfig;
import no.goodtech.dashboard.config.ui.*;
import no.goodtech.vaadin.properties.model.PropertyClass;

/**
 * Creates test dashboards
 */
public class DashboardTestdataCreator {

	public void init() {
		createDashboard1();
		Globals.getDashboardSeriesManager().refresh();
	}

	private void createDashboard1() {
		final FetcherConfig simulatorFetcher1 = new SimulatorFetcherConfig("random1", 0.0, 100.0, 3).save();
		final FetcherConfig simulatorFetcher2 = new SimulatorFetcherConfig("random2", -10.0, 10.0, 3).save();

		DashboardConfig dashboardConfig = new DashboardConfig("DASH1");
		dashboardConfig.setRefreshIntervalInSeconds(5);
		dashboardConfig.setNumRows(2);

		SeriesConfig seriesConfig1 = new SeriesConfig("random1");
		seriesConfig1.setFetcherConfig(simulatorFetcher1);

		SeriesConfig seriesConfig2 = new SeriesConfig("random2");
		seriesConfig2.setFetcherConfig(simulatorFetcher2);

		final ChartConfig chartConfig1 = new ChartConfig(dashboardConfig);
		chartConfig1.addSeriesConfig(seriesConfig1);
		chartConfig1.setPeriodLengthInMinutes(5);
		chartConfig1.setRunningAverageSeries(seriesConfig1);
		dashboardConfig.addPanelConfig(chartConfig1);

		final ChartConfig chartConfig2 = new ChartConfig(dashboardConfig);
		chartConfig2.addSeriesConfig(seriesConfig2);
		chartConfig2.setPeriodLengthInMinutes(2);
		chartConfig2.setStartRow(2);
		chartConfig2.setEndRow(2);
		final AxisConfig axisConfig1 = new AxisConfig(chartConfig2);
		axisConfig1.setName("akse 1");
		axisConfig1.setMinValue(2.0);
		chartConfig2.addyAxis(axisConfig1);
		dashboardConfig.addPanelConfig(chartConfig2);
		seriesConfig2.setAxisConfig(axisConfig1);

		dashboardConfig = dashboardConfig.save();

		PropertyClass propertyClass = new PropertyClass("DashboardConfig");
		propertyClass.setDescription("DashboardConfig");
		propertyClass = propertyClass.save();
		System.out.print(String.format("Dashboard config id='%s', pk=%d created", dashboardConfig.getId(), dashboardConfig.getPk()));
	}
}
