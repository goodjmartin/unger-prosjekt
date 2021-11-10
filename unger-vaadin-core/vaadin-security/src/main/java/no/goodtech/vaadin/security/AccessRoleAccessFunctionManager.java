package no.goodtech.vaadin.security;

import no.goodtech.vaadin.security.model.AccessRole;
import no.goodtech.vaadin.security.model.AccessRoleFinder;

import java.util.List;

import static no.goodtech.vaadin.security.AccessFunctionManager.registerAccessFunction;
import static no.goodtech.vaadin.security.AccessFunctionManager.removeAccessFunction;

public class AccessRoleAccessFunctionManager {
	/**
	 * Updates all connections between AccessRole and AccessFunction with the newAccessFunction
	 *
	 * @param oldId             the old AccessFunction id
	 * @param newAccessFunction the new AccessFunction
	 */
	public static void updateAccessFunction(final String oldId, final AccessFunction newAccessFunction) {
		if (newAccessFunction != null) {
			removeAccessFunction(oldId);
			registerAccessFunction(newAccessFunction);
			List<AccessRole> accessRoles = new AccessRoleFinder().setAccessFunction(oldId).loadList();
			accessRoles.stream().filter(accessRole -> accessRole != null &&
					accessRole.getAccessFunctionIds().contains(oldId)).forEach(accessRole -> {
				accessRole.removeAccessFunctionId(oldId);
				accessRole.addAccessFunctionId(newAccessFunction.getId());
				accessRole.save();
			});
		}
	}
}
