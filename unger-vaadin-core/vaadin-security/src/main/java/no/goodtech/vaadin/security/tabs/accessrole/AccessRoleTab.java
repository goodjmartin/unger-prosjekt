package no.goodtech.vaadin.security.tabs.accessrole;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Notification;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.security.AccessFunction;
import no.goodtech.vaadin.security.AccessFunctionManager;
import no.goodtech.vaadin.security.model.AccessRole;
import no.goodtech.vaadin.security.model.AccessRoleFinder;
import no.goodtech.vaadin.tabs.IMenuView;

import java.util.Set;

@UIScope
@SpringView(name = AccessRoleTab.VIEW_ID)
public class AccessRoleTab extends HorizontalLayout implements IAccessRoleActionListener, IMenuView {

	public static final String VIEW_ID = "AccessRoleTab";
	public static final String VIEW_NAME = ApplicationResourceBundle.getInstance("vaadin-security").getString("accessrole.viewName");

	public static final String ACCESS_ROLE_VIEW = "accessRoleView";
    public static final String ACCESS_ROLE_UPDATE = "accessRoleUpdate";

    private final AccessRoleList accessRoleList;
    private final AccessRoleDetails accessRoleDetails;
    private volatile String selectedAccessRoleId = null;

    static {
        AccessFunctionManager.registerAccessFunction(new AccessFunction(ACCESS_ROLE_VIEW, ApplicationResourceBundle.getInstance("vaadin-security").getString("accessFunction.accessrole.accessRoleView")));
        AccessFunctionManager.registerAccessFunction(new AccessFunction(ACCESS_ROLE_UPDATE, ApplicationResourceBundle.getInstance("vaadin-security").getString("accessFunction.accessrole.accessRoleUpdate")));
    }

	public AccessRoleTab() {
        setSizeFull();
		setSpacing(false);
		setMargin(false);

        final HorizontalSplitPanel horizontal = new HorizontalSplitPanel();
        horizontal.setSplitPosition(40);
        addComponent(horizontal);

        // Create the access role list and detail panels
        accessRoleDetails = new AccessRoleDetails(this);
        accessRoleList = new AccessRoleList(this);
        accessRoleDetails.setVisible(false);

        // Add the access role panels
        horizontal.addComponent(accessRoleList);
        horizontal.addComponent(accessRoleDetails);
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public void accessRoleSelected(final String accessRoleId) {
        // Keep the last selected access role
        selectedAccessRoleId = accessRoleId;

        if (accessRoleId != null) {
			AccessRoleFinder accessRoleFinder = new AccessRoleFinder();
			accessRoleFinder.setId(accessRoleId);
            AccessRole accessRole = accessRoleFinder.find();
            accessRoleDetails.setVisible(true);
            accessRoleDetails.refresh(accessRole);
        } else {
            accessRoleDetails.setVisible(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAccessRoleSaveClicked(final String accessRoleId, final String accessRoleDescription, final Set<String> accessFunctionIds) {
        if (selectedAccessRoleId == null) {
			AccessRole accessRole = new AccessRole();
			accessRole.setId(accessRoleId);
			accessRole.setDescription(accessRoleDescription);
			accessRole.setAccessFunctionIds(accessFunctionIds);
			if (accessRole.save() != null) {
				Notification.show(ApplicationResourceBundle.getInstance("vaadin-security").getString("accessRoleTab.notification.add.success") + " " + accessRoleId);
			} else {
				Notification.show(ApplicationResourceBundle.getInstance("vaadin-security").getString("accessRoleTab.notification.add.failure") + " " + accessRoleId, Notification.Type.WARNING_MESSAGE);
			}
        } else {
			AccessRoleFinder accessRoleFinder = new AccessRoleFinder();
			accessRoleFinder.setId(selectedAccessRoleId);
			AccessRole accessRole = accessRoleFinder.find();
			accessRole.setId(accessRoleId);
			accessRole.setDescription(accessRoleDescription);
            accessRole.setAccessFunctionIds(accessFunctionIds);
			if (accessRole.save() != null) {
				Notification.show(ApplicationResourceBundle.getInstance("vaadin-security").getString("accessRoleTab.notification.modify.success") + " " + accessRoleId);
			} else {
				Notification.show(ApplicationResourceBundle.getInstance("vaadin-security").getString("accessRoleTab.notification.modify.failure") + " " + accessRoleId, Notification.Type.WARNING_MESSAGE);
			}
        }

        // Refresh the list and notify the user
        accessRoleList.refresh(selectedAccessRoleId != null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAuthorized(final User user, final String value) {
        return AccessFunctionManager.isAuthorized(user, ACCESS_ROLE_VIEW);
    }

	@Override
	public String getViewName() {
		return VIEW_NAME;
	}

	@Override
	public void addClicked() {
		selectedAccessRoleId = null;
		accessRoleList.refresh(selectedAccessRoleId != null);
		accessRoleDetails.setVisible(true);
		accessRoleDetails.refresh(null);
	}

	@Override
	public void removeClicked() {
		AccessRoleFinder accessRoleFinder = new AccessRoleFinder();
		accessRoleFinder.setId(selectedAccessRoleId);
		AccessRole accessRole = accessRoleFinder.find();
		if ((accessRole != null) && accessRole.delete()) {
			Notification.show(ApplicationResourceBundle.getInstance("vaadin-security").getString("accessRoleTab.notification.remove.success") + " " + selectedAccessRoleId);
		} else {
			Notification.show(ApplicationResourceBundle.getInstance("vaadin-security").getString("accessRoleTab.notification.remove.failure") + " " + selectedAccessRoleId, Notification.Type.WARNING_MESSAGE);
		}

		selectedAccessRoleId = null;

		accessRoleList.refresh(selectedAccessRoleId != null);
	}

	@Override
	public void enter(final ViewChangeListener.ViewChangeEvent event) {
		accessRoleList.refresh(selectedAccessRoleId != null);
	}

}
