package no.goodtech.vaadin.properties.ui.table;

import com.vaadin.ui.Component;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.ui.AbstractSelect;
import no.goodtech.vaadin.properties.model.Property;

/**
 * Displays descriptions as a caption of the description row
 */
public class PropertyValueDescriptionGenerator implements AbstractSelect.ItemDescriptionGenerator {
	@Override
	public String generateDescription(Component source, Object itemId, Object propertyId) {
		if (propertyId != null && propertyId == PropertyAndValueBean.Fields.PROPERTY_VALUE_PREFIX + Property.Fields.DESCRIPTION) {
			PropertyValueTable propertyValueTable = (PropertyValueTable) source;
			@SuppressWarnings("unchecked")
			BeanItem<PropertyAndValueBean> propertyAndValueBean = (BeanItem<PropertyAndValueBean>) propertyValueTable.getItem(itemId);
			if (propertyAndValueBean != null && propertyAndValueBean.getBean().getPropertyValue() != null) {
				return propertyAndValueBean.getBean().getPropertyValue().getDescription();
			}
		}
		return null;
	}
}
