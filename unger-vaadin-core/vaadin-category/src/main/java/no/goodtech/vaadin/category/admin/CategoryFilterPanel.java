package no.goodtech.vaadin.category.admin;

import com.vaadin.data.HasValue;
import com.vaadin.data.provider.Query;
import no.goodtech.vaadin.category.CategoryFinder;
import no.goodtech.vaadin.category.Texts;
import no.goodtech.vaadin.search.FilterPanel;


public class CategoryFilterPanel extends FilterPanel<CategoryFinder> {

	private final CategoryOwnerComboBox categoryOwnerComboBox;

	public CategoryFilterPanel() {
		super();
		hideDisabledCheckbox();
		categoryOwnerComboBox = new CategoryOwnerComboBox(Texts.get("categoryFilterPanel.owner"));
		categoryOwnerComboBox.setEmptySelectionAllowed(false);
		addComponents(categoryOwnerComboBox);
		categoryOwnerComboBox.addValueChangeListener((HasValue.ValueChangeListener<String>) event -> {
			if (actionListener != null)
				search();
		});
	}

	@Override
	public CategoryFinder getFinder() {
		final CategoryFinder finder = new CategoryFinder();
		finder.setOwnerClass(categoryOwnerComboBox.getValue());
		return finder;
	}

	public String getSelectedOwnerClass() {
		return categoryOwnerComboBox.getValue();
	}

	public void setSelectedOwnerClass(String selectedOwnerClass) {
		categoryOwnerComboBox.setValue(selectedOwnerClass);
	}

	public void setOwnerClassComboBoxEnabled(boolean enabled) {
		categoryOwnerComboBox.setEnabled(!enabled);
	}

	public void selectFirstItem() {
		categoryOwnerComboBox.setValue(categoryOwnerComboBox.getDataProvider().fetch(new Query<>()).findFirst().orElse(null));
	}

	public void refresh(String url) {
		super.refresh(url);
		categoryOwnerComboBox.refresh();
	}
}
