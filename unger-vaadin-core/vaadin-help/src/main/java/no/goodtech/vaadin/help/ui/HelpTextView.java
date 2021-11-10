package no.goodtech.vaadin.help.ui;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.VerticalLayout;
import no.goodtech.vaadin.UrlUtils;
import no.goodtech.vaadin.frontpage.model.FrontPageCardComponent;
import no.goodtech.vaadin.frontpage.model.IFrontPageCardComponent;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.security.AccessFunction;
import no.goodtech.vaadin.security.AccessFunctionManager;
import no.goodtech.vaadin.tabs.IMenuView;

import java.util.HashMap;
import java.util.Map;

/**
 * View with a help text on top
 */
@UIScope
@SpringView(name = HelpTextView.VIEW_ID)
@FrontPageCardComponent
public class HelpTextView extends VerticalLayout implements IMenuView, IFrontPageCardComponent {

	static final String VIEW_ID = "HelpTextView";
	private static final String DEFAULT_HELP_TEXT_ID = "FRONTPAGE";
	private static final String UPDATE = "helpTextUpdate";
	private static final String VIEW_NAME = ApplicationResourceBundle.getInstance("vaadin-help").getString("helptext.viewName");
	private final HelpTextLabel helpTextLabel = new HelpTextLabel();
	private boolean isEditable = false;

	static {
		AccessFunctionManager.registerAccessFunction(new AccessFunction(UPDATE, getText("accessFunction.helptext.update")));
	}

	public HelpTextView() {
		setSizeFull();
		addComponents(helpTextLabel);
	}
	
	public void enter(final ViewChangeEvent event) {
		String helpTextId = new UrlUtils(event).getParameter("helpTextId");
		if (helpTextId == null) {
			helpTextId = DEFAULT_HELP_TEXT_ID;
		}
		helpTextLabel.refresh(helpTextId, isEditable);
	}

	@Override
	public boolean isAuthorized(User user, String argument) {
		isEditable = AccessFunctionManager.isAuthorized(user, UPDATE);
		return true; //anybody can view
	}

	@Override
	public Map<String,String> getArguments(){
		Map<String, String> args = new HashMap<>();
		args.put("id", DEFAULT_HELP_TEXT_ID);
		return args;
	}

	@Override
	public void setArguments(Map<String, String> args){
		String id = args.get("id");
		helpTextLabel.refresh(id, isEditable);
	}

	@Override
	public String getViewName() {
		return VIEW_NAME;
	}

	@Override
	public String getParentViewId() {
		return VIEW_ID;
	}

	private static String getText(final String key) {
		return ApplicationResourceBundle.getInstance("vaadin-help").getString(key);
	}
}
