package no.goodtech.vaadin.security.tabs.accessrole;


import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import no.goodtech.vaadin.lists.DataProviderUtils;
import no.goodtech.vaadin.security.AccessFunction;
import no.goodtech.vaadin.security.AccessFunctionManager;
import no.goodtech.vaadin.security.ui.Texts;

@UIScope
@SpringView(name = AccessRoleAdminPanel.VIEW_ID)
public class AccessRoleAdminPanel extends AccessRoleWrappedGrid {
	public static final String VIEW_ID = "AccessRoleAdminPanel";
	public static final String VIEW_NAME = Texts.get("accessrole.viewName");

	public static final String ACCESS_VIEW = "accessRole.view";
	public static final String ACCESS_EDIT = "accessRole.edit";


	static {
		AccessFunctionManager.registerAccessFunction(new AccessFunction(ACCESS_VIEW, Texts.get("accessFunction.accessRole.view")));
		AccessFunctionManager.registerAccessFunction(new AccessFunction(ACCESS_EDIT, Texts.get("accessFunction.accessRole.edit")));
	}

	public AccessRoleAdminPanel(){
		super(new AccessRoleGrid(), new AccessRoleFilterPanel(null));
		((AccessRoleFilterPanel)filterPanel).setActionListener(finder -> {
			grid.setDataProvider(DataProviderUtils.createDataProvider(finder));
			return null;
		});
		setMargin(true);
		setSpacing(true);
	}

	@Override
	public String getViewName() {
		return VIEW_NAME;
	}

}
