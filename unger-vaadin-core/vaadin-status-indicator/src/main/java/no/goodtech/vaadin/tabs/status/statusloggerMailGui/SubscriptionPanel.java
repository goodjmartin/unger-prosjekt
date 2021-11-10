package no.goodtech.vaadin.tabs.status.statusloggerMailGui;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.Table;
import no.goodtech.vaadin.tabs.status.model.StatusIndicatorSubscription;

import java.util.List;

public class SubscriptionPanel extends VerticalLayout {
    private StatusIndicatorSubscriptionContainer statusIndicatorSubscriptionContainer;
	private final SubscriptionTable subscriptionTable;

    public SubscriptionPanel(final IMailPanelActionListener mailPanelActionListener)
    {
		setSpacing(false);

        subscriptionTable = new SubscriptionTable();
        statusIndicatorSubscriptionContainer = new StatusIndicatorSubscriptionContainer();
        subscriptionTable.setContainerDataSource(statusIndicatorSubscriptionContainer);
        subscriptionTable.setSelectable(true);
        subscriptionTable.setMultiSelect(false);
        subscriptionTable.setImmediate(true);
		subscriptionTable.setSizeFull();
        addComponent(subscriptionTable);

        final SubscriptionButtonPanel subscriptionButtonPanel = new SubscriptionButtonPanel(mailPanelActionListener);
        addComponent(subscriptionButtonPanel);
        // Add a listener to handle row selection events
        subscriptionTable.addValueChangeListener(new Table.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent event) {
                StatusIndicatorSubscription subscription = (StatusIndicatorSubscription) event.getProperty().getValue();
                // Enable and disable the Remove button and details panel as appropriate
                if (subscription != null) {
                    subscriptionButtonPanel.enableRemoveButton(true);
                } else {
                    subscriptionButtonPanel.enableRemoveButton(false);
                }
                // Notify the listener
				mailPanelActionListener.subscriptionSelected(subscription);
            }
        });
    }

    public void refreshContainer(List<StatusIndicatorSubscription> statusLoggerRepository)
    {
        statusIndicatorSubscriptionContainer.refresh(statusLoggerRepository);
		subscriptionTable.refreshRowCache();
    }
}
