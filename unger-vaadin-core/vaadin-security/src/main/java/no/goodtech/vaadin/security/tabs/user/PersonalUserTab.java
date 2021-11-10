package no.goodtech.vaadin.security.tabs.user;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import no.goodtech.vaadin.buttons.SaveButton;
import no.goodtech.vaadin.buttons.SaveButton.ISaveListener;
import no.goodtech.vaadin.global.VaadinSpringContextHelper;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.main.Constants;
import no.goodtech.vaadin.security.AccessFunction;
import no.goodtech.vaadin.security.AccessFunctionManager;
import no.goodtech.vaadin.security.SelfRegistration;
import no.goodtech.vaadin.security.model.User;
import no.goodtech.vaadin.security.model.UserFinder;
import no.goodtech.vaadin.security.tabs.user.UserEditForm.GroupMembershipMode;
import no.goodtech.vaadin.security.ui.Texts;
import no.goodtech.vaadin.tabs.IMenuView;

/**
 * Side for å redigere opplysninger om egen bruker
 */
@UIScope
@SpringView(name = PersonalUserTab.VIEW_ID)
public class PersonalUserTab extends VerticalLayout implements IMenuView {

	public static final String VIEW_ID = "PersonalUserTab";
	public static final String VIEW_NAME = ApplicationResourceBundle.getInstance("vaadin-security").getString("personalUser.viewName");

	public static final String USER_UPDATE = "personalUserUpdate";
	public static final String GROUP_MEMBERSHIP_VIEW = "personalGroupMembershipView";
	public static final String GROUP_MEMBERSHIP_UPDATE = "personalGroupMembershipUpdate";

	private final UserEditForm userEditForm;
	private final SaveButton saveButton;

    static {
        AccessFunctionManager.registerAccessFunction(new AccessFunction(USER_UPDATE, ApplicationResourceBundle.getInstance("vaadin-security").getString("accessFunction.user.personalUserUpdate")));
        AccessFunctionManager.registerAccessFunction(new AccessFunction(GROUP_MEMBERSHIP_VIEW, ApplicationResourceBundle.getInstance("vaadin-security").getString("accessFunction.user.personalGroupMembershipView")));
        AccessFunctionManager.registerAccessFunction(new AccessFunction(GROUP_MEMBERSHIP_UPDATE, ApplicationResourceBundle.getInstance("vaadin-security").getString("accessFunction.user.personalGroupMembershipUpdate")));
    }

	public PersonalUserTab() {
		setWidth(100, Unit.PERCENTAGE);

		userEditForm = new UserEditForm();
		userEditForm.setPersonalMode();

		saveButton = new SaveButton(new ISaveListener() {
			public void saveClicked() {
				if (userEditForm.save()) {
					final Navigator navigator = UI.getCurrent().getNavigator();
					navigator.navigateTo(Constants.DEFAULT_VIEW);
				}
			}
		});

		addComponents(userEditForm, saveButton);
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAuthorized(final no.goodtech.vaadin.login.User user, final String value) {
    	if (AccessFunctionManager.isAuthorized(user, GROUP_MEMBERSHIP_UPDATE))
    		userEditForm.setGroupMembershipMode(GroupMembershipMode.EDITABLE);
    	else if (AccessFunctionManager.isAuthorized(user, GROUP_MEMBERSHIP_VIEW))
    		userEditForm.setGroupMembershipMode(GroupMembershipMode.READONLY);
    	else
    		userEditForm.setGroupMembershipMode(GroupMembershipMode.HIDDEN);

		return AccessFunctionManager.isAuthorized(user, USER_UPDATE);
    }

	@Override
	public String getViewName() {
		return VIEW_NAME;
	}

	public void enter(final ViewChangeEvent event) {
		final no.goodtech.vaadin.login.User user = (no.goodtech.vaadin.login.User) VaadinSession.getCurrent().getAttribute(Constants.USER);

		if (user == null) {
			//self-registration of new user
			userEditForm.refresh(new User());
			setFormEnabled(true);
		} else {
			// Fetch logged in user from database
			User userFromDatabase = new UserFinder().setId(user.getId()).find();
			if (userFromDatabase != null) {
				userEditForm.refresh(userFromDatabase);
				setFormEnabled(true);
			} else {
				setFormEnabled(false);
				Notification.show(String.format(Texts.get("user.personalUserTab.userNotFound"), user.getId()));
			}
		}
	}

	private void setFormEnabled(final boolean enabled) {
		userEditForm.setVisible(enabled);
		saveButton.setEnabled(enabled);
	}

	public boolean isSelfRegistrationAllowed() {
		return VaadinSpringContextHelper.getBean(SelfRegistration.class).getSelfRegistratationRole() != null;
	}
}
