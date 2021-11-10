package no.goodtech.vaadin.category;

import no.goodtech.persistence.jpa.AbstractFinder;
import no.goodtech.vaadin.search.FilterPanel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Use this to search for statuses of relevant type
 */
public class CategoryFinder extends AbstractFinder<Category, Category, CategoryFinder>
		implements FilterPanel.IMaxRowsAware {

	public CategoryFinder(Class<?> ownerClass) {
		this(ownerClass.getSimpleName());
	}

	public CategoryFinder() {
		super("select s from Category s", "s");
	}

	public CategoryFinder(String owner) {
		super("select s from Category s", "s");
		if (owner != null)
			addEqualFilter(prefixWithAlias("owner"), owner);
		addSortOrder(prefixWithAlias("indexNo"), true);
	}

	public CategoryFinder setId(String id) {
		addEqualFilter(prefixWithAlias("id"), id);
		return this;
	}


	public CategoryFinder setOwner(String owner) {
		if (owner != null)
			addEqualFilter(prefixWithAlias("owner"), owner);
		addSortOrder(prefixWithAlias("indexNo"), true);

		return this;
	}

	public CategoryFinder setOwnerClass(String ownerClass) {
		addInFilter(prefixWithAlias("owner"), ownerClass);
		addSortOrder(prefixWithAlias("indexNo"), true);

		return this;
	}

	public List<String> listOwners() {
		Set<String> owners = new HashSet<>();
		for (Category cat : list()) {
			owners.add(cat.getOwner());
		}
		List<String> list = new ArrayList<>(owners);
		return list;
	}


}