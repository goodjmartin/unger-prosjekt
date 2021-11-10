package no.goodtech.vaadin.category.ui;

import com.vaadin.server.FontAwesome;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.renderers.HtmlRenderer;
import no.goodtech.vaadin.category.Category;
import no.goodtech.vaadin.category.Utils;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Show category name and/or icon in a grid column
 * Usage:
 *
 * CategoryColumnRenderer.apply(grid.getColumn(MaterialSubLot.Fields.CATEGORY))
 *
 * OR
 *
 * CategoryColumnRenderer statusRenderer = new CategoryColumnRenderer(CategoryColumnRenderer.Type.ICON_AND_TEXT);
 * grid.getColumn(MaterialSubLot.Fields.CATEGORY).setRenderer(statusRenderer, statusRenderer);
 */
public class CategoryColumnRenderer extends HtmlRenderer
		implements Converter<String, Category>, Grid.CellStyleGenerator {

	/**
	 * How to display status
	 */
	public enum Type {
		ICON_ONLY,
		TEXT_ONLY,
		ICON_AND_TEXT
	}

	protected final Type type;
	protected final Set<String> styles = new HashSet<>();

	/**
	 * Create renderer with given type
	 */
	public CategoryColumnRenderer(Type type) {
		super("");
		this.type = type;
	}

	/**
	 * Apply a renderer to given column that shows both icon and category name
	 * @return the renderer
	 */
	public static CategoryColumnRenderer apply(final Grid.Column column) {
		return CategoryColumnRenderer.apply(column, Type.ICON_AND_TEXT);
	}

	/**
	 * Apply a renderer to given column
	 * @return the renderer
	 */
	public static CategoryColumnRenderer apply(final Grid.Column column, Type type) {
		CategoryColumnRenderer renderer = new CategoryColumnRenderer(type);
		if (column != null) {
			column.setRenderer(renderer, renderer);
		}
		return renderer;
	}

	public Converter<String, Category> getConverter() {
		return this;
	}

	@Override
	public Category convertToModel(String s, Class<? extends Category> aClass, Locale locale) throws ConversionException {
		return null; //not implemented;
	}

	@Override
	public String convertToPresentation(Category category, Class<? extends String> aClass, Locale locale) throws ConversionException {
		if (category != null) {
			switch (type) {
				case TEXT_ONLY:
					return category.getName();
				case ICON_ONLY:
					return createIconLabel(category, false);
				case ICON_AND_TEXT:
					return createIconLabel(category, true);
			}
		}
		return null;
	}

	/**
	 * Creates the icon-label for each category. Adds a style to it as well.
	 * @param category the category
	 * @return the label
	 */
	private String createIconLabel(final Category category, final boolean includeText) {
		if (category == null)
			return null;

		final FontAwesome icon = no.goodtech.vaadin.utils.Utils.findIcon(category.getIconName());
		if (icon == null)
			return null;

		addIconColorStyle(category); // Create icon color style

		String result = icon.getHtml();
		if (includeText) {
			result += " " + category.getName(); // Add text if specified
		}
		return result;
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
	public Class<Category> getModelType() {
		return Category.class;
	}

	@Override
	public String getStyle(Grid.CellReference cellReference) {
		Category category = (Category) cellReference.getValue();
		if (category != null) {
			return category.getStyleName();
		}
		return null;
	}
}
