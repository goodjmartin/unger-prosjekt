package no.goodtech.vaadin.properties.ui;

import java.util.Collections;
import java.util.Locale;

import no.goodtech.persistence.entity.AbstractEntityImpl;
import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.vaadin.properties.model.Property;
import no.goodtech.vaadin.properties.model.PropertyValue;
import no.goodtech.vaadin.properties.model.PropertyValues;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.ui.Link;

public class PropertyValueColumnGeneratorTest {

	public PropertyValueColumnGeneratorTest() {
		Locale.setDefault(new Locale("nb", "NO"));
	}

	@Test
	public void testGenerateCell() {
		Property stringProperty = createProperty("stringProperty", String.class);
		Property booleanProperty = createProperty("booleanProperty", Boolean.class);
		Property linkProperty = createProperty("linkProperty", Link.class);
		Owner owner1 = new Owner(1L);
		
		final PropertyValue stringValue = new PropertyValue("stringValue", stringProperty, owner1);
		final PropertyValue emptyStringValue = new PropertyValue(stringProperty, owner1);
		final PropertyValue falseBooleanValue = new PropertyValue("false", booleanProperty, owner1);
		final PropertyValue trueBooleanValue = new PropertyValue("true", booleanProperty, owner1);
		final PropertyValue emptyBooleanValue = new PropertyValue(booleanProperty, owner1);
		final PropertyValue urlValue = new PropertyValue("http://goodtech.no/", linkProperty, owner1);
		
		Assert.assertEquals("stringValue", createColumnGenerator(stringValue).generateCell(null, owner1, new PropertyValueColumn(stringProperty)));
		Assert.assertEquals("", createColumnGenerator(emptyStringValue).generateCell(null, owner1, new PropertyValueColumn(stringProperty)));
		Assert.assertEquals("Nei", createColumnGenerator(falseBooleanValue).generateCell(null, owner1, new PropertyValueColumn(booleanProperty)));
		Assert.assertEquals("Ja", createColumnGenerator(trueBooleanValue).generateCell(null, owner1, new PropertyValueColumn(booleanProperty)));
		Assert.assertEquals("", createColumnGenerator(emptyBooleanValue).generateCell(null, owner1, new PropertyValueColumn(booleanProperty)));

		final Object linkWidget = createColumnGenerator(urlValue).generateCell(null, owner1, new PropertyValueColumn(linkProperty));
		Assert.assertEquals(Link.class, linkWidget.getClass());
	}

	private PropertyValueColumnGenerator createColumnGenerator(final PropertyValue propertyValue) {
		return new PropertyValueColumnGenerator(new PropertyValues(Collections.singletonList(propertyValue)));
	}

	private Property createProperty(String id, Class<?> type) {
		final Property property = new Property(id);
		property.setDataType(type);
		return property;
	}
	
	private static class Owner extends AbstractEntityImpl<Owner> implements EntityStub<Owner> {

		public Owner(long pk) {
			super(pk);
		}
	}
}
