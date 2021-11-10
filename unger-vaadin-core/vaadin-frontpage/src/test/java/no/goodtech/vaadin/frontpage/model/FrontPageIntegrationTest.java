package no.goodtech.vaadin.frontpage.model;

import no.goodtech.persistence.MainConfig;
import no.goodtech.vaadin.security.model.User;
import org.junit.Assert;
import org.junit.Before;
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
public class FrontPageIntegrationTest {

	private User user;

	@Before
	public void setup(){
		user = new User("admin", "admin", new String[]{""}).save();
	}

	@Test
	public void testSaveAndLoad() {
		final FrontPageCard card = new FrontPageCard(user, "no.goodtech.vaadin.frontpage.ui.FrontPage", null, 0).save();
		Assert.assertNotNull(card.getPk());
		
		Assert.assertTrue(new FrontPageCardFinder().exists());
		Assert.assertTrue(new FrontPageCardFinder().setUserId("admin").exists());

		final FrontPageCard card2 = new FrontPageCard(user, "no.goodtech.vaadin.frontpage.ui.FrontPage", null, 1).save();
		Assert.assertEquals(2, new FrontPageCardFinder().setUserId("admin").list().size());
		Assert.assertEquals(card, new FrontPageCardFinder().setUserId("admin").list().get(0));
		Assert.assertEquals(card2, new FrontPageCardFinder().setUserId("admin").list().get(1));
	}

}
