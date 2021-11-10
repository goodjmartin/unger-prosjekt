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
 * Integrasjonstester av {@link PropertyFinder}
 * @author oystein
 */
@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@Transactional
@ComponentScan("no.goodtech")
@ContextConfiguration(locations={"classpath*:goodtech-server.xml", "classpath:vaadin-properties-test.xml"})
public class PropertyFinderTest {

	/**
	 * Test av filter på ID
	 */
	@Test
	public void testFindPropertyById() {
		Assert.assertEquals(0, new PropertyFinder().list().size());
		Property p1 = new Property("p1").save();
		Assert.assertEquals(1, new PropertyFinder().list().size());
		Assert.assertEquals(0, new PropertyFinder().setId("feil id").list().size());
		Assert.assertEquals(p1, new PropertyFinder().setId("p1").load());
	}
}
