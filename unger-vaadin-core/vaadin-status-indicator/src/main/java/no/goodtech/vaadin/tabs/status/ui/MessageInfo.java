package no.goodtech.vaadin.tabs.status.ui;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.themes.BaseTheme;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.tabs.status.model.StatusLogEntryStub;

/**
 * Created by IntelliJ IDEA
 * <p/>
 * User: bakke
 */
public class MessageInfo extends HorizontalLayout {

    public MessageInfo(final StatusLogEntryStub statusLogEntry) {
		setMargin(false);
		this.setSizeFull();
		Component message;
        if (statusLogEntry.getDetails() != null) {
            Button clickableMessage = new Button(statusLogEntry.getMessage());
            clickableMessage.setStyleName(BaseTheme.BUTTON_LINK);

            clickableMessage.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    Window window = new Window(ApplicationResourceBundle.getInstance("vaadin-status-indicator").getString("status.detailsLink.caption"));
					window.setModal(true);
					window.setWidth(80, Sizeable.Unit.PERCENTAGE);
					window.setHeight(80, Sizeable.Unit.PERCENTAGE);
					window.setVisible(true);

					VerticalLayout verticalLayout = new VerticalLayout();
					verticalLayout.addComponent(new Label("<b>" + ApplicationResourceBundle.getInstance("vaadin-status-indicator").getString("status.detailsLink.messageHeader") + "</b><br/>", ContentMode.HTML));
					verticalLayout.addComponent(new Label(statusLogEntry.getMessage(), ContentMode.PREFORMATTED));
					
					if (statusLogEntry.getDetails() != null) {
						verticalLayout.addComponent(new Label("<br/><b>" + ApplicationResourceBundle.getInstance("vaadin-status-indicator").getString("status.detailsLink.exceptionHeader") + "</b><br/>", ContentMode.HTML));
					    verticalLayout.addComponent(new Label(statusLogEntry.getDetails(), ContentMode.PREFORMATTED));
					}

					window.setContent(verticalLayout);
					UI.getCurrent().addWindow(window);
                }
            });
            message = clickableMessage;
        }
        else {
	        message = new Label(statusLogEntry.getMessage());
	        message.setStyleName("statusLogEntry-message");
	        message.setWidth(100, Unit.PERCENTAGE);
	        addComponent(message);
        }
        message.setWidth(100, Unit.PERCENTAGE);
        addComponent(message);
    }

}
