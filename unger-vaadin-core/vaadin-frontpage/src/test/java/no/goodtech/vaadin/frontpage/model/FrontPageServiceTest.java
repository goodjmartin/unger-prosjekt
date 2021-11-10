package no.goodtech.vaadin.frontpage.model;

import com.vaadin.ui.Component;
import no.goodtech.persistence.MainConfig;
import no.goodtech.vaadin.frontpage.TestCardComponent;
import no.goodtech.vaadin.frontpage.ui.FrontPageView;
import no.goodtech.vaadin.security.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@Transactional
@ContextConfiguration(classes = MainConfig.class, locations={"classpath*:goodtech-server.xml"})
public class FrontPageServiceTest {

	private final FrontPageService service = new FrontPageService("no.goodtech.vaadin.frontpage");

	@Test
	public void testArgumentToStringArray(){
		Map<String, String> argMap = new HashMap<>();
		argMap.put("1", "2");
		argMap.put("3", "4");

		String[] argArr = service.argumentMapToStringArray(argMap);

		Assert.assertEquals(4, argArr.length);
		Assert.assertEquals("1", argArr[0]);
		Assert.assertEquals("2", argArr[1]);
		Assert.assertEquals("3", argArr[2]);
		Assert.assertEquals("4", argArr[3]);
	}

	@Test
	public void testGetComponentFromCard(){
		User user = new User("admin", "admin", null).save();
		FrontPageCard card = new FrontPageCard(user, TestCardComponent.class.getName(), null, 0).save();
		Component component = service.getComponentFromCard(card);

		Assert.assertNotNull(component);
		Assert.assertTrue(component instanceof TestCardComponent);
	}

	@Test
	public void testGetClasses(){
		ArrayList<IFrontPageCardComponent> components = service.getClasses();
		Assert.assertNotNull(components);
		Assert.assertTrue(components.size() > 0);
		Assert.assertTrue(components.get(0) instanceof TestCardComponent);
	}
}
