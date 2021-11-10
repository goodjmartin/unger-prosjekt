package no.goodtech.vaadin.security.tabs.user;

import com.vaadin.data.ValueProvider;
import com.vaadin.ui.Label;
import com.vaadin.ui.renderers.ComponentRenderer;
import no.goodtech.vaadin.lists.MessyGrid;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.security.model.User;
import no.goodtech.vaadin.utils.OkOrNotLabel;

public class UserGrid extends MessyGrid<User> {

	public static final String PROPERTY_ID = ApplicationResourceBundle.getInstance("vaadin-security").getString("userTable.header.id");
	public static final String PROPERTY_NAME = ApplicationResourceBundle.getInstance("vaadin-security").getString("userTable.header.name");
	public static final String PROPERTY_EMAIL = ApplicationResourceBundle.getInstance("vaadin-security").getString("userTable.header.email");
	public static final String PROPERTY_ROLES = ApplicationResourceBundle.getInstance("vaadin-security").getString("userTable.header.roles");
	public static final String PROPERTY_BLOCKED = ApplicationResourceBundle.getInstance("vaadin-security").getString("userTable.header.blocked");

	public UserGrid() {
		addColumn((ValueProvider<User, String>) User::getId).setCaption(PROPERTY_ID);
		addColumn((ValueProvider<User, String>) User::getName).setCaption(PROPERTY_NAME);
		addColumn((ValueProvider<User, String>) User::getEmail).setCaption(PROPERTY_EMAIL);
		addColumn((ValueProvider<User, Label>) user -> new OkOrNotLabel(user.getBlocked()), new ComponentRenderer()).setStyleGenerator(item -> "v-align-center").setMinimumWidth(50).setCaption(PROPERTY_BLOCKED);
	}
}

