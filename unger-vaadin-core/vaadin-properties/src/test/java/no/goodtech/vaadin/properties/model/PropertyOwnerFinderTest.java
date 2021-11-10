package no.goodtech.vaadin.properties.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test av {@link PropertyOwnerFinder}
 * @author oystein
 */
@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@Transactional
@ComponentScan("no.goodtech")
@ContextConfiguration(locations={"classpath*:goodtech-server.xml", "classpath:vaadin-properties-test.xml"})
public class PropertyOwnerFinderTest {

	/**
	 * Tester at man kan finne eier på bakgrunn av egenskaper
	 */
	@Test
	public void testFindOwnerByProperty() {
		Property property1 = new Property("p1").save();
		Property property2 = new Property("p2").save();
		PropertyOwner owner1 = new PropertyOwner("owner1").save();
		PropertyOwner owner2 = new PropertyOwner("owner2").save();
		new PropertyValue("p1v1o1", property1, owner1).save();
		new PropertyValue("p1v2o2", property1, owner2).save();
		new PropertyValue("p2v2o2", property2, owner2).save();
	
		Assert.assertFalse(new OwnerFinder().setProperty(property1, "bladibladi").exists());
		Assert.assertFalse(new OwnerFinder().setProperty(property2, "p1v1").exists());
		Assert.assertEquals("owner1", new OwnerFinder().setProperty(property1, "p1v1o1").load().getId());
		Assert.assertEquals("owner1", new OwnerFinder().setProperty(property1, "p1v1*").load().getId());
		Assert.assertEquals("owner2", new OwnerFinder().setProperty(property2, "p2v2o2").load().getId());
		Assert.assertEquals(2, new OwnerFinder().setProperty(property1, "p1*").list().size()); //fant begge eiere
		Assert.assertEquals("owner2", new OwnerFinder().setProperty(property1, "p1v2o2").setProperty(property2, "p2v2o2").load().getId()); //fant begge eiere
	}
}
