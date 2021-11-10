package no.goodtech.admin.tabs.report;

import com.vaadin.v7.ui.TextField;
import no.goodtech.admin.report.CurrentUsernameParameterType;
import no.goodtech.vaadin.login.User;

/**
 * Shows name of current logged in user and provides the username to the data fetcher
 * @author oystein
 *
 */
public class CurrentUsernameParameter extends TextField implements IReportParameterComponent {

	private String parameterValue = "";
	private final String name;

	public CurrentUsernameParameter(CurrentUsernameParameterType type, final User currentUser) {
		
		addStyleName("textFieldParameter");
		setCaption(type.getLabel());
		this.name = type.getName();

		if (currentUser != null) {
			parameterValue = currentUser.getId();
			setValue(currentUser.getFullName());
		}
		setReadOnly(true);
	}

	@Override
	public String getValueAsString() {
		return  parameterValue;
	}

	public void setValueAsString(String value) {
		//we doesn't support this. Would be a security hole
	}

	public String getName() {
		return name;
	}
}
