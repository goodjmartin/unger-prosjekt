package no.goodtech.vaadin.properties.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@Transactional
@ComponentScan("no.goodtech")
@ContextConfiguration(locations={"classpath*:goodtech-server.xml", "classpath:vaadin-properties-test.xml"})
public class PropertyMembershipFinderTest {
	@Before
	public void setUp() {
		PropertyClass pcA = new PropertyClass("PC-1").save();

		Property pa = new Property("p1").save();
		Property pb = new Property("p2").save();
		Property pc = new Property("p3").save();
		new Property("p4").save();

		pcA.addPropertyMembership(pa, null, null, 2);
		pcA.addPropertyMembership(pb, true, true, 3);
		pcA.addPropertyMembership(pc, false, false, 0);
		pcA.save();
	}

	@Test
	public void findSinglePropertyMembership() {
		Property pa = new PropertyFinder().setId("p1").find();
		PropertyClass pcA = new PropertyClassFinder().setId("PC-1").find();
		PropertyMembership propertyMembership = new PropertyMembershipFinder().setPropertyPk(pa.getPk()).setPropertyClassPk(pcA.getPk()).find();
		Assert.assertNotNull(propertyMembership);
	}

	@Test
	public void findAllPropertyMemberships(){
		List<PropertyMembership> propertyMemberships = new PropertyMembershipFinder().loadList();
		Assert.assertEquals(3, propertyMemberships.size());
		Assert.assertEquals("p3", propertyMemberships.get(0).getProperty().getId());
		Assert.assertEquals("p1", propertyMemberships.get(1).getProperty().getId());
		Assert.assertEquals("p2", propertyMemberships.get(2).getProperty().getId());
	}

	@Test
	public void findRequiredAndEditableMemberships(){
		// Required
		List<PropertyMembership> propertyMemberships = new PropertyMembershipFinder().setRequired(null).loadList();
		Assert.assertEquals(3, propertyMemberships.size());
		propertyMemberships = new PropertyMembershipFinder().setRequired(true).loadList();
		Assert.assertEquals(1, propertyMemberships.size());
		propertyMemberships = new PropertyMembershipFinder().setRequired(false).loadList();
		Assert.assertEquals(2, propertyMemberships.size());

		// Editable
		propertyMemberships = new PropertyMembershipFinder().setEditable(null).loadList();
		Assert.assertEquals(3, propertyMemberships.size());
		propertyMemberships = new PropertyMembershipFinder().setEditable(true).loadList();
		Assert.assertEquals(1, propertyMemberships.size());
		propertyMemberships = new PropertyMembershipFinder().setEditable(false).loadList();
		Assert.assertEquals(2, propertyMemberships.size());
	}

	@Test
	public void findMembershipsInClassById(){
		List<PropertyMembership> propertyMemberships = new PropertyMembershipFinder().setPropertyClassId("PC-1").loadList();
		Assert.assertEquals(3, propertyMemberships.size());

		propertyMemberships = new PropertyMembershipFinder().setPropertyClassId(null).loadList();
		Assert.assertEquals(3, propertyMemberships.size());
	}
}
