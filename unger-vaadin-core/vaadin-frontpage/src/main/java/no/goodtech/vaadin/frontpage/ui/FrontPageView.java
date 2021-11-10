package no.goodtech.vaadin.frontpage.ui;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.VerticalLayout;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.tabs.IMenuView;

@UIScope
@SpringView(name = FrontPageView.VIEW_ID)
public class FrontPageView extends VerticalLayout implements IMenuView {
	public static final String VIEW_ID = "FrontPageView";
	public static final String VIEW_NAME = ApplicationResourceBundle.getInstance("vaadin-frontpage").getString("frontPage.viewName");

	private final FlexibleBoard container;

	public FrontPageView() {
		setSizeFull();
		container = new FlexibleBoard();
		addComponent(container);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		container.refresh();
	}

	@Override
	public boolean isAuthorized(no.goodtech.vaadin.login.User user, String argument) {
		// Access for everyone
		return true;
	}

	@Override
	public String getViewName() {
		return VIEW_NAME;
	}
}
