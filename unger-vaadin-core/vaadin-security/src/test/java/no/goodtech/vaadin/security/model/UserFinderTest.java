package no.goodtech.vaadin.security.model;

/*
import java.util.List;

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
public class UserFinderTest {

	@Test
	public void testFindById() {
		populateTestData();
		Assert.assertEquals("u1", new UserFinder().setId("u1").find().getId());
		Assert.assertEquals("u2", new UserFinder().setId("u2").find().getId());
		Assert.assertEquals("u3", new UserFinder().setId("u3").find().getId());
		Assert.assertNull(new UserFinder().setId("u4").find());
	}

	public void testFindAll() {
		populateTestData();

		List<User> users = new UserFinder().loadList();
		Assert.assertEquals(3, users.size());
	}

	public void testOrderById() {
		populateTestData();

		UserFinder userFinder = new UserFinder();
		userFinder.orderById(true);        // Ascending
		List<User> users = userFinder.loadList();
		Assert.assertEquals(3, users.size());
		Assert.assertEquals("u1", users.get(0).getId());
		Assert.assertEquals("u2", users.get(1).getId());
		Assert.assertEquals("u3", users.get(2).getId());

		userFinder = new UserFinder();
		userFinder.orderById(false);        // Descending
		users = userFinder.loadList();
		Assert.assertEquals(3, users.size());
		Assert.assertEquals("u3", users.get(0).getId());
		Assert.assertEquals("u2", users.get(1).getId());
		Assert.assertEquals("u1", users.get(2).getId());
	}

	private void populateTestData() {
		createUser("u1");
		createUser("u2");
		createUser("u3");
	}

	private User createUser(final String id) {
		User user = new User();
		user.setId(id);
		user.setPassword("password");
		return user.save();
	}

}
 */
