package no.goodtech.vaadin.security.tabs.accessrole;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.security.model.AccessRoleFinder;
import no.goodtech.vaadin.security.model.AccessRoleStub;

import java.util.List;

public class AccessRoleTable {

	public static final Object PROPERTY_ID = ApplicationResourceBundle.getInstance("vaadin-security").getString("accessRoleTable.header.id");
 	public static final Object PROPERTY_DESCRIPTION = ApplicationResourceBundle.getInstance("vaadin-security").getString("accessRoleTable.header.description");

	public static IndexedContainer getContainer() {
		IndexedContainer container = new IndexedContainer();

		container.addContainerProperty(PROPERTY_ID, String.class, null);
		container.addContainerProperty(PROPERTY_DESCRIPTION, String.class, null);

        final List<AccessRoleStub> accessRoles = new AccessRoleFinder().list();
        if (accessRoles != null) {
            for (AccessRoleStub accessRole : accessRoles) {
                Item item = container.addItem(accessRole.getId());
                item.getItemProperty(PROPERTY_DESCRIPTION).setValue(accessRole.getDescription());
                item.getItemProperty(PROPERTY_ID).setValue(accessRole.getId());
            }
            container.sort(new Object[]{PROPERTY_ID}, new boolean[]{true});
        }

		return container;
	}

}
