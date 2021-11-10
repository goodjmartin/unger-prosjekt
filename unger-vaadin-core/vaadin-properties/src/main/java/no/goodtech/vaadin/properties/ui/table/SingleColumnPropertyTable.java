package no.goodtech.vaadin.properties.ui.table;

import com.vaadin.ui.Component;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.themes.Runo;
import no.goodtech.vaadin.properties.model.Property;

import java.util.Map;

/**
 * Properties table for brukt for søk på egenskaper
 */
public class SingleColumnPropertyTable extends Table {
	private final no.goodtech.vaadin.properties.ui.table.ColumnGenerator columnGenerator;

	public SingleColumnPropertyTable() {
		setImmediate(true);
		addStyleName(Runo.TABLE_SMALL);
		columnGenerator = new no.goodtech.vaadin.properties.ui.table.ColumnGenerator();
		addGeneratedColumn(SingleColumnPropertyTableContainer.ValueField, columnGenerator);
	}

	public Map<Property, Component> getSearchCriteria() {
		return columnGenerator.getSearchCriteria();
	}
}
