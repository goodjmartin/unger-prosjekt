package no.goodtech.vaadin.properties.ui.admin;

import com.vaadin.data.ValueProvider;
import no.goodtech.persistence.entity.AbstractSimpleEntityImpl;
import no.goodtech.vaadin.lists.MessyGrid;
import no.goodtech.vaadin.properties.model.Property;
import no.goodtech.vaadin.properties.ui.Texts;

public class PropertyGrid extends MessyGrid<Property> {
	public PropertyGrid() {
		addColumn((ValueProvider<Property, Long>) AbstractSimpleEntityImpl::getPk).setCaption(getCaption(Property.Fields.PK)).setHidden(true).setHidable(true);
		addColumn((ValueProvider<Property, String>) Property::getId).setCaption(getCaption(Property.Fields.ID));
		addColumn((ValueProvider<Property, String>) Property::getName).setCaption(getCaption(Property.Fields.NAME));
		addColumn((ValueProvider<Property, String>) property -> Property.getNiceDatatypeName(property.getDataType())).setCaption(getCaption(Property.Fields.DATATYPE));
		addColumn((ValueProvider<Property, String>) Property::getUnitOfMeasure).setCaption(getCaption(Property.Fields.UNIT_OF_MEASURE));
		addColumn((ValueProvider<Property, String>) Property::getFormatPattern).setCaption(getCaption(Property.Fields.FORMAT_PATTERN));
		addColumn((ValueProvider<Property, String>) Property::getOptions).setCaption(getCaption(Property.Fields.OPTIONS));
		addColumn((ValueProvider<Property, String>) Property::getDescription).setCaption(getCaption(Property.Fields.DESCRIPTION));
	}

	private String getCaption(String key){
		return Texts.get("propertyTable.column.header." + key);
	}
}
