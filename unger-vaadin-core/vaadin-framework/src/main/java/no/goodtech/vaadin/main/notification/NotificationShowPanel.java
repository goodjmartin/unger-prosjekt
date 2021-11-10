package no.goodtech.vaadin.main.notification;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import no.goodtech.push.ISubscriber;
import no.goodtech.vaadin.main.ApplicationResourceBundle;


public class NotificationShowPanel extends HorizontalLayout {

	private final Label notificationLabel = new Label();

	public NotificationShowPanel() {
		Button hideButton = new Button(ApplicationResourceBundle.getInstance("vaadin-core").getString("notification.close.button.caption"), FontAwesome.TIMES);
		hideButton.addClickListener((ClickListener) event -> setVisible(false));

		setWidth("100%");
		addComponents(notificationLabel, hideButton);
		setVisible(false);
		addStyleName("notificationPanel");
		notificationLabel.addStyleName("notificationPanel");
		setExpandRatio(notificationLabel, 1);
		setComponentAlignment(hideButton, Alignment.BOTTOM_RIGHT);

		ISubscriber<Notification> notificationSubscriber = notification -> getUI().access(() -> {
			notificationLabel.setCaption(notification.getContent());
			setVisible(true);
			markAsDirty();
		});

		addAttachListener((AttachListener) event -> NotificationFactory.getNotificationAdaptor().register(notificationSubscriber));
		addDetachListener((DetachListener) event -> NotificationFactory.getNotificationAdaptor().unregister(notificationSubscriber));

	}
}
