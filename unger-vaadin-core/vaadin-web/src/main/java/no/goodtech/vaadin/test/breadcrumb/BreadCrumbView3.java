package no.goodtech.vaadin.test.breadcrumb;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Label;
import no.goodtech.vaadin.breadcrumb.annotation.BreadCrumb;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.tabs.IMenuView;

@UIScope
@SpringView(name = BreadCrumbView3.VIEW_ID)
@BreadCrumb(viewId = BreadCrumbView3.VIEW_ID, parentViewId = BreadCrumbView2.VIEW_ID)
public class BreadCrumbView3 extends VerticalLayout implements IMenuView {

	public static final String VIEW_ID = "BreadCrumbView3";

	private final Label parametersLabel = new Label();

	public BreadCrumbView3() {
		setSizeFull();

		// Show parameters
		HorizontalLayout parameterLayout = new HorizontalLayout();
		parameterLayout.addComponents(new Label("Parameters: "), parametersLabel);
		parameterLayout.setMargin(false);
		parameterLayout.setSpacing(false);

		// Create button
		Button navigateButton = new Button("Next View");
		navigateButton.addClickListener((Button.ClickListener) event -> UI.getCurrent().getNavigator().navigateTo(BreadCrumbView4.VIEW_ID + "/id=4"));

		// Add components
		addComponents(new Label("ViewId=" + VIEW_ID + ", ViewName=" + getViewName()), parameterLayout, navigateButton);
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
