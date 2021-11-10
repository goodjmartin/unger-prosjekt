package no.goodtech.vaadin.lists.v7;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.FontIcon;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.ColumnGenerator;

import java.util.HashMap;
import java.util.Map;

/**
 * Shows boolean values as icons in a table 
 */
@Deprecated
public class BooleanIconColumnGenerator implements ColumnGenerator {

	private Map<Boolean, FontIcon> icons = new HashMap<Boolean, FontIcon>();
	
	/**
	 * Shows true values as an icon for given columns
	 */
	public BooleanIconColumnGenerator() {
		this(null, FontAwesome.CHECK_SQUARE_O);
	}
	
	/**
	 * Shows boolean values as icon for given columns
	 * @param falseIcon icon for false values
	 * @param trueIcon icon for true values
	 */
	public BooleanIconColumnGenerator(FontIcon falseIcon, FontIcon trueIcon) {
		icons.put(false, falseIcon);
		icons.put(true, trueIcon);
	}
	
	public Object generateCell(Table source, Object itemId, Object columnId) {
		Property<?> property = source.getItem(itemId).getItemProperty(columnId);
        if(property == null || property.getValue() == null)
        	return null;

        Boolean cellValue = (Boolean) property.getValue();
		return getIcon(cellValue);
	}
	
	public Label getIcon(Boolean value) {
		if (value == null)
			return null;
		
		final FontIcon fontIcon = icons.get(value);
		if (fontIcon == null)
			return null;
		
		return new Label(fontIcon.getHtml(), ContentMode.HTML);
	}
}
