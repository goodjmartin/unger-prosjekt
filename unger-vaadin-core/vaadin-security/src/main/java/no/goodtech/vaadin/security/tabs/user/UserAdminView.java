package no.goodtech.vaadin.security.tabs.user;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import no.goodtech.vaadin.attachment.ui.ShowAttachmentsInPopupButton;
import no.goodtech.vaadin.lists.DataProviderUtils;
import no.goodtech.vaadin.lists.v7.ListSelectionAwareComponentDisabler;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.main.SimpleInputBox.IinputBoxContent;
import no.goodtech.vaadin.security.AccessFunction;
import no.goodtech.vaadin.security.model.User;
import no.goodtech.vaadin.security.model.UserFinder;
import no.goodtech.vaadin.ui.SimpleCrudAdminPanel;

@UIScope
@SpringView(name = UserAdminView.VIEW_ID)
public class UserAdminView extends SimpleCrudAdminPanel<User, User, UserFinder> {

	public static final String VIEW_ID = "UserTab";
	public static final String VIEW_NAME = ApplicationResourceBundle.getInstance("vaadin-security").getString("user.viewName");
	public static final String USER_VIEW = "userView";
    public static final String USER_UPDATE = "userUpdate";
	public static final String ACCESS_VIEW = "user.view";
	public static final String ACCESS_EDIT = "user.edit";

	private final ShowAttachmentsInPopupButton attachmentButton;

	public UserAdminView() {

		super(new UserFilterPanel(), new UserGrid());


		attachmentButton = createAttachmentButton();
		buttonLayout.addButton(attachmentButton);

		((UserGrid)table).setMessyListener(user -> {
			if (user != null) {
				attachmentButton.setOwner(user);
				attachmentButton.refresh();
			}
		});
	}

	@Override
	public String getViewName() {
		return VIEW_NAME;
	}

	public boolean isAuthorized(no.goodtech.vaadin.login.User user, String value) {

		final boolean authorized = super.isAuthorized(user, value);
		if (!isReadOnly())
			ListSelectionAwareComponentDisabler.control(table, true, attachmentButton);
		return authorized;

	}

	public void enter(ViewChangeListener.ViewChangeEvent event) {
		super.enter(event);
	}

	private ShowAttachmentsInPopupButton createAttachmentButton() {
		ShowAttachmentsInPopupButton button = new ShowAttachmentsInPopupButton();
		button.addClickListener((Button.ClickListener) clickEvent -> {
			User user = getSelectedObject();
			if (user != null) {
				attachmentButton.setOwner(user);
				attachmentButton.refresh();
			}
		});
		button.setEnabled(false);
		return button;
	}



	protected IinputBoxContent createDetailForm(User entity) {
		UserEditForm form = new UserEditForm();
		if (entity.isNew())
			form.refresh(entity);
		else
			form.refresh(entity.load());
		return form;
	}

	@Override
	public User createEntity() {
		return new User();
	}

	protected AccessFunction getAccessFunctionView() {
		return new AccessFunction(USER_VIEW, ApplicationResourceBundle.getInstance("vaadin-security").getString("accessFunction.user.userView"));
	}

	protected AccessFunction getAccessFunctionEdit() {
		return new AccessFunction(USER_UPDATE, ApplicationResourceBundle.getInstance("vaadin-security").getString("accessFunction.user.userUpdate"));
	}


	@Override
	public Integer getDetailsPopupWidth() {
		return 30;
	}
}
