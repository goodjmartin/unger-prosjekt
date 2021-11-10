package no.goodtech.vaadin.security.tabs.accessrole;

import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.vaadin.lists.MessyGrid;
import no.goodtech.vaadin.main.SimpleInputBox;
import no.goodtech.vaadin.search.FilterPanel;
import no.goodtech.vaadin.security.AccessFunction;
import no.goodtech.vaadin.security.model.AccessRole;
import no.goodtech.vaadin.security.model.AccessRoleFinder;
import no.goodtech.vaadin.security.model.AccessRoleStub;
import no.goodtech.vaadin.security.ui.Texts;
import no.goodtech.vaadin.ui.SimpleCrudGridWrapper;

import java.util.List;


public class AccessRoleWrappedGrid extends SimpleCrudGridWrapper<AccessRole, AccessRoleStub, AccessRoleFinder> {

	public AccessRoleWrappedGrid(MessyGrid grid) {
		super(grid, new AccessRoleFinder());
	}

	public AccessRoleWrappedGrid(MessyGrid grid, FilterPanel filterPanel) {
		super(grid, filterPanel);
	}

	@Override
	protected AccessFunction getAccessFunctionView() {
		return new AccessFunction(AccessRoleAdminPanel.ACCESS_VIEW, Texts.get("accessFunction.accessRole.view"));
	}

	@Override
	protected AccessFunction getAccessFunctionEdit() {
		return new AccessFunction(AccessRoleAdminPanel.ACCESS_EDIT, Texts.get("accessFunction.accessRole.edit"));
	}

	public MessyGrid getGrid() {
		return grid;
	}

	@Override
	public SimpleInputBox.IinputBoxContent createDetailForm(EntityStub entity) {
		return new AccessRoleForm(entity);
	}

	@Override
	public AccessRole createEntity() {
		return new AccessRole();
	}


	public void refresh(List<AccessRole> objects) {
		grid.refresh(objects);
	}
}
