package no.goodtech.dashboard.ui;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import no.goodtech.admin.tabs.report.IReportParameter;
import no.goodtech.vaadin.UrlUtils;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.security.AccessFunction;
import no.goodtech.vaadin.security.AccessFunctionManager;
import no.goodtech.vaadin.tabs.IMenuView;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * View to run customized reports based on the dashboard configuration model
 */
@SpringView(name = DashboardReportView.VIEW_ID)
public class DashboardReportView extends VerticalLayout implements IMenuView {

	public static final String VIEW_ID = "DashboardReportView";
	public static final String VIEW_NAME = ApplicationResourceBundle.getInstance("vaadin-dashboard").getString("dashboardReport.viewName");

	private static String ACCESS_VIEW = "dashboardPanel.view";

	static {
		AccessFunctionManager.registerAccessFunction(new AccessFunction(ACCESS_VIEW, ApplicationResourceBundle.getInstance("vaadin-dashboard").getString("accessFunction." + ACCESS_VIEW)));
	}

	public DashboardReportView() {
		setSizeFull();
		setMargin(false);
		setSpacing(false);
	}

	public static Long toLongOrNull(String number) {
		if (number != null && StringUtils.isNumeric(number)) {
			return Long.valueOf(number);
		}
		return null;
	}

	public void enter(ViewChangeListener.ViewChangeEvent event) {
		UrlUtils urlUtils = new UrlUtils(event);
		Long pk = toLongOrNull(urlUtils.getParameter("pk"));
		String id = urlUtils.getParameter("id");

		Map<String, IReportParameter> parameters = new HashMap<>();
		//TODO: Fetch report parameter config and process only those, not all parameters in URL!
		for (Map.Entry<String, String> entry : urlUtils.getParameterMap().entrySet()){
			final String name = entry.getKey();
			parameters.put(name, new DummyReportParameter(name, entry.getValue()));
		}
		refresh(pk, id, parameters);
	}

	public void refresh(Long pk, String id, Map<String, IReportParameter> parameters) {
		//TODO: Hack to test live dashboard
		boolean live = false;
		final IReportParameter liveParameter = parameters.get("live");
		if (liveParameter != null && liveParameter.getValueAsString().equals("true")) {
			live = true;
		}
		DashboardPanel panel = new DashboardPanel(pk, id, live, parameters);
//		DashboardPanel panel = new DashboardPanel(pk, id, false, parameters);
		panel.refresh();
		removeAllComponents();
		addComponent(panel);
	}

	public boolean isAuthorized(User user, String argument) {
		return AccessFunctionManager.isAuthorized(user, ACCESS_VIEW);
	}

	@Override
	public String getViewName() {
		return VIEW_NAME;
	}
}
