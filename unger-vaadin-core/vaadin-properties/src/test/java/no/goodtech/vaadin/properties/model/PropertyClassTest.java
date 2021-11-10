package no.goodtech.vaadin.properties.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@Transactional
@ComponentScan("no.goodtech")
@ContextConfiguration(locations={"classpath*:goodtech-server.xml", "classpath:vaadin-properties-test.xml"})
public class PropertyClassTest {
	@Test
	public void testSave()
	{
		PropertyClass propertyClass = new PropertyClass(UUID.randomUUID().toString()).save();
		Assert.assertNotNull(propertyClass.getPk());
	}

	@Test
	public void testGetMembers(){
		// Create propertyclass 'A'
		PropertyClass pcA = new PropertyClass("PC1").save();

		// Create propertyclass 'B'
		PropertyClass pcB = new PropertyClass("PC2").save();

		// Create propertyclass 'C', without any property
		PropertyClass pcC = new PropertyClass(UUID.randomUUID().toString()).save();

		// Create property 'a' and connect to propertyclass 'A'
		Property pa = new Property("PA").save();
		pa.setId("a");
		pa.addPropertyClass(pcA);
		pa.save();

		// Create property 'b' and connect to propertyclass 'A' and 'B'
		Property pb = new Property("PB").save();
		pb.setId("b");
		pb.addPropertyClass(pcA);
		pb.addPropertyClass(pcB);
		pb.save();

		// Create property 'c' and connect to propertyclass 'B'
		Property pc = new Property(UUID.randomUUID().toString()).save();
		pc.setId("c");
		pc.addPropertyClass(pcB);
		pc.save();

		// Create property 'd' (not assigned to any propertyclass)
		Property pd = new Property(UUID.randomUUID().toString()).save();
		pd.setId("d");

		// Validate propertyclass 'A'
		PropertyFinder propertyFinder = new PropertyFinder().setPropertyClassId(pcA.getId());
		List<PropertyStub> properties = propertyFinder.list();

		Assert.assertEquals(2, properties.size());
		Assert.assertTrue(properties.contains(pa));
		Assert.assertTrue(properties.contains(pb));
		Assert.assertFalse(properties.contains(pc));

		// Validate propertyclass 'B'
		propertyFinder = new PropertyFinder().setPropertyClassId(pcB.getId());
		properties = propertyFinder.list();
		Assert.assertEquals(2, properties.size());
		Assert.assertFalse(properties.contains(pa));
		Assert.assertTrue(properties.contains(pb));
		Assert.assertTrue(properties.contains(pc));

		// Validate propertyclass 'C'
		propertyFinder = new PropertyFinder().setPropertyClassId(pcC.getId());
		properties = propertyFinder.list();
		Assert.assertEquals(0, properties.size());
	}

	@Test
	public void getMembershipsTest(){
		PropertyClass pcA = new PropertyClass("PC1").save();

		Property pa = new Property("p1").save();
		Property pb = new Property("p2").save();

		pcA.addPropertyMembership(pa, null, null, 1);
		pcA.addPropertyMembership(pb, null, null, 0);

		pcA = pcA.save();

		Assert.assertEquals(2, pcA.getPropertyMemberships().size());
		Assert.assertTrue(pcA.getProperties().contains(pa));
		Assert.assertTrue(pcA.getProperties().contains(pb));
		Assert.assertEquals(pb, pcA.getPropertyMemberships().first().getProperty());
		Assert.assertEquals(pa, pcA.getPropertyMemberships().last().getProperty());
	}

	@Test
	public void isEditableAndRequiredTest(){
		PropertyClass pcA = new PropertyClass("PC1").save();

		Property pa = new Property("p1").save();
		Property pb = new Property("p2").save();
		Property pc = new Property("p3").save();

		pcA.addPropertyMembership(pa, true, true);
		pcA.addPropertyMembership(pb, null, null);
		pcA.addPropertyMembership(pc, true, null);

		pcA = pcA.save();

		Assert.assertEquals(3, pcA.getProperties().size());

		Assert.assertEquals(1, pcA.getEditableProperties(true).size());
		Assert.assertTrue(pcA.getEditableProperties(true).contains(pa));
 
		Assert.assertEquals(2, pcA.getRequiredProperties(true).size());

		Assert.assertEquals(1, pcA.getProperties(true, true).size());
		Assert.assertTrue(pcA.getProperties(true,true).contains(pa));

		Assert.assertEquals(3, pcA.getProperties(null, null).size());
		Assert.assertEquals(2, pcA.getProperties(true, null).size());

		Assert.assertEquals(1, pcA.getProperties(null, true).size());
		Assert.assertTrue(pcA.getProperties(null, true).contains(pa));

		Assert.assertEquals(1, pcA.getProperties(false, false).size());
	}
}