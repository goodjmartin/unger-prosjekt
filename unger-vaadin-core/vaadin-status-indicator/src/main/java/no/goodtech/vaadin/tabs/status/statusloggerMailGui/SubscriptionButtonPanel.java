package no.goodtech.vaadin.tabs.status.statusloggerMailGui;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import no.goodtech.vaadin.buttons.AddButton;
import no.goodtech.vaadin.buttons.RemoveButton;

public class SubscriptionButtonPanel extends HorizontalLayout {

	private final RemoveButton removeButton;
    private volatile boolean updateAllowed = false;

    public SubscriptionButtonPanel(final IMailPanelActionListener mailPanelActionListener) {

        // Set some layout properties
        setHeight(45, Unit.PIXELS);
        setMargin(new MarginInfo(true, true, false, false));

        // Create the Add button
		AddButton addButton = new AddButton(mailPanelActionListener);
        addComponent(addButton);

        // Create the Remove button
		removeButton = new RemoveButton(mailPanelActionListener);
        addComponent(removeButton);

        // Set the button alignment
        setComponentAlignment(addButton, Alignment.MIDDLE_LEFT);
        setComponentAlignment(removeButton, Alignment.MIDDLE_LEFT);
		updateAllowed = true;
		removeButton.setEnabled(true);
    }

    public void enableRemoveButton(boolean enable) {
        removeButton.setEnabled(enable && updateAllowed);
    }

}
