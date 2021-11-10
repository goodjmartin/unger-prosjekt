package no.goodtech.dashboard.ui;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;

/**
 * Separate UI to show dashboards standalone (without menu, authentication e.g.)
 * Example url: http://localhost/gmi/dashboard?pk=42
 */
@Theme("admin")
@SpringUI(path = "dashboard")
@Widgetset("vaadinWidgetSet")
@Push(PushMode.AUTOMATIC)
public class DashboardUi extends UI {

	@Override
	protected void init(final VaadinRequest request) {
		String id = request.getParameter("id");
		DashboardPanel panel = new DashboardPanel(id, true);
		panel.refresh();
		setContent(panel);
	}
}
