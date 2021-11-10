package no.goodtech.vaadin.chart;

import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.ColumnGenerator;

/**
 * Fill a table column with the color found in the cell
 */
public class ColorColumnGenerator implements ColumnGenerator {
	
	private String column = "color";
	
	public ColorColumnGenerator(String column) {
		this.column = column;
	}

	public Object generateCell(Table source, Object itemId, Object columnId) {
		if (itemId instanceof IColorable) {
			final IColorable series = (IColorable) itemId;
			if (columnId.equals(column)) {
				CssLayout content = new CssLayout(new Label("-----")) {
			        @Override
			        public String getCss(Component c) {
						final Color color = series.getColor();
			            final String colorHex = color.toString();
						return "background: " + colorHex + ";" + "color: " + colorHex + ";";
			        }
			    };
			    return content;
			}
		}
		return null;
	}
}