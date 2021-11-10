package no.goodtech.vaadin.security.model;


import no.goodtech.persistence.jpa.AbstractFinder;
import no.goodtech.vaadin.search.FilterPanel;

public class AccessRoleFinder extends AbstractFinder<AccessRole, AccessRoleStub, AccessRoleFinder> implements FilterPanel.IMaxRowsAware {


   private static final long serialVersionUID = 1L;

    public AccessRoleFinder() {
        super("select ar from AccessRole ar", "ar");
    }

	public AccessRoleFinder setAccessFunction(String accessFunction){
		addJoin(prefixWithAlias("accessFunctionIds af"));
		final String property = "af";
		if (accessFunction == null)
			addNullFilter(property);
		else
			addEqualFilter(property, accessFunction);
		return this;
	}

    public AccessRoleFinder orderById(boolean ascending) {
		addSortOrder(prefixWithAlias("id"), ascending);
		return this;
	}

}
