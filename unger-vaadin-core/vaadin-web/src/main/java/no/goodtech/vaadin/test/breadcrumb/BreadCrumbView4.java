package no.goodtech.vaadin.test.breadcrumb;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Label;
import no.goodtech.vaadin.breadcrumb.annotation.BreadCrumb;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.tabs.IMenuView;

@UIScope
@SpringView(name = BreadCrumbView4.VIEW_ID)
@BreadCrumb(viewId = BreadCrumbView4.VIEW_ID, parentViewId = BreadCrumbView3.VIEW_ID)
public class BreadCrumbView4 extends VerticalLayout implements IMenuView {

	public static final String VIEW_ID = "BreadCrumbView4";

	private final Label parametersLabel = new Label();

	public BreadCrumbView4() {
		setSizeFull();

		// Show parameters
		HorizontalLayout parameterLayout = new HorizontalLayout();
		parameterLayout.addComponents(new Label("Parameters: "), parametersLabel);

		// Add components
		addComponents(new Label("ViewId=" + VIEW_ID + ", ViewName=" + getViewName()), parameterLayout);
	}

	@Override
	public void enter(final ViewChangeListener.ViewChangeEvent event) {
		parametersLabel.setValue(event.getParameters());
	}

	@Override
	public boolean isAuthorized(final User user, final String argument) {
		return true;
	}

	@Override
	public String getViewName() {
		return VIEW_ID;
	}

}
