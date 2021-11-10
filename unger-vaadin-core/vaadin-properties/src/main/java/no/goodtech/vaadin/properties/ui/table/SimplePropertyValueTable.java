package no.goodtech.vaadin.properties.ui.table;

import com.vaadin.v7.data.util.BeanItemContainer;
import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.vaadin.lists.v7.IMessyTableActionListener;
import no.goodtech.vaadin.lists.v7.MessyTable;
import no.goodtech.vaadin.properties.model.PropertyStub;
import no.goodtech.vaadin.properties.model.PropertyValue;
import no.goodtech.vaadin.properties.model.PropertyValueFinder;
import no.goodtech.vaadin.properties.ui.PropertyValueFormatter;
import no.goodtech.vaadin.properties.ui.Texts;

import java.util.List;

/**
 * Shows property values in a table.
 * The values are formatted as strings
 */
public class SimplePropertyValueTable extends MessyTable<PropertyValue> {

	public static final String VALUE_COLUMN = "value";

	public SimplePropertyValueTable() {
		this(null);
	}

	public SimplePropertyValueTable(IMessyTableActionListener<PropertyValue> actionListener) {
		super(new BeanItemContainer<PropertyValue>(PropertyValue.class), actionListener, false);
		container.addNestedContainerBean("property");

		setVisibleColumns("property.name", VALUE_COLUMN, "property.unitOfMeasure");
		for (Object column : getVisibleColumns())
			setColumnHeader(column, Texts.get("simplePropertyValueTable.column.caption." + (String) column));

		setColumnAlignment(VALUE_COLUMN, Align.RIGHT);
	}

	public void refresh(EntityStub<?> owner) {
		final List<PropertyValue> propertyValues = new PropertyValueFinder().setOwner(owner).loadList();
		refresh(propertyValues);
		setPageLength(propertyValues.size());
	}

	@Override
	protected String formatPropertyValue(Object rowId, Object columnId, com.vaadin.v7.data.Property<?> tableProperty) {
		PropertyValue propertyValue = (PropertyValue) rowId;
		if (VALUE_COLUMN.equals(columnId)) {
			PropertyStub property = propertyValue.getProperty();
			return PropertyValueFormatter.formatValue(propertyValue, property);
		}
		return super.formatPropertyValue(rowId, columnId, tableProperty);
	}
}
