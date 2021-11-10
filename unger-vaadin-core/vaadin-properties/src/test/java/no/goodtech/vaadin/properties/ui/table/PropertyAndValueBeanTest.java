package no.goodtech.vaadin.properties.ui.table;

import com.vaadin.v7.ui.TextField;
import no.goodtech.vaadin.properties.model.Property;
import no.goodtech.vaadin.properties.model.PropertyValue;
import org.junit.Assert;
import org.junit.Test;

public class PropertyAndValueBeanTest {

	/**
	 * Tester at tall blir formatert riktig 
	 */
	@Test
	public void testNumericFormatting() {
		Property doubleProperty = createProperty(Double.class);
		PropertyValue doublePropertyValue = doubleProperty.createValue();
		doublePropertyValue.setValue(new Double(1003.14));
		
		Property longProperty = createProperty(Long.class);
		PropertyValue longPropertyValue = longProperty.createValue();
		longPropertyValue.setValue(1003L);

		Property stringProperty = createProperty(String.class);
		PropertyValue stringPropertyValue = stringProperty.createValue();
		stringPropertyValue.setValue("jadda");

		
		Assert.assertEquals("1003.14", createField(doublePropertyValue, doubleProperty).getValue());
		Assert.assertEquals("1003", createField(longPropertyValue, longProperty).getValue());
		Assert.assertEquals("jadda", createField(stringPropertyValue, stringProperty).getValue());
	}

	private Property createProperty(Class<?> type) {
		Property property = new Property();
		property.setDataType(type);
		return property;
	}
	
	private TextField createField(PropertyValue propertyValue, Property property) {
		PropertyAndValueBean bean = new PropertyAndValueBean(property);
		bean.setPropertyValue(propertyValue);
		return (TextField) bean.getPropertyValueField();
	}

}
