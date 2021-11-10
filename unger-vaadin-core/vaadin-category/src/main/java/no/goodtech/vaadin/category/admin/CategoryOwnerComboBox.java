package no.goodtech.vaadin.category.admin;

import com.vaadin.ui.ComboBox;
import no.goodtech.vaadin.category.CategoryFinder;

import java.util.List;

public class CategoryOwnerComboBox extends ComboBox<String> {

	public CategoryOwnerComboBox(String caption) {
		super(caption);
	}

	public void refresh() {
		refresh(new CategoryFinder().listOwners());
	}

	public void refresh(List<String> ownerClasses) {
		setItems(ownerClasses);
	}
}
