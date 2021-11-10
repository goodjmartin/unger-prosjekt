package no.goodtech.vaadin.security;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import no.goodtech.vaadin.UrlUtils;

public class TopMenu extends Panel {

	private final TopMenuTabSheetLayout topMenuTabSheetLayout;

	public TopMenu(final SpringViewProvider viewProvider) {

		topMenuTabSheetLayout = new TopMenuTabSheetLayout(viewProvider);
		addStyleName("topMenu");
		addStyleName(ValoTheme.PANEL_BORDERLESS);
		setVisible(false);
	}

	public boolean handleTopMenu(final ViewChangeListener.ViewChangeEvent event, final Navigator navigator) {
		// Check if top menu should be shown
		String topMenuId = new UrlUtils().getParameter("topMenuId", event.getParameters());

		setVisible(false);

		if ((event.getNewView() instanceof TopMenuView)) {
			setTabSheetById(topMenuId, null);
			setVisible(true);
			return false;
		} else if (topMenuId != null) {
			String tabId = new UrlUtils().getParameter("tabId", event.getParameters());

			if (tabId != null) {
				setTabSheetById(topMenuId, tabId);
				setVisible(true);
			}
		} else if ((event.getParameters() != null) && !event.getParameters().contains("menu=true")) {
			// Get previous view state
			String viewState = navigator.getState();

			// Find previous topMenuId and tabId
			UrlUtils urlUtils = new UrlUtils();
			String prevTopMenuId = urlUtils.getParameter("topMenuId", viewState);
			String prevTabId = urlUtils.getParameter("tabId", viewState);

			if ((prevTopMenuId != null) && (prevTabId != null)) {
				// Navigate to view with added state
				navigator.navigateTo(event.getViewName() + "/" + event.getParameters() + "/topMenuId=" + prevTopMenuId + "/tabId=" + prevTabId);
				return false;
			}
		}

		return true;
	}

	private void setTabSheetById(final String tabSheetId, final String tabId) {
		TabSheet tabSheet = topMenuTabSheetLayout.getTabSheetById(tabSheetId);

		if (tabSheet != null) {
			setContent(tabSheet);

			if (tabId != null) {
				Component component = topMenuTabSheetLayout.getTabById(tabId);
				if (!component.equals(tabSheet.getSelectedTab())) {
					tabSheet.setSelectedTab(component);
				}
			} else {
				UI.getCurrent().getNavigator().navigateTo(tabSheet.getSelectedTab().getId());
			}
		}
	}

}
