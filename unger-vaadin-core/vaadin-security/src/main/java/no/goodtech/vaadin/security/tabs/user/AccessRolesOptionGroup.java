package no.goodtech.vaadin.security.tabs.user;

import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.ItemCaptionGenerator;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.security.model.AccessRoleFinder;
import no.goodtech.vaadin.security.model.AccessRoleStub;

/**
 * This class implements all functionality needed for the AccessRoles option group.
 *
 * @author bakke
 */
public class AccessRolesOptionGroup extends CheckBoxGroup<AccessRoleStub> {

    public AccessRolesOptionGroup() {
        super(ApplicationResourceBundle.getInstance("vaadin-security").getString("user.optionGroup.accessFunctions"));
        setItemCaptionGenerator((ItemCaptionGenerator<AccessRoleStub>) AccessRoleStub::getId);
    }
    
    public void refresh() {
        setItems(new AccessRoleFinder().orderById(true).list());

    }
}
