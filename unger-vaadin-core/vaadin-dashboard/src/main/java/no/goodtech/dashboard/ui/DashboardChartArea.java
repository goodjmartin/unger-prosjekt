package no.goodtech.dashboard.ui;

import com.vaadin.event.UIEvents.PollEvent;
import com.vaadin.ui.*;
import no.goodtech.admin.tabs.report.IReportParameter;
import no.goodtech.dashboard.Globals;
import no.goodtech.dashboard.config.ui.ChartConfig;
import no.goodtech.dashboard.config.ui.DashboardConfig;
import no.goodtech.dashboard.config.ui.PanelConfig;
import no.goodtech.dashboard.config.ui.SeriesConfig;
import no.goodtech.dashboard.model.SeriesInfo;
import no.goodtech.vaadin.Poller;
import no.goodtech.vaadin.Poller.IPollablePanel;

import javax.management.timer.Timer;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains the panel (chart) part on the dashboard
 */
public class DashboardChartArea extends VerticalLayout implements IPollablePanel {
	private final Map<PanelConfig, IDashboardSubPanel> panels = new HashMap<>();

	public DashboardChartArea(DashboardConfig dashboardConfig, boolean live, Map<String, IReportParameter> parameters) {
		final GridLayout gridLayout = new GridLayout(dashboardConfig.getNumColumns(), dashboardConfig.getNumRows());
		for (PanelConfig panelConfig : dashboardConfig.getPanels()) {
			Component panel = null;
			if (panelConfig.isCoordinatesValid()) {
				if (panelConfig instanceof ChartConfig) {
					SampleChart chart = new SampleChart((ChartConfig) panelConfig, live); //TODO: Support more panel types
					panels.put(panelConfig, chart); // remember sample chart to be able to refresh chart later
					panel = chart;
				} else {
					panel = new Label("Unsupported panel type: " + panelConfig.getTitle());
				}
				gridLayout.addComponent(panel, panelConfig.getStartColumn() - 1, panelConfig.getStartRow() - 1,
						panelConfig.getEndColumn() - 1, panelConfig.getEndRow() - 1);
			} else {
				//TODO: Translate
				Notification.show(String.format("Coordinates for panel %s is outside grid, so this panel is ignored. Panel coordinates are: %s. Grid coordinates are %s",
						panelConfig.getTitle(), panelConfig.formatCoordinates(), panelConfig.getDashboardConfig().formatCoordinates()), Notification.Type.WARNING_MESSAGE);
			}
		}
		gridLayout.setSizeFull();
		addComponent(gridLayout);
		setSizeFull();
		if (live) {
			Integer refreshIntervalInSeconds = dashboardConfig.getRefreshIntervalInSeconds();
			if (refreshIntervalInSeconds == null) {
				refreshIntervalInSeconds = 60;
				Notification.show(Texts.get("dashboard.live.warning.missingRefreshInterval"), Notification.Type.TRAY_NOTIFICATION);
			}
			Poller.poll(this, new Long(refreshIntervalInSeconds * Timer.ONE_SECOND).intValue());
			poll(null); //hack to force panel to update immediately
		} else {
			refresh(dashboardConfig, parameters);
		}
		setMargin(false);
		setSpacing(false);
	}
	
	public void poll(PollEvent event) {
		for (IDashboardSubPanel panel : panels.values())
			panel.refresh();
	}

	/**
	 * Fetch all data for dashboard at once and refresh dashboard
	 * @param dashboardConfig configuration for dashboard
	 * @param parameters query parameters
	 */
	public void refresh(DashboardConfig dashboardConfig, Map<String, IReportParameter> parameters) {
		final Map<SeriesConfig, SeriesInfo> dashboardData = Globals.getDashboardSeriesManager().getData(dashboardConfig.getSeriesConfigs(), parameters);
		//find which series belongs to each panel and update the panels
		for (PanelConfig panelConfig : dashboardConfig.getPanels()) {
			Map<SeriesConfig, SeriesInfo> dataForPanel = new HashMap<>();
			if (panelConfig instanceof ChartConfig) {
				ChartConfig chartConfig = (ChartConfig) panelConfig;
				for (Map.Entry<SeriesConfig, SeriesInfo> entry : dashboardData.entrySet()) {
					//we use the seriesConfigs from the result in case a fetcher have generated any series that was not in the configuration
					final SeriesConfig seriesConfig = entry.getKey();
					if (seriesConfig.getPanelConfig().equals(chartConfig)) {
						dataForPanel.put(seriesConfig, entry.getValue());
					}
				}
			}
			final IDashboardSubPanel panel = panels.get(panelConfig);
			if (panel != null) {
				panel.refresh(dataForPanel);
			}
		}
	}
}
