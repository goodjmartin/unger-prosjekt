package no.goodtech.vaadin.security.tabs.accessrole;

import no.goodtech.vaadin.lists.MessyGrid;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.security.model.AccessRoleStub;

public class AccessRoleGrid extends MessyGrid<AccessRoleStub> {

	public static final String ID = ApplicationResourceBundle.getInstance("vaadin-security").getString("accessRoleTable.header.id");
	public static final String DESCRIPTION = ApplicationResourceBundle.getInstance("vaadin-security").getString("accessRoleTable.header.description");

	public AccessRoleGrid() {
		setCaption("Registrerte roller"); //TODO
		addColumn(AccessRoleStub::getId).setCaption(ID);
		addColumn(AccessRoleStub::getDescription).setCaption(DESCRIPTION);
	}
}