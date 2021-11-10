package no.goodtech.vaadin.help.model;

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
public class HelpTextIntegrationTest {

	@Test
	public void testSaveAndLoad() {
		final HelpText savedText = new HelpText("test1").save();
		Assert.assertNotNull(savedText.getPk());
		
		Assert.assertNotNull(new HelpTextFinder().setId("test1").find().getPk());
	}

}
