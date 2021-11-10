package no.goodtech.vaadin.properties.ui.membership;

import com.vaadin.data.ValueProvider;
import com.vaadin.ui.Label;
import com.vaadin.ui.renderers.ComponentRenderer;
import no.goodtech.vaadin.lists.MessyGrid;
import no.goodtech.vaadin.properties.model.PropertyMembershipStub;
import no.goodtech.vaadin.properties.ui.Texts;
import no.goodtech.vaadin.utils.OkOrNotLabel;

public class PropertyMembershipGrid extends MessyGrid<PropertyMembershipStub> {
	public PropertyMembershipGrid() {
		super();
		setSelectionMode(SelectionMode.SINGLE);
		addColumn((ValueProvider<PropertyMembershipStub, Integer>) PropertyMembershipStub::getIndexNo).setStyleGenerator(item -> "v-align-right").setCaption(getCaption("indexNo"));
		addColumn((ValueProvider<PropertyMembershipStub, Label>) propertyMembership -> new OkOrNotLabel(propertyMembership.isEditable())).setRenderer(new ComponentRenderer()).setStyleGenerator(item -> "v-align-center").setCaption(getCaption("editable")).setMinimumWidth(120);
		addColumn((ValueProvider<PropertyMembershipStub, Label>) propertyMembership -> new OkOrNotLabel(propertyMembership.isRequired())).setRenderer(new ComponentRenderer()).setStyleGenerator(item -> "v-align-center").setCaption(getCaption("required")).setMinimumWidth(120);
		addColumn((ValueProvider<PropertyMembershipStub, Label>) propertyMembership -> new OkOrNotLabel(propertyMembership.isShowInCrosstab())).setRenderer(new ComponentRenderer()).setStyleGenerator(item -> "v-align-center").setCaption(getCaption("showInCrosstab")).setMinimumWidth(140);
		addColumn((ValueProvider<PropertyMembershipStub, String>) propertyMembership -> propertyMembership.getProperty().getName()).setCaption(getCaption("property.name")).setExpandRatio(1);
		addColumn((ValueProvider<PropertyMembershipStub, String>) propertyMembership -> propertyMembership.getProperty().getId()).setHidable(true).setHidden(true).setCaption(getCaption("property.id")).setExpandRatio(1);
		addColumn((ValueProvider<PropertyMembershipStub, String>) propertyMembership -> propertyMembership.getPropertyClass().getDescription()).setHidden(true).setHidable(true).setCaption(getCaption("propertyClass.description")).setExpandRatio(1);
		addColumn((ValueProvider<PropertyMembershipStub, String>) propertyMembership -> propertyMembership.getPropertyClass().getId()).setHidden(true).setHidable(true).setCaption(getCaption("propertyClass.id")).setExpandRatio(1);
	}

	private static String getCaption(String key) {
		return Texts.get("property.membership.table.column." + key);
	}
}
