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
public class PropertyClassFinderTest {

	@Before
	public void setUp() {
		new PropertyClass("PC-1").save();
		new PropertyClass("PC-2").save();
		new PropertyClass("PC-3").save();
	}

	@Test
	public void findSinglePropertyClass() {
		PropertyClass propertyClass = new PropertyClassFinder().setId("PC-2").load();
		Assert.assertEquals("PC-2", propertyClass.getId());
	}

	@Test
	public void findAllPropertyClasses() {
		List<PropertyClass> propertyClasses = new PropertyClassFinder().loadList();
		Assert.assertEquals(3, propertyClasses.size());
		Assert.assertEquals("PC-1", propertyClasses.get(0).getId());
		Assert.assertEquals("PC-2", propertyClasses.get(1).getId());
		Assert.assertEquals("PC-3", propertyClasses.get(2).getId());
	}

}