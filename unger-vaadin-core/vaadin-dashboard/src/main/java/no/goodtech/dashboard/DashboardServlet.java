package no.goodtech.dashboard;

import com.vaadin.server.*;
import com.vaadin.spring.server.SpringVaadinServlet;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;

@Profile("VaadinCoreServlet")
@Component("vaadinServlet")
public class DashboardServlet extends SpringVaadinServlet {

	@Override
	protected void servletInitialized() throws ServletException {
		super.servletInitialized();

		getService().setSystemMessagesProvider(
				(SystemMessagesProvider) systemMessagesInfo -> {
					CustomizedSystemMessages messages = new CustomizedSystemMessages();

					// Session expired
					messages.setSessionExpiredNotificationEnabled(false);
					messages.setSessionExpiredURL(null);

					// Internal error
					messages.setInternalErrorNotificationEnabled(false);
					messages.setInternalErrorURL(null);

					// Communication error
					messages.setCommunicationErrorCaption(ApplicationResourceBundle.getInstance("vaadin-dashboard").getString("dashboardServlet.systemMessages.communicationError.caption"));
					messages.setCommunicationErrorMessage(ApplicationResourceBundle.getInstance("vaadin-dashboard").getString("dashboardServlet.systemMessages.communicationError.message"));
					messages.setCommunicationErrorNotificationEnabled(true);
					messages.setCommunicationErrorURL(null);

					return messages;
				});
	}

}
