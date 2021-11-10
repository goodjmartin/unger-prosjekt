package no.goodtech.vaadin.properties.ui;

import com.vaadin.data.provider.Query;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ItemCaptionGenerator;
import no.goodtech.vaadin.properties.model.PropertyFinder;
import no.goodtech.vaadin.properties.model.PropertyStub;

import java.util.List;
import java.util.stream.Collectors;

public class PropertyComboBox extends ComboBox<PropertyStub> {

	public PropertyComboBox(String caption) {
		super(caption);
		setItemCaptionGenerator((ItemCaptionGenerator<PropertyStub>) PropertyStub::getName);
	}

	public void refresh() {
		refresh(new PropertyFinder().setOrderByName().list());
	}
	
	public void refresh(List<PropertyStub> properties ) {
		setItems(properties);
	}
	
	/**
	 * Select the item with given ID 
	 * @param id the ID of the item you want to select, if found. If not found nothing happens
	 */
	public void select(String id) {
		for (PropertyStub item : getDataProvider().fetch(new Query<>()).collect(Collectors.toList())) {
			if (item.getId().equals(id)) {
				setValue(item);
				return;
			}
		}
	}
}
