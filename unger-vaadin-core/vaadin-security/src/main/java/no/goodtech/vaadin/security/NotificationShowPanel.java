package no.goodtech.vaadin.security;

/*
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import no.goodtech.push.ISubscriber;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.main.notification.Notification;
import no.goodtech.vaadin.main.notification.NotificationFactory;
import no.goodtech.vaadin.security.model.*;
import no.goodtech.vaadin.security.ui.Texts;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class NotificationShowPanel extends HorizontalLayout {

	private final Label notificationLabel = new Label();
	List<Notification> notifications = new ArrayList<>();

	public NotificationShowPanel(final String userId) {
		Button hideButton = new Button(ApplicationResourceBundle.getInstance("vaadin-core").getString("notification.close.button.caption"), VaadinIcons.CLOSE);
		hideButton.addClickListener((ClickListener) event -> {
			setVisible(false);
			notifications.clear();
		});

		setWidth("100%");
		addComponents(notificationLabel, hideButton);
		setVisible(false);
		setExpandRatio(notificationLabel, 1);
		setComponentAlignment(hideButton, Alignment.BOTTOM_RIGHT);
		ISubscriber<Notification> notificationSubscriber = notification -> getUI().access(() -> {
			// If there are more than three notifications, the oldest is removed and the new is added
			sortNotifications(notification);
			// Clears out the css styling on the HorizontalLayout
			setStyleName(null);
			if (notification.getUserId() != null) {
				// Shows notification to specific user
				if (notification.getUserId().equals(userId)) {
					showMessage();
					setStyleName(Texts.get("notificationShowPanel.css.setStyle.niceMessage"));
				}
			} else if(notification.getAccessRoleId() != null){
				// Shows notification to specific accessRole (e.g Blanderi)
				if(userId == getUserAccessRole(userId, notification.getAccessRoleId())){
					showMessage();
					addStyleName(Texts.get("notificationShowPanel.css.setStyle.warning"));
				}
			} else {
				// Shows notification to all users
				showMessage();
				addStyleName(Texts.get("notificationShowPanel.css.setStyle.notificationPanel"));
			}
		});

		addAttachListener((AttachListener) event -> NotificationFactory.getNotificationAdaptor().register(notificationSubscriber));
		addDetachListener((DetachListener) event -> NotificationFactory.getNotificationAdaptor().unregister(notificationSubscriber));
	}

	private void showMessage(){
		StringBuilder sb = new StringBuilder();
		for(Notification n : notifications){
			sb.append(n.getContent() + "\n");
		}
		// Sets contentmode to PREFORMATTED so that we can use "/n" e.g
		notificationLabel.setContentMode(notificationLabel.getContentMode().PREFORMATTED);
		notificationLabel.setValue(sb.toString());
		setVisible(true);
		markAsDirty();
	}


	public String getUserAccessRole(final String userId, List<String> accessRoleId){
		List<UserStub> u = new UserFinder().setAccessRoles(accessRoleId).list();
		for (UserStub uu : u){
			if (uu.getId().equals(userId)){
				return userId;
			}
		}
		return null;
	}

	public void sortNotifications(Notification notification){
		// Limits the number of notifications to be shown to five
		if (notifications.size() >= 5){
			Collections.rotate(notifications, -1);
			notifications.set(notifications.size() - 1, notification);
		}else{
			notifications.add(notification);
		}
	}

}*/

