package no.goodtech.vaadin.properties.ui.v7;

import com.vaadin.v7.ui.ComboBox;
import no.goodtech.vaadin.properties.model.PropertyFinder;
import no.goodtech.vaadin.properties.model.PropertyStub;

import java.util.List;

/**
 * @deprecated
 * @see no.goodtech.vaadin.properties.ui.PropertyComboBox
 */
public class PropertyComboBox extends ComboBox {

	public PropertyComboBox(String caption) {
		super(caption);
	}

	public void refresh() {
		refresh(new PropertyFinder().setOrderByName().list());
	}

	public void refresh(List<PropertyStub> properties) {
		removeAllItems();
		for (PropertyStub property : properties) {
			addItem(property);
			setItemCaption(property, property.getName());
		}
	}

	public PropertyStub getValue() {
		return (PropertyStub) (super.getValue());
	}

	/**
	 * Select the item with given ID
	 *
	 * @param id the ID of the item you want to select, if found. If not found nothing happens
	 */
	public void select(String id) {
		for (Object item : getItemIds()) {
			PropertyStub category = (PropertyStub) item;
			if (category.getId().equals(id)) {
				select(item);
				return;
			}
		}
	}
}
