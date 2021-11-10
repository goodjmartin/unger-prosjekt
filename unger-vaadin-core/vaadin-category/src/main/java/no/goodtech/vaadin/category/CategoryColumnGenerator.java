package no.goodtech.vaadin.category;


import com.vaadin.addon.tableexport.v7.ExportableColumnGenerator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.Table;

import java.util.HashSet;
import java.util.Set;

/**
 * Creates special columns for displaying values of type category
 */
public class CategoryColumnGenerator implements ExportableColumnGenerator {

	private final Set<String> styles = new HashSet<>();
	private final Table table;

	/**
	 * How to display status
	 */
	public enum Type {
		ICON_ONLY,
		TEXT_ONLY,
		ICON_AND_TEXT
	}
	
	private final String columnId;
	private final Type type;
	
	/**
	 * Create a column generator for given column. Use this constructor if the column value need to be exported.
	 *
	 * @param table the table (need by the ExportableColumnGenerator method)
	 * @param columnId column to look for
	 */
	public CategoryColumnGenerator(final Table table, final String columnId, final Type type) {
		this.table = table;
		this.columnId = columnId;
		this.type = type;
	}

	/**
	 * Create a column generator for given column
	 * @param columnId column to look for
	 */
	public CategoryColumnGenerator(final String columnId, final Type type) {
		this(null, columnId, type);
	}

	@Override
	public Object generateCell(Table source, Object itemId, Object columnId) {
		Category category = getCategory(source, itemId, columnId);
		if (category != null) {
			switch (type) {
			case ICON_ONLY:
				return createIconLabel(category, false);
			case TEXT_ONLY:
				return category.getName();
			case ICON_AND_TEXT:
				return createIconLabel(category, true);
			}
		}
		return null;
	}

	private Category getCategory(Table source, Object itemId, Object columnId) {
		if (this.columnId.equals(columnId)) {
			final Property<?> itemProperty = source.getItem(itemId).getItemProperty(columnId);
			if (itemProperty != null) {
				return (Category) itemProperty.getValue();
			}
		}
		return null;
	}

	/**
	 * Creates the icon-label for each category. Adds a style to it as well.
	 * @param category the category
	 * @return the label
	 */
	private HorizontalLayout createIconLabel(final Category category, final boolean includeText) {
		if (category == null)
			return null;

		final FontAwesome icon = no.goodtech.vaadin.utils.Utils.findIcon(category.getIconName());
		if (icon == null)
			return null;

		// Create horizontal layout
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setMargin(false);

		// Create icon label
		Label iconLabel = new Label(icon.getHtml());
		iconLabel.setContentMode(ContentMode.HTML);
		iconLabel.addStyleName(category.getStyleName());
		horizontalLayout.addComponent(iconLabel);

		// Create icon color style
		addIconColorStyle(category);

		// Add text if specified
		if (includeText) {
			Label textLabel = new Label(category.getName());
			horizontalLayout.addComponent(textLabel);
		}

		return horizontalLayout;
	}

	/**
	 * Make sure that we store only one colorstyle for the category
	 *
	 * @param category The category
	 */
	private void addIconColorStyle(final Category category) {
		if (!styles.contains(category.getId())) {
			styles.add(category.getId());
			Utils.addIconColorStyle(category);
		}
	}

	@Override
	public Property getGeneratedProperty(final Object itemId, final Object columnId) {
		if (table != null) {
			final Category category = getCategory(table, itemId, columnId);
			if (category != null)
				return new ObjectProperty<>(category.getName());
		}
		return null;
	}

	@Override
	public Class<?> getType() {
		return String.class;
	}

}