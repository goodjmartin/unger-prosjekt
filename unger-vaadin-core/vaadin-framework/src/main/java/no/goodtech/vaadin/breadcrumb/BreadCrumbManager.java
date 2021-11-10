package no.goodtech.vaadin.breadcrumb;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.navigator.SpringViewProvider;
import no.goodtech.vaadin.tabs.IMenuView;

import java.util.HashMap;
import java.util.Map;

public class BreadCrumbManager {

	private static Map<String, String> breadCrumbEntries = new HashMap<>();

	public static void registerBreadCrumb(final String viewId, final String parentViewId) {
		breadCrumbEntries.put(viewId, parentViewId);
	}

	public static String getBreadCrumbEntry(final String viewId) {
		return breadCrumbEntries.get(viewId);
	}

	public static void createViewTitle(final ViewTitle viewTitle, final ViewChangeListener.ViewChangeEvent event, final SpringViewProvider viewProvider) {
		// Remove old view titles
		viewTitle.removeTrails();

		// Find view
		View view = event.getNewView();
		String viewId = event.getViewName();

		while ((view != null) && (view instanceof IMenuView)) {

			// Find view name
			String viewName = ((IMenuView)view).getViewName();

			// Insert title trail
			viewTitle.insertTrail(viewName);

			// Find parent view ID
			String parentViewId = BreadCrumbManager.getBreadCrumbEntry(viewId);

			if ((parentViewId) != null) {
				if (!"ROOT".equals(parentViewId)) {
					view = viewProvider.getView(parentViewId);
					viewId = parentViewId;
				} else {
					break;
				}
			} else {
				break;
			}
		}

		// Control title visibility
		viewTitle.setVisible(viewTitle.isComponentsAdded());
	}
}
