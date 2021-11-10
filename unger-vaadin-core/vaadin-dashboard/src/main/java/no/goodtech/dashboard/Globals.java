package no.goodtech.dashboard;

import com.vaadin.v7.shared.ui.combobox.FilteringMode;
import no.goodtech.dashboard.model.IDashboardSeriesManager;

public class Globals {

	private static IDashboardSeriesManager dashboardSeriesManager;
	private static volatile DashboardChangedAdaptor dashboardChangedAdaptor;
	private static volatile AxisChangedAdaptor axisChangedAdaptor;
	private static volatile String seriesIdFilteringMode;

	public Globals(final IDashboardSeriesManager dashboardSeriesManager,
				   DashboardChangedAdaptor dashboardChangedAdaptor, AxisChangedAdaptor axisChangedAdaptor,
				   String seriesIdFilteringMode) {

		Globals.dashboardSeriesManager = dashboardSeriesManager;
		Globals.dashboardChangedAdaptor = dashboardChangedAdaptor;
		Globals.axisChangedAdaptor = axisChangedAdaptor;
		Globals.seriesIdFilteringMode = seriesIdFilteringMode;
	}

	public static IDashboardSeriesManager getDashboardSeriesManager() {
		return dashboardSeriesManager;
	}

	public static DashboardChangedAdaptor getDashboardChangedAdaptor() {
		return dashboardChangedAdaptor;
	}

	public static AxisChangedAdaptor getAxisChangedAdaptor() {
		return axisChangedAdaptor;
	}

	public static FilteringMode getSeriesIdFilteringMode() {
		if (seriesIdFilteringMode == null) {
			return FilteringMode.STARTSWITH;
		} else {
			return FilteringMode.valueOf(seriesIdFilteringMode);
		}
	}
}
