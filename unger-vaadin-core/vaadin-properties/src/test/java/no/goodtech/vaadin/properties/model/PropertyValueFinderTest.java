package no.goodtech.vaadin.properties.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test av {@link PropertyValueFinder}
 * @author oystein
 */
@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@Transactional
@ComponentScan("no.goodtech")
@ContextConfiguration(locations={"classpath*:goodtech-server.xml", "classpath:vaadin-properties-test.xml"})
public class PropertyValueFinderTest {

	/**
	 * Tester at man kan filtrere på eier
	 */
	@Test
	public void testFindByOwner() {
		PropertyOwner owner1 = new PropertyOwner("owner1").save();
		PropertyOwner owner2 = new PropertyOwner("owner2").save();
		Property property1 = new Property("p1").save();
		new PropertyValue("propertyValue1", property1, owner1).save();
		Assert.assertFalse(new PropertyValueFinder().setOwner(owner2).exists());
		Assert.assertEquals("propertyValue1", new PropertyValueFinder().setOwner(owner1).load().getValue());
	}

	/**
	 * Tester at man kan filtrere på flere eiere
	 */
	@Test
	public void testFindByOwners() {
		PropertyOwner owner1 = new PropertyOwner("owner1").save();
		PropertyOwner owner2 = new PropertyOwner("owner2").save();
		PropertyOwner owner3 = new PropertyOwner("owner3").save();
		Property property1 = new Property("p1").save();
		new PropertyValue("propertyValue1", property1, owner1).save();
		Assert.assertFalse(new PropertyValueFinder().setOwners(new ArrayList<>()).exists());
		Assert.assertFalse(new PropertyValueFinder().setOwners(Arrays.asList(owner2, owner3)).exists());
		Assert.assertTrue(new PropertyValueFinder().setOwners(Arrays.asList(owner1, owner3)).exists());
	}

	/**
	 * Tester at man kan filtrere på egenskap
	 */
	@Test
	public void testFindByProperty() {
		PropertyOwner owner1 = new PropertyOwner("owner1").save();
		Property property1 = new Property("p1").save();
		Property property2 = new Property("p2").save();
		new PropertyValue("propertyValue1", property1, owner1).save();
		Assert.assertFalse(new PropertyValueFinder().setProperty(property2).exists());
		Assert.assertEquals("propertyValue1", new PropertyValueFinder().setProperty(property1).load().getValue());
	}
	
	/**
	 * Tester at man kan finne arvede egenskaper
	 */
	@Test
	public void testFindInheritedValues() {
		PropertyOwner child1 = new PropertyOwner("child1").save();
		PropertyOwner parent1 = new PropertyOwner("parent1").save();
		PropertyOwner parent2 = new PropertyOwner("parent2").save();
		parent1.propertyHeirs.add(child1);
		parent2.propertyHeirs.add(child1);
		Property property1 = new Property("p1").save();
		Property property2 = new Property("p2").save();
		new PropertyValue("property1 arvet fra parent1", property1, parent1).save();
		new PropertyValue("property1 overstyrt av child1", property1, child1).save();
		new PropertyValue("property2 arvet fra parent2", property2, parent2).save();
		
		Assert.assertEquals("property1 arvet fra parent1",  new PropertyValueFinder().setOwner(parent1).load().getValue());
		Assert.assertEquals("property2 arvet fra parent2",  new PropertyValueFinder().setOwner(parent2).load().getValue());
		child1.propertySources.add(parent1);
		child1.propertySources.add(parent2);
		final List<PropertyValue> allPropertiesForChild1 = new PropertyValueFinder().setOwner(child1).setMayInheritFrom(child1.propertySources).list();
		Assert.assertEquals(2,  allPropertiesForChild1.size());
	}
	
	/**
	 * Tester at arvede egenskaper kan overstyres
	 */
	@Test
	public void testOverrideInheritedValues() {
		PropertyOwner child = new PropertyOwner("child").save();
		PropertyOwner parent = new PropertyOwner("parent").save();
		PropertyOwner grandParent = new PropertyOwner("grandParent").save();
		Property p1 = new Property("p1").save();
		Property p2 = new Property("p2").save();
		new PropertyValue("p1 eiet av grandParent", p1, grandParent).save();
		new PropertyValue("p2 eiet av grandParent", p2, grandParent).save();
		new PropertyValue("p1 overstyrt av parent", p1, parent).save();
		new PropertyValue("p2 overstyrt av child", p2, child).save();
		child.propertySources.add(parent);
		child.propertySources.add(grandParent);
		
		final PropertyValueFinder finder = new PropertyValueFinder().setOwner(child).setMayInheritFrom(child.propertySources);
		Assert.assertEquals(2, finder.list().size());

		final Map<PropertyStub, PropertyValue> propertyValueMap = finder.getPropertyValueMap();
		Assert.assertEquals("p1 overstyrt av parent", propertyValueMap.get(p1).getValue());
		Assert.assertEquals("p2 overstyrt av child", propertyValueMap.get(p2).getValue());		
	}

}
