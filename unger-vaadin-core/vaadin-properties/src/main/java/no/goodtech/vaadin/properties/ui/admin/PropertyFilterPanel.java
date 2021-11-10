package no.goodtech.vaadin.properties.ui.admin;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.TextField;
import no.goodtech.vaadin.UrlUtils;
import no.goodtech.vaadin.properties.model.PropertyClassFinder;
import no.goodtech.vaadin.properties.model.PropertyClassStub;
import no.goodtech.vaadin.properties.model.PropertyFinder;
import no.goodtech.vaadin.properties.ui.Texts;
import no.goodtech.vaadin.search.FilterPanel;

public class PropertyFilterPanel extends FilterPanel<PropertyFinder>{

	private final ComboBox<PropertyClassStub> propertyClassComboBox;
	private final TextField idTextField, nameTextField;

	public PropertyFilterPanel() {
		idTextField = createTextField("id");
		nameTextField = createTextField("name");
		propertyClassComboBox = new ComboBox<>(Texts.get("property.membership.filter.propertyClass.caption"));
		propertyClassComboBox.setEmptySelectionAllowed(true);
		propertyClassComboBox.setItemCaptionGenerator((ItemCaptionGenerator<PropertyClassStub>) PropertyClassStub::getDescription);
		addComponents(propertyClassComboBox, idTextField, nameTextField);
	}

	@Override
	public PropertyFinder getFinder() {
		PropertyFinder finder = new PropertyFinder().setOrderByName();
		if (propertyClassComboBox.getValue() != null)
			finder.setPropertyClassId(propertyClassComboBox.getValue().getId());
		if (idTextField.getValue() != null && !"".equals(idTextField.getValue()))
			finder.setId(idTextField.getValue());
		if (nameTextField.getValue() != null && !"".equals(nameTextField.getValue()))
			finder.setName(nameTextField.getValue());
		return finder;
	}
	
	@Override
	public void refresh(String url) {
		super.refresh(url);
		propertyClassComboBox.setItems(new PropertyClassFinder().setOrderByDescription().list());
		UrlUtils utils = new UrlUtils(url);
		if (utils.getParameter("id") != null)
			idTextField.setValue(utils.getParameter("id"));
		if (utils.getParameter("name") != null)
			nameTextField.setValue(utils.getParameter("name"));
	}
	
	private TextField createTextField(String name) {
		TextField field = new TextField(Texts.get("propertyFilterPanel." + name + ".caption"));
		field.setDescription(Texts.get("propertyFilterPanel." + name + ".description"));
		return field;
	}
}
