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
@SpringView(name = BreadCrumbView1.VIEW_ID)
@BreadCrumb(viewId = BreadCrumbView1.VIEW_ID)
public class BreadCrumbView1 extends VerticalLayout implements IMenuView {

	public static final String VIEW_ID = "BreadCrumbView1";

	private final Label parametersLabel = new Label();

	public BreadCrumbView1() {
		setSizeFull();

		// Show parameters
		HorizontalLayout parameterLayout = new HorizontalLayout();
		parameterLayout.addComponents(new Label("Parameters: "), parametersLabel);
		parameterLayout.setSpacing(false);
		parameterLayout.setMargin(false);

		// Create button
		Button navigateButton = new Button("Next View");
		navigateButton.addClickListener((Button.ClickListener) event -> UI.getCurrent().getNavigator().navigateTo(BreadCrumbView2.VIEW_ID + "/id=2"));

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
