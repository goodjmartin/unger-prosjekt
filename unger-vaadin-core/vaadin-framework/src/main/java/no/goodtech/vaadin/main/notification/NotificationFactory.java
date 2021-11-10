package no.goodtech.vaadin.main.notification;

public class NotificationFactory {
	private static volatile NotificationAdaptor notificationAdaptor;

	public NotificationFactory(final NotificationAdaptor notificationAdaptor) {
		NotificationFactory.notificationAdaptor = notificationAdaptor;
	}

	public static NotificationAdaptor getNotificationAdaptor() {
		return notificationAdaptor;
	}

}
