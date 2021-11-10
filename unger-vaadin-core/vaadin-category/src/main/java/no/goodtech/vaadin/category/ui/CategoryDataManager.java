package no.goodtech.vaadin.category.ui;

import com.vaadin.server.FontAwesome;
import com.vaadin.v7.ui.AbstractSelect;
import no.goodtech.vaadin.category.Category;
import no.goodtech.vaadin.category.CategoryFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CategoryDataManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(CategoryDataManager.class);
	private final String owner;
	private final AbstractSelect select;

	
	public CategoryDataManager(String owner, AbstractSelect select) {
		this.owner = owner;
		this.select = select;
	}

	public void refresh() {
		select.removeAllItems();
		for (Category category : new CategoryFinder(owner).list()) {
			select.addItem(category);
			select.setItemCaption(category, category.getName());
			final String iconName = category.getIconName();
			if (iconName != null) {
				final FontAwesome icon = no.goodtech.vaadin.utils.Utils.findIcon(iconName);
				if (icon != null)
					select.setItemIcon(category, icon);
				else
					LOGGER.warn("Icon '{}' not found in FontAwesome, category id = '{}'", iconName, category.getId());

				setColor(category);
			}
		}
	}

	private void setColor(Category category) {
//TODO: Individial color of each item doesn't work beacuse items in a select/optiongroup can't be styled individually
//TODO: Wait for http://dev.vaadin.com/ticket/14076
//		Utils.addIconColorStyle(category);
	}
}
