package no.goodtech.vaadin.security;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.VerticalLayout;
import no.goodtech.vaadin.UrlUtils;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.tabs.IMenuView;

@UIScope
@SpringView(name = TopMenuView.VIEW_ID)
public class TopMenuView extends VerticalLayout implements IMenuView {

	public static final String VIEW_ID = "TopMenuView";

	@Override
	public boolean isAuthorized(final User user, final String argument) {
		return AccessFunctionManager.isAuthorized(user, new UrlUtils().getParameter("topMenuId", argument));
	}

	@Override
	public String getViewName() {
		return "TopMenuView";
	}

	@Override
	public void enter(final ViewChangeListener.ViewChangeEvent event) {
	}

	public TopMenuView() {
		setMargin(false);
		setSpacing(false);
	}
}
