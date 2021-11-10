package no.goodtech.vaadin.security.tabs.user;


import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.vaadin.lists.IExportable;
import no.goodtech.vaadin.lists.MessyGrid;
import no.goodtech.vaadin.main.SimpleInputBox;
import no.goodtech.vaadin.search.FilterPanel;
import no.goodtech.vaadin.security.AccessFunction;
import no.goodtech.vaadin.security.model.User;
import no.goodtech.vaadin.security.model.UserFinder;
import no.goodtech.vaadin.ui.SimpleCrudGridWrapper;

import java.util.List;

public class UserWrappedGrid extends SimpleCrudGridWrapper<User, User, UserFinder> implements IExportable {

	public UserWrappedGrid(MessyGrid grid) {
		super(grid);
	}

	public UserWrappedGrid(MessyGrid grid, FilterPanel filterPanel) {
		super(grid, filterPanel);
	}

	@Override
	protected AccessFunction getAccessFunctionView() {
		return new AccessFunction(UserAdminView.ACCESS_VIEW, null);
	}

	@Override
	protected AccessFunction getAccessFunctionEdit() {
		return new AccessFunction(UserAdminView.ACCESS_EDIT, null);
	}

	@Override
	public SimpleInputBox.IinputBoxContent createDetailForm(EntityStub entity) {
		return new UserEditForm();
	}

	@Override
	public User createEntity() {
		return new User();
	}

	public void refresh(List<User> objects) {
		grid.refresh(objects);
	}

	@Override
	public Integer getDetailsPopupWidth() {
		return 30;
	}
}
