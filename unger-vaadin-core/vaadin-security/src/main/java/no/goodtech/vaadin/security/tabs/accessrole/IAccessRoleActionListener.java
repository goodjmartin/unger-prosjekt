package no.goodtech.vaadin.security.tabs.accessrole;

import no.goodtech.vaadin.buttons.AddButton;
import no.goodtech.vaadin.buttons.RemoveButton;

import java.util.Set;

public interface IAccessRoleActionListener extends AddButton.IAddListener, RemoveButton.IRemoveListener {

    /**
     * This method is called when an access role is selected
     *
     * @param accessRoleId The identity of the selected access role
     */
    public void accessRoleSelected(final String accessRoleId);

    /**
     * This method is called when the Save button is clicked
     *
     * @param accessRoleId The access role identity
     * @param accessRoleDescription The access role description
     * @param accessFunctionIds The set of access functions assigned to the access role
     */
	public void addAccessRoleSaveClicked(final String accessRoleId, final String accessRoleDescription, final Set<String> accessFunctionIds);

}
