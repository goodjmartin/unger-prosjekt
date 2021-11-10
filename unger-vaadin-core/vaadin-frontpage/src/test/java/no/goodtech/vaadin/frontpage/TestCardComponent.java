package no.goodtech.vaadin.frontpage;

import com.vaadin.ui.VerticalLayout;
import no.goodtech.vaadin.frontpage.model.FrontPageCardComponent;
import no.goodtech.vaadin.frontpage.model.IFrontPageCardComponent;
import no.goodtech.vaadin.login.User;

@FrontPageCardComponent
public class TestCardComponent extends VerticalLayout implements IFrontPageCardComponent {
	@Override
	public String getViewName() {
		return "TestCard";
	}

	@Override
	public boolean isAuthorized(User user, String arg) {
		return true;
	}
}
