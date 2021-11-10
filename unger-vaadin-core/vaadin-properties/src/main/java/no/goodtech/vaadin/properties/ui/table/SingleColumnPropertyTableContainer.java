package no.goodtech.vaadin.properties.ui.table;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import no.goodtech.vaadin.properties.model.PropertyStub;

import java.util.List;

/**
 * Container for enkeltkolonnetabellen
 */
public class SingleColumnPropertyTableContainer extends IndexedContainer {
	public static final String ValueField = "ID";

	public SingleColumnPropertyTableContainer() {
		addContainerProperty(ValueField, String.class, null);
	}

	public void refreshPropertyTableContainer(List<PropertyStub> properties) {
		removeAllItems();
		for (PropertyStub property : properties) {
			final Item item = addItem(property);
			item.getItemProperty(ValueField).setValue(property.getId());
		}
	}
}