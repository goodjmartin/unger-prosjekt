package no.goodtech.vaadin.category.ui;

import com.vaadin.v7.ui.OptionGroup;
import no.goodtech.vaadin.category.Category;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create an option group that contains statuses owned by one specific class
 * Use {@link #refresh()} to fill it with data from the database
 */
public class CategoryOptionGroup extends OptionGroup {

	private final CategoryDataManager manager;

	public CategoryOptionGroup(Class<?> ownerClass) {
		this(ownerClass.getSimpleName());
	}

	public CategoryOptionGroup(String caption, Class<?> ownerClass) {
		this(caption, ownerClass.getSimpleName());
	}

	public CategoryOptionGroup(String caption, String owner) {
		super(caption);
		manager = new CategoryDataManager(owner, this);
		setNullSelectionAllowed(false);
		addStyleName("statusOptionGroup");
		addStyleName("statusOptionGroup-" + owner);		
	}	

	public CategoryOptionGroup(String owner) {
		this(null, owner);
	}	

	public void refresh() {
		manager.refresh();
	}
	
	public Collection<Category> getValues() {
		final Object value = getValue();
		if (isMultiSelect())
			return (Collection<Category>) (value);
		
		Collection<Category> values = new ArrayList<Category>();
		if (value != null)
			values.add((Category) value);
		return values;
	}

	/**
	 * Select the category with given ID 
	 * @param id the ID of the category you want to select, if found. If not found nothing happens
	 */
	public void select(String id) {
		for (Object item : getItemIds()) {
			Category category = (Category) item;
			if (category.getId().equals(id)) {
				select(item);
				return;
			}
		}
	}
	
	/**
	 * Select the categories with given IDs 
	 * @param ids the IDs of the categories you want to select, if found. If not found nothing happens
	 */
	public void select(List<String> ids) {
		for (String id : ids)
			select(id);
	}

	/**
	 * @return a list of all the selected statuses.
	 */
	public List<Long> getSelectedStatuses() {
		List<Long> list = new ArrayList<>();
		Collection<Category> selectedStatuses = getValues();

		for (Category status : selectedStatuses) {
			list.add(status.getPk());
		}
		return list;
	}
}
