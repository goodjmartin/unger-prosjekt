package no.goodtech.dashboard.ui;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import no.goodtech.admin.tabs.report.IReportParameter;
import no.goodtech.dashboard.Globals;
import no.goodtech.dashboard.config.ui.DashboardConfig;
import no.goodtech.dashboard.config.ui.DashboardConfigFinder;
import no.goodtech.push.ISubscriber;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Dashboard content based on configuration in database, see {@link DashboardConfig}.
 * May be used in a standalone view, see {@link DashboardView} or embedded in a popup
 */
public class DashboardPanel extends VerticalLayout {

	private static final Logger LOGGER = LoggerFactory.getLogger(DashboardPanel.class);
	protected Long pk;
	protected String id;
	protected boolean live;
	private final Map<String, IReportParameter> parameters;

	public DashboardPanel() {
		parameters = new HashMap<>();
		setSizeFull();
		setSpacing(false);
	}

	/**
	 * @param pk primary key of dashboard configuration
	 * @param live true = dashboard will refresh itself based on configured interval,
	 *                false = dashboard will fetch all data on startup
	 * @see DashboardConfig#getRefreshIntervalInSeconds()
	 */
	public DashboardPanel(Long pk, boolean live) {
		this(pk, null, live, new HashMap<>());
	}

	/**
	 * @param id ID of dashboard configuration
	 * @param live true = dashboard will refresh itself based on configured interval,
	 *                false = dashboard will fetch all data on startup
	 * @see DashboardConfig#getRefreshIntervalInSeconds()
	 */
	public DashboardPanel(String id, boolean live) {
		this(null, id, live, new HashMap<>());
	}

	/**
	 * @param pk pk of dashboard configuration
	 *
	 * @param live true = dashboard will refresh itself based on configured interval,
	 *                false = dashboard will fetch all data on startup
	 * @param parameters parameters for querying data TODO: Why is this a map; what do we need the value for?
	 * @see DashboardConfig#getRefreshIntervalInSeconds()
	 */
	public DashboardPanel(Long pk, String id, boolean live, Map<String, IReportParameter> parameters) {
		this.pk = pk;
		this.id = id;
		this.live = live;
		this.parameters = parameters;

		setSizeFull();
		setMargin(false);
		setSpacing(false);
		setLive(live);
	}

	public void setLive(boolean live){
		this.live = live;
		if (live) {
			//refresh automatically when dashboard config changes
			ISubscriber<String> dashboardChangedSubscriber = dashboardPk -> getUI().access(new Runnable() {
				@Override
				public void run() {
					if (dashboardPk.equals(String.valueOf(DashboardPanel.this.pk))) {
						refresh();
					}
				}
			});
			addAttachListener((AttachListener) (AttachEvent event) -> {
				Globals.getDashboardChangedAdaptor().register(dashboardChangedSubscriber);
			});
			addDetachListener((DetachListener) event -> {
				Globals.getDashboardChangedAdaptor().unregister(dashboardChangedSubscriber);
			});
		}
	}

	public void refresh() {
		removeAllComponents();
		if (pk == null && id == null) {
			showWarning("Please provide url parameter 'pk' or 'id' that corresponds to the pk or id of the dashboard you want");
		} else {
			DashboardConfig dashboardConfig = null;
			final DashboardConfigFinder finder = new DashboardConfigFinder();
			if (pk != null) {
				dashboardConfig = finder.setPk(pk).find();
				if (dashboardConfig == null) {
					String.format("No dashboard configuration found with pk = %d", pk);
					return;
				}
			} else if (id != null) {
				dashboardConfig = finder.setId(id).find();
				if (dashboardConfig == null) {
					String.format("No dashboard configuration found with id = '%s'", id);
					return;
				}
				pk = dashboardConfig.getPk();
			}

			// Add factory / line header
			HorizontalLayout horizontalLayout = new HorizontalLayout();
			horizontalLayout.setMargin(false);
			horizontalLayout.setSpacing(false);
			horizontalLayout.setWidth(100, Unit.PERCENTAGE);

			// Add dashboardConfig title
			Label label = new Label(dashboardConfig.getTitle());
			label.addStyleName("dashboardTitle");
			horizontalLayout.addComponent(label);
			horizontalLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
			addComponent(horizontalLayout);

			// Add chart layout
			final DashboardChartArea chartArea = new DashboardChartArea(dashboardConfig, live, parameters);
			addComponent(chartArea);
			setExpandRatio(chartArea, 1.0f);
		}
	}

	private void showWarning(String message) {
		addComponent(new Label(message));
		LOGGER.warn(message);
	}

	protected static String getCaption(String key){
		return ApplicationResourceBundle.getInstance("vaadin-dashboard").getString("dashboardPanel." + key);
	}
}
