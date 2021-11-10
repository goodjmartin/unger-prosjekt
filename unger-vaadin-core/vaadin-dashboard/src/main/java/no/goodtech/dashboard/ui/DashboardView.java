package no.goodtech.dashboard.ui;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.VerticalLayout;
import no.goodtech.vaadin.UrlUtils;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.security.AccessFunction;
import no.goodtech.vaadin.security.AccessFunctionManager;
import no.goodtech.vaadin.tabs.IMenuView;

import java.util.HashMap;

/**
 * Shows dashboards embedded in a normal view and accessible via menu
 * You must specify pk as a request parameter and the pk must correspond to a pre-configured dashboard
 */
@UIScope
@SpringView(name = DashboardView.VIEW_ID)
public class DashboardView extends VerticalLayout implements IMenuView {

	public static final String VIEW_ID = "DashboardView";
	public static final String VIEW_NAME = ApplicationResourceBundle.getInstance("vaadin-dashboard").getString("dashboard.viewName");

	private static String ACCESS_VIEW = "dashboardPanel.view";

	static {
		AccessFunctionManager.registerAccessFunction(new AccessFunction(ACCESS_VIEW, ApplicationResourceBundle.getInstance("vaadin-dashboard").getString("accessFunction." + ACCESS_VIEW)));
	}
	
	public DashboardView() {
		setSizeFull();
		setMargin(false);
		setSpacing(false);
	}

	public void enter(ViewChangeEvent event) {
		UrlUtils url = new UrlUtils(event);
		Long pk = DashboardReportView.toLongOrNull(url.getParameter("pk"));
		String id = url.getParameter("id");
		DashboardPanel panel = new DashboardPanel(pk, id, true, new HashMap<>());
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
