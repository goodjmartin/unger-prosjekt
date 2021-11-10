package no.goodtech.vaadin.category;

import no.goodtech.persistence.MainConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@Transactional
@ContextConfiguration(classes = MainConfig.class, locations={"classpath*:goodtech-server.xml"})
public class CategoryFinderTest {

	@Test
	public void findByOwner() {
		new Category("cat1", "cat", Category.class, 0).save();
		new Category("cat1", "cat", Double.class, 0).save();

		CategoryFinder finder = new CategoryFinder(Category.class);
		Assert.assertEquals(1, finder.list().size());

		finder = new CategoryFinder(Category.class.getSimpleName());
		Assert.assertEquals(1, finder.list().size());
	}
}
