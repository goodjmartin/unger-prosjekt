package no.goodtech.dashboard.ui.admin;

import com.vaadin.ui.Notification;
import no.goodtech.dashboard.config.ui.ChartConfig;
import no.goodtech.dashboard.config.ui.DashboardConfig;
import no.goodtech.dashboard.config.ui.PanelConfig;
import no.goodtech.vaadin.main.SimpleInputBox;
import no.goodtech.vaadin.ui.SimpleCrudGridInMemoryPanel;

public class PanelConfigCrudPanel extends SimpleCrudGridInMemoryPanel<PanelConfig> {

	private final DashboardConfig dashboardConfig;
	private final DashboardConfigFormVaadin7.CoordinatesProvider coordinatesProvider;

	public PanelConfigCrudPanel(DashboardConfig dashboardConfig, DashboardConfigFormVaadin7.CoordinatesProvider coordinatesProvider) {
		super(null, dashboardConfig.getPanels(), new PanelConfigGrid(null), false);
		this.dashboardConfig = dashboardConfig;
		this.coordinatesProvider = coordinatesProvider;
		refresh();
	}

	@Override
	protected SimpleInputBox.IinputBoxContent createDetailForm(PanelConfig entity, boolean isNew) {
		if (entity instanceof ChartConfig) {
			final ChartConfig chartConfig = (ChartConfig) entity; //TODO: Support more panel types
			return new PanelConfigForm(chartConfig, coordinatesProvider);
		} else {
			Notification.show("Panel-type '" + entity.getClass().getSimpleName() + "' støttes ikke ennå");
			return null;
		}
	}

	@Override
	protected PanelConfig create() {
		//TODO: Decide panel type
		return new ChartConfig(dashboardConfig);
	}

//	@Override
//	public List<PanelConfig> getItems() {
//		return new ArrayList<>(dashboardConfig.getPanels());
//	}
//
//	@Override
//	protected void add(PanelConfig item) {
//		dashboardConfig.addPanelConfig(item);
//	}
//
//	@Override
//	protected void remove(PanelConfig item) {
//		dashboardConfig.getPanels().remove(item);
//	}
}
