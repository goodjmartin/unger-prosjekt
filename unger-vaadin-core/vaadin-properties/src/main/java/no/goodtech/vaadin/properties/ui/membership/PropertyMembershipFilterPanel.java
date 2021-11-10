package no.goodtech.vaadin.properties.ui.membership;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ItemCaptionGenerator;
import no.goodtech.vaadin.properties.model.PropertyClassFinder;
import no.goodtech.vaadin.properties.model.PropertyClassStub;
import no.goodtech.vaadin.properties.model.PropertyMembershipFinder;
import no.goodtech.vaadin.properties.ui.Texts;
import no.goodtech.vaadin.search.FilterPanel;

import java.util.List;

public class PropertyMembershipFilterPanel extends FilterPanel<PropertyMembershipFinder> {

	private final ComboBox<PropertyClassStub> propertyClassComboBox;

	public PropertyMembershipFilterPanel() {
		super();
		propertyClassComboBox = new ComboBox<>(Texts.get("property.membership.filter.propertyClass.caption"));
		propertyClassComboBox.setEmptySelectionAllowed(false);
		propertyClassComboBox.setItemCaptionGenerator((ItemCaptionGenerator<PropertyClassStub>) item -> (item.getDescription() != null) ? item.getDescription() : item.getId());
		addComponents(propertyClassComboBox);
	}

	@Override
	public PropertyMembershipFinder getFinder() {
		final PropertyMembershipFinder finder = new PropertyMembershipFinder();
		final PropertyClassStub propertyClass = getPropertyClass();
		if (propertyClass != null)
			finder.setPropertyClasses(propertyClass);
		return finder;
	}

	public void refresh() {
		List<PropertyClassStub> propertyClasses = new PropertyClassFinder().setOrderByDescription().list();
		propertyClassComboBox.setItems(propertyClasses);
		if (propertyClasses.size() > 0)
			propertyClassComboBox.setValue(propertyClasses.get(0));
	}

	/**
	 * @return selected group or null if none selected
	 */
	public PropertyClassStub getPropertyClass() {
		return propertyClassComboBox.getValue();
	}
}
