package no.goodtech.vaadin.security.tabs.accessrole;

import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import no.goodtech.vaadin.buttons.AddButton;
import no.goodtech.vaadin.buttons.RemoveButton;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.main.Constants;
import no.goodtech.vaadin.security.AccessFunctionManager;

public class AccessRoleButtonPanel extends HorizontalLayout {

    private final AddButton addButton;
	private final RemoveButton removeButton;
    private volatile boolean updateAllowed = false;

	public AccessRoleButtonPanel(final IAccessRoleActionListener actionListener) {

		// Set some layout properties
        setHeight(45, Unit.PIXELS);
        setMargin(new MarginInfo(true, true, false, false));

		// Create the Add button
		addButton = new AddButton(actionListener);
		addComponent(addButton);

		// Create the Remove button
		removeButton = new RemoveButton(actionListener);
		addComponent(removeButton);

		// Set the button alignment
		setComponentAlignment(addButton, Alignment.MIDDLE_LEFT);
		setComponentAlignment(removeButton, Alignment.MIDDLE_LEFT);
	}

	public void enableRemoveButton(boolean enabled) {
        removeButton.setEnabled(enabled && updateAllowed);
	}

	public void refresh(final boolean accessRoleIsSelected) {
		// Find logged in user
		User user = (User) VaadinSession.getCurrent().getAttribute(Constants.USER);

		// Check if the user is allowed to perform updates
		if (AccessFunctionManager.isAuthorized(user, AccessRoleTab.ACCESS_ROLE_UPDATE)) {
			updateAllowed = true;
		}

		// Set the initial enabled status
		addButton.setEnabled(updateAllowed);
		removeButton.setEnabled(updateAllowed && accessRoleIsSelected);
	}
}
