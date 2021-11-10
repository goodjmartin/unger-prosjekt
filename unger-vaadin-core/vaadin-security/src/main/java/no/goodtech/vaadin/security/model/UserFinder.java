package no.goodtech.vaadin.security.model;

import java.util.Set;

import no.goodtech.persistence.jpa.AbstractFinder;
import no.goodtech.persistence.search.SearchFilterGroup.GroupingOperator;
import no.goodtech.vaadin.search.FilterPanel.IMaxRowsAware;

public class UserFinder extends AbstractFinder<User, UserStub, UserFinder> implements IMaxRowsAware {

    private static final long serialVersionUID = 1L;

    public UserFinder() {
		super("select distinct u from User u", "u");
       //super("select select u from User u", "u");
     //super("select u from User u left join fetch u.accessRoles ar", "u");
    }

    /**
     * Free text search by ID, name or email.
     * Please provide this filter as last filter because of OR
     */
    public UserFinder setIdOrNameOrEmail(String idOrNameOrEmail) {
    	addFilterGroup(GroupingOperator.OR);
    	addLikeFilter(prefixWithAlias("id"), idOrNameOrEmail, true);
    	addLikeFilter(prefixWithAlias("name"), idOrNameOrEmail, true);
    	addLikeFilter(prefixWithAlias("email"), idOrNameOrEmail, true);
    	return this;
    }
    
    public UserFinder orderById(boolean ascending) {
    	addSortOrder(prefixWithAlias("id"), ascending);
        return this;
    }

    public UserFinder setAccessRole(final String ... accessRoleId){
		addJoin("u.accessRoles ar");
		addInFilter("ar.id", ((Object[])accessRoleId));
		return this;
	}
    
    public UserFinder setAccessRoles(final Set<? extends AccessRoleStub> accessRoles){
		addJoin("u.accessRoles ar");
		addInFilter("ar.pk", getPkList(accessRoles));
		return this;
	}

	/**
	 * Find all users with a given password.
	 * This should only be used to migrate users towards better password encryption.
	 */
	public UserFinder setPassword(String password){
		addEqualFilter(prefixWithAlias("password"), password);
		return this;
	}
}
