package no.goodtech.vaadin.main.notification;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.security.AccessFunction;
import no.goodtech.vaadin.security.AccessFunctionManager;
import no.goodtech.vaadin.tabs.IMenuView;
import no.goodtech.vaadin.utils.Utils;

import java.util.Date;


@UIScope
@SpringView(name = NotificationCreatePanel.VIEW_ID)
public class NotificationCreatePanel extends VerticalLayout implements IMenuView {

	public static final String VIEW_ID = "NotificationCreatePanel";
	public static final String VIEW_NAME = ApplicationResourceBundle.getInstance("vaadin-core").getString("notification.viewName");

	private static final String ACCESS_SEND = "notificationSend";

	static {
		AccessFunctionManager.registerAccessFunction(new AccessFunction(ACCESS_SEND, ApplicationResourceBundle.getInstance("vaadin-core").getString("accessFunction.notification.send")));
	}

	private TextArea messageField;
	private Button sendButton;

	public NotificationCreatePanel() {
		messageField = new TextArea(ApplicationResourceBundle.getInstance("vaadin-core").getString("notification.create.message.caption"));
		sendButton = new Button(ApplicationResourceBundle.getInstance("vaadin-core").getString("notification.create.button.caption"), VaadinIcons.PAPERPLANE);
		sendButton.addClickListener((ClickListener) event -> NotificationFactory.getNotificationAdaptor().publish(new Notification(Utils.DATETIME_FORMATTER.format(new Date()) + " - " + messageField.getValue())));
	}

	public void enter(ViewChangeEvent event) {
		if (getComponentCount() == 0) {
			addComponents(messageField, sendButton);
		}
	}

	public boolean isAuthorized(User user, String argument) {
		return AccessFunctionManager.isAuthorized(user, ACCESS_SEND);
	}

	@Override
	public String getViewName() {
		return VIEW_NAME;
	}

}
