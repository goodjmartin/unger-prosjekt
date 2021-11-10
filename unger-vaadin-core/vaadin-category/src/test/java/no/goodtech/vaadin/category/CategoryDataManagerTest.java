package no.goodtech.vaadin.category;

import no.goodtech.persistence.MainConfig;
import no.goodtech.vaadin.category.ui.CategoryComboBox;

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
public class CategoryDataManagerTest {

	@Test
	public void wrongIconConfig() {
		Category category1 = new Category("category1", Category.class);
		category1.setIconName("");
		category1.save();
		
		final CategoryComboBox comboBox = new CategoryComboBox(Category.class);
		comboBox.refresh();
		
	}
}
