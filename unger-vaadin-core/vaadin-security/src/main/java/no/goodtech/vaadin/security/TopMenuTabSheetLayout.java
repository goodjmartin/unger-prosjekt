package no.goodtech.vaadin.security;

import com.vaadin.navigator.View;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.main.Constants;
import no.goodtech.vaadin.tabs.IMenuView;
import no.goodtech.vaadin.topMenuLayout.TopMenuGroupType;
import no.goodtech.vaadin.topMenuLayout.TopMenuLayout;
import no.goodtech.vaadin.topMenuLayout.ViewType;

import java.util.HashMap;
import java.util.Map;

class TopMenuTabSheetLayout {

	private final Map<String, TabSheet> tabSheetMap = new HashMap<>();
	private static volatile TopMenuLayout topMenuLayout = null;
	private final Map<String, TabSheet.Tab> tabMap = new HashMap<>();
	private volatile String currentTabId;

	static {
		topMenuLayout = no.goodtech.vaadin.global.Globals.getTopMenuManager().getTopMenuLayout();

		if (topMenuLayout != null) {
			if ((topMenuLayout.getTopMenuGroups() != null) && topMenuLayout.getTopMenuGroups().getTopMenuGroup() != null) {
				for (TopMenuGroupType topMenuGroupType : topMenuLayout.getTopMenuGroups().getTopMenuGroup()) {
					AccessFunctionManager.registerAccessFunction(new AccessFunction(topMenuGroupType.getId(), "Tilgang til " + topMenuGroupType.getId()));
				}
			}
		}
	}

	TopMenuTabSheetLayout(final SpringViewProvider viewProvider) {

		// Used to navigate to the current tab ID when the tab caption is clicked on
		JavaScript.getCurrent().addFunction("clickedTab", (JavaScriptFunction) arguments -> {
			String captionArgument = arguments.getString(0);
			//Only reload view the clicked caption is the same as the current view
			if ((currentTabId != null && captionArgument != null) && captionArgument.equals(currentTabId)) {
				UI.getCurrent().getNavigator().navigateTo(currentTabId);
			}
		});

		// Check if file was read successfully
		if (topMenuLayout != null) {
			if ((topMenuLayout.getTopMenuGroups() != null) && topMenuLayout.getTopMenuGroups().getTopMenuGroup() != null) {
				for (TopMenuGroupType topMenuGroup : topMenuLayout.getTopMenuGroups().getTopMenuGroup()) {
					// Create new tab sheet
					TabSheet tabSheet = new TabSheet();
					tabSheet.setTabCaptionsAsHtml(true);
					tabSheet.setSizeFull();

					// Add each panel to tab sheet
					for (ViewType view : topMenuGroup.getView()) {
						User user = (User)VaadinSession.getCurrent().getAttribute(Constants.USER);

						// Lookup view
						View newView = viewProvider.getView(view.getId());

						if ((newView != null) && (newView instanceof IMenuView)) {
							boolean isAuthorized = ((IMenuView)newView).isAuthorized(user, view.getParameter());

							if (isAuthorized) {
								// Construct parameters
								String tabInformation = "/topMenuId=" + topMenuGroup.getId() + "/tabId=" + topMenuGroup.getId() + "-" + view.getId();
								String parameters = (view.getParameter() != null) ? ("/" + view.getParameter() + tabInformation) : tabInformation;
								HorizontalLayout horizontalLayout = new HorizontalLayout();
								horizontalLayout.setSpacing(false);
								horizontalLayout.setMargin(false);
								horizontalLayout.setId(view.getId() + parameters);
								String captionText = (view.getName() != null) ? Menu.get(view.getName()) : ((IMenuView) newView).getViewName();
								String htmlCaption = "<div onclick=\"clickedTab('" + view.getId() + parameters + "');\">" + captionText;
								// Add tab
								tabMap.put(topMenuGroup.getId() + "-" + view.getId(), tabSheet.addTab(horizontalLayout, htmlCaption));
							}
						}
					}
					// Add tab sheet listener to capture tab selections
					tabSheet.addSelectedTabChangeListener((TabSheet.SelectedTabChangeListener) event -> {
						UI.getCurrent().getNavigator().navigateTo(tabSheet.getSelectedTab().getId());
						this.currentTabId = tabSheet.getSelectedTab().getId();
					});
					tabSheetMap.put(topMenuGroup.getId(), tabSheet);
				}
			}
		}
	}

	TabSheet getTabSheetById(final String tabSheetId) {
		return tabSheetMap.get(tabSheetId);
	}

	Component getTabById(final String tabId) {
		return tabMap.get(tabId).getComponent();
	}

}
