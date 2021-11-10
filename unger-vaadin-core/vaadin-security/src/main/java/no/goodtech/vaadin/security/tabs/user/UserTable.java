package no.goodtech.vaadin.security.tabs.user;

import com.vaadin.v7.data.util.BeanItemContainer;
import no.goodtech.vaadin.lists.v7.IMessyTableActionListener;
import no.goodtech.vaadin.lists.v7.MessyTable;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.security.model.User;

public class UserTable extends MessyTable<User> {

	public static final String PROPERTY_ID = ApplicationResourceBundle.getInstance("vaadin-security").getString("userTable.header.id");
	public static final String PROPERTY_NAME = ApplicationResourceBundle.getInstance("vaadin-security").getString("userTable.header.name");
	public static final String PROPERTY_EMAIL = ApplicationResourceBundle.getInstance("vaadin-security").getString("userTable.header.email");
	public static final String PROPERTY_ROLES = ApplicationResourceBundle.getInstance("vaadin-security").getString("userTable.header.roles");
    public static final String PROPERTY_BLOCKED = ApplicationResourceBundle.getInstance("vaadin-security").getString("userTable.header.blocked");
	
	public UserTable(IMessyTableActionListener<User> listener) {
    	super(new BeanItemContainer<>(User.class), listener, false);

    	setVisibleColumns("id", "name", "email", "blocked");
    	setColumnHeaders(PROPERTY_ID, PROPERTY_NAME, PROPERTY_EMAIL, PROPERTY_BLOCKED);
//TODO    	addGeneratedColumn("blocked", new BooleanColumnGenerator("ja", "nei", "nei"));
		setColumnCollapsingAllowed(true);
        setImmediate(true);
        setSizeFull();
	}	
//{
//
//	public static final Object PROPERTY_ID = ApplicationResourceBundle.getInstance("vaadin-security").getString("userTable.header.id");
//	public static final Object PROPERTY_NAME = ApplicationResourceBundle.getInstance("vaadin-security").getString("userTable.header.name");
//	public static final Object PROPERTY_ROLES = ApplicationResourceBundle.getInstance("vaadin-security").getString("userTable.header.roles");
//    public static final Object PROPERTY_BLOCKED = ApplicationResourceBundle.getInstance("vaadin-security").getString("userTable.header.blocked");
//
//	public static IndexedContainer getContainer() {
//		IndexedContainer container = new IndexedContainer();
//
//		container.addContainerProperty(PROPERTY_ID, String.class, null);
//		container.addContainerProperty(PROPERTY_NAME, String.class, null);
//		container.addContainerProperty(PROPERTY_ROLES, String.class, null);
//        container.addContainerProperty(PROPERTY_BLOCKED, Embedded.class, null);
//
//        final List<User> users = new UserFinder().orderById(true).loadList();
//        if (users != null) {
//            for (User user : users) {
//                Item item = container.addItem(user.getPk());
//                item.getItemProperty(PROPERTY_ID).setValue(user.getId());
//                item.getItemProperty(PROPERTY_NAME).setValue(user.getName());
//
//                // Obtain a display friendly version of the access role set
//                Set<AccessRole> accessRoles = user.getAccessRoles();
//                List<String> displayAccessRoles = new ArrayList<String>();
//                for (AccessRole accessRole : accessRoles)  {
//                    displayAccessRoles.add(accessRole.getId());
//                }
//
//                // Sort the list elements
//                Collections.sort(displayAccessRoles);
//                item.getItemProperty(PROPERTY_ROLES).setValue(displayAccessRoles.toString());
//
//                if (user.getLoginFailures() >= Globals.getMaxLoginFailureAttempts()) {
//                    item.getItemProperty(PROPERTY_BLOCKED).setValue(new Embedded(null, new ThemeResource("images/ok.png")));
//                }
//            }
//        }
//
//		return container;
//	}
//
}
