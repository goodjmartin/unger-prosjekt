package no.goodtech.vaadin.security.tabs.accessrole;

import no.goodtech.persistence.search.SearchFilterGroup;
import no.goodtech.vaadin.search.FilterPanel;
import no.goodtech.vaadin.security.model.AccessRoleFinder;


public class AccessRoleFilterPanel extends FilterPanel<AccessRoleFinder> {

	public AccessRoleFilterPanel(FilterActionListener actionListener) {
		//super(actionListener);
		super(actionListener, true);
	}

	@Override
	public AccessRoleFinder getFinder() {
		AccessRoleFinder finder = new AccessRoleFinder();

		String text = getFilterString();
		finder.addGroup(SearchFilterGroup.GroupingOperator.OR);
		//finder.setValueLike("id", text);
		//finder.setValueLike("description", text);

		return finder;
	}
}
