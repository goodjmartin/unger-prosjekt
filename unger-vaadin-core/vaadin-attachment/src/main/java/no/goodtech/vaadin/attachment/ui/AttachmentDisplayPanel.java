package no.goodtech.vaadin.attachment.ui;


import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.security.AccessFunction;
import no.goodtech.vaadin.security.AccessFunctionManager;
import no.goodtech.vaadin.tabs.IMenuView;

@SuppressWarnings("unused")
@UIScope
@SpringView(name = AttachmentDisplayPanel.VIEW_ID)
public class AttachmentDisplayPanel extends AttachmentPanel implements IMenuView {
	public static final String VIEW_ID = "AttachmentDisplayPanel";
	public static final String VIEW_NAME = getCaption("attachmentDisplayPanel.viewName");
	private static String ACCESS_VIEW = "attachmentDisplayPanel.view";
	private static String ACCESS_EDIT = "attachmentDisplayPanel.edit";

	static {
		AccessFunctionManager.registerAccessFunction(new AccessFunction(ACCESS_VIEW, getCaption("accessFunction.attachmentDisplayPanel.view")));
		AccessFunctionManager.registerAccessFunction(new AccessFunction(ACCESS_EDIT, getCaption("accessFunction.attachmentDisplayPanel.edit")));
	}

	public AttachmentDisplayPanel() {
		super();
		showFilterPanel(true);
		getAddAttachmentButton().setEnabled(false);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
		refreshFilterPanel();
		refresh();
	}

	@Override
	public boolean isAuthorized(final User user, final String argument) {
		boolean isAuthorizedToEdit = AccessFunctionManager.isAuthorized(user, ACCESS_EDIT);
		setReadOnly(!isAuthorizedToEdit);
		return AccessFunctionManager.isAuthorized(user, ACCESS_VIEW);
	}

	@Override
	public String getViewName() {
		return VIEW_NAME;
	}

	private static String getCaption(final String caption) {
		return ApplicationResourceBundle.getInstance("vaadin-attachment").getString(caption);
	}
}
