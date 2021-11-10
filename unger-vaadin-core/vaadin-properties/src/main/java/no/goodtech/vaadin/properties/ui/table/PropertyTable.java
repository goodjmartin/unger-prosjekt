package no.goodtech.vaadin.properties.ui.table;

import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.themes.Runo;


/**
 * Egenskapstabell for egenskaper
 */
public class PropertyTable extends Table {
	public PropertyTable() {
		setImmediate(true);
		setStyleName(Runo.TABLE_SMALL);
		setWidth(100, Unit.PERCENTAGE);
		setPageLength(0);

		setColumnExpandRatio("property.id", 15);
		setColumnExpandRatio("propertyValueField", 30);
	}
}
