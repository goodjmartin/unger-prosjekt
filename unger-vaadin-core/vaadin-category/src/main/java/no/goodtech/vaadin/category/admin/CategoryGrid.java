package no.goodtech.vaadin.category.admin;

import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.ColorPickerArea;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import no.goodtech.persistence.entity.AbstractSimpleEntityImpl;
import no.goodtech.vaadin.category.Category;
import no.goodtech.vaadin.category.Texts;
import no.goodtech.vaadin.lists.MessyGrid;

public class CategoryGrid extends MessyGrid<Category> {
	public CategoryGrid() {
		addColumn((ValueProvider<Category, Long>) AbstractSimpleEntityImpl::getPk).setCaption(Texts.get("category." + Category.Fields.PK)).setHidden(true).setHidable(true);
		addColumn((ValueProvider<Category, String>) Category::getId).setCaption(Texts.get("category." + Category.Fields.ID)).setMinimumWidth(90);
		addColumn((ValueProvider<Category, String>) Category::getName).setCaption(Texts.get("category." + Category.Fields.NAME)).setMinimumWidth(190);
		addColumn((ValueProvider<Category, String>) Category::getDescription).setCaption(Texts.get("category." + Category.Fields.DESCRIPTION)).setExpandRatio(1);
		addColumn((ValueProvider<Category, String>) category -> getIconAsHtml(category.getIconName())).setRenderer(new HtmlRenderer()).setStyleGenerator((StyleGenerator<Category>) item -> "v-align-center").setCaption(Texts.get("category." + Category.Fields.ICON_NAME));
		addColumn((ValueProvider<Category, ColorPickerArea>) category -> (category.getColor() != null) ? getColorPickerArea(category.getColor()) : null).setCaption(Texts.get("category." + Category.Fields.COLOR)).setRenderer(new ComponentRenderer()).setMinimumWidth(80);
	}

	private ColorPickerArea getColorPickerArea(Integer rgb){
		ColorPickerArea colorPickerArea = new ColorPickerArea(null, new Color(rgb));
		colorPickerArea.setReadOnly(true);
		return colorPickerArea;
	}

	/**
	 * Fetch icon as html from VaadinIcons or FontAwesome
	 */
	private String getIconAsHtml(String name){
		if (name != null && !name.isEmpty()){
			try {
				return VaadinIcons.valueOf(name).getHtml();
			}catch (IllegalArgumentException e){
				try {
					return FontAwesome.valueOf(name).getHtml();
				}catch (IllegalArgumentException ignored){
				}
			}
		}
		return null;
	}
}
