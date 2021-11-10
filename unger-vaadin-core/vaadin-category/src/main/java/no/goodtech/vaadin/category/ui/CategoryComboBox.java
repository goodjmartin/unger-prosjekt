package no.goodtech.vaadin.category.ui;

import com.vaadin.v7.ui.ComboBox;
import no.goodtech.vaadin.category.Category;

/**
 * Single selection of categories
 */
public class CategoryComboBox extends ComboBox
{
	private final CategoryDataManager manager;
	
	public CategoryComboBox(Class<?> ownerClass) {
		this(ownerClass.getSimpleName());
	}

	public CategoryComboBox(String caption, Class<?> ownerClass) {
		this(caption, ownerClass.getSimpleName());
	}

	public CategoryComboBox(String owner) {
		this(null, owner);
	}	
	
	public CategoryComboBox(String caption, String owner) {
		super(caption);
		manager = new CategoryDataManager(owner, this);
		setNullSelectionAllowed(false);
	}

	public Category getCurrentCategory(){
		return (Category) getValue();
	}

	public void refresh() {
		manager.refresh();
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
}
