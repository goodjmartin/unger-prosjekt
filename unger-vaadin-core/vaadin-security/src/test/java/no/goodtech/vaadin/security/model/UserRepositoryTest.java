package no.goodtech.vaadin.security.model;

/*
import java.util.HashSet;
import java.util.Set;

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
public class UserRepositoryTest {

	@Test
	public void testAddUser() {
		// Opprett bruker
		Assert.assertNotNull(createUser("u1"));

		// Finn bruker
		UserFinder userFinder = new UserFinder();
		userFinder.setId("u1");
		User user = userFinder.find();
		Assert.assertNotNull(user);
		Assert.assertEquals("u1", user.getId());
	}

	@Test
	public void testRemoveUser() {
		// Opprett bruker
		User user = createUser("u1");
		Assert.assertNotNull(user);

		// Slett bruker
		user.delete();

		// Prøv å finne bruker 'u1' (finnes ikke lenger)
		UserFinder userFinder = new UserFinder();
		userFinder.setId("u1");
		Assert.assertNull(userFinder.find());
	}


	@Test
	public void testAddAccessRole() {
		// Opprett bruker og aksess rolle
		User user = createUser("u1");


		// Tildel rolle til bruker
		AccessRole accessRole = createAccessRole("ar1");
		Set<AccessRole> accessRoles = new HashSet<>();
		accessRoles.add(accessRole);
		user.setAccessRoles(accessRoles);
		Assert.assertNotNull(user.save());

		// Finn bruker og valider resultat
		UserFinder userFinder = new UserFinder();
		userFinder.setId("u1");
		user = userFinder.find();
		Assert.assertNotNull(user);
		Assert.assertEquals(1, user.getAccessRoles().size());
		Assert.assertEquals(user.getAccessRoles().iterator().next().getId(), accessRole.getId());
//        Assert.assertTrue(user.getAccessRoles().contains(user));
	}

	@Test
	public void testRemoveAccessRole() {
		// Opprett bruker og aksess rolle
		User user = createUser("u1");

		// Tildel rolle til bruker
		Set<AccessRole> accessRoles = new HashSet<>();
		AccessRole accessRole = createAccessRole("ar1");
		accessRoles.add(accessRole);
		user.setAccessRoles(accessRoles);
		Assert.assertNotNull(user.save());

		// Slett aksess rolle
		AccessRoleFinder accessRoleFinder = new AccessRoleFinder();
		accessRoleFinder.setId("ar1");
		accessRoleFinder.load();

		//TODO denne testen fungerte tidligere pga en entitynotfoundexception på user som ble ignorert
		//Assert.assertFalse(accessRole.delete());
	}

	@Test
	public void testRemoveUserWithAssignedAccessRole() {
		// Opprett bruker og aksess rolle
		User user = createUser("u1");

		// Tildel rolle til bruker
		Set<AccessRole> accessRoles = new HashSet<>();
		AccessRole accessRole = createAccessRole("ar1");
		accessRoles.add(accessRole);
		user.setAccessRoles(accessRoles);
		user = user.save();

		// Slett bruker
		user.delete();

		// Finn bruker og valider resultat (bruker skal ikke finnes lenger)
		UserFinder userFinder = new UserFinder();
		userFinder.setId("u1");
		Assert.assertNull(userFinder.find());

		// Finn aksess rolle og valider resultat (rollen skal være der fremdeles)
		AccessRoleFinder accessRoleFinder = new AccessRoleFinder();
		accessRoleFinder.setId("ar1");
		accessRole = accessRoleFinder.find();
		Assert.assertNotNull(accessRole);
		Assert.assertEquals("ar1", accessRole.getId());
	}

	@Test
	public void testAddAdditionalAccessRole() {
		// Opprett bruker og aksess rolle
		User user = createUser("u1");
		AccessRole accessRole1 = createAccessRole("ar1");
		AccessRole accessRole2 = createAccessRole("ar2");

		// Tildel rolle til bruker
		Set<AccessRole> accessRoles = new HashSet<>();
		accessRoles.add(accessRole1);
		user.setAccessRoles(accessRoles);
		Assert.assertNotNull(user.save());

		// Finn bruker og valider resultat
		UserFinder userFinder = new UserFinder();
		userFinder.setId("u1");
		user = userFinder.find();
		Assert.assertNotNull(user);
		Assert.assertEquals(1, user.getAccessRoles().size());
		Assert.assertTrue(user.getAccessRoles().contains(accessRole1));

		// Legg til ny rolle
		accessRoles = new HashSet<>();
		accessRoles.add(accessRole1);
		accessRoles.add(accessRole2);
		user.setAccessRoles(accessRoles);
		Assert.assertNotNull(user.save());

		// Finn bruker og valider resultat
		user = userFinder.find();
		Assert.assertNotNull(user);
		Assert.assertEquals(2, user.getAccessRoles().size());
		Assert.assertTrue(user.getAccessRoles().contains(accessRole1));
		Assert.assertTrue(user.getAccessRoles().contains(accessRole2));

	}

	@Test
	public void testSetLoginFailures() {
		// Opprett bruker
		User user = createUser("u1");
		Assert.assertNotNull(user);

		// Set antall login feil
		user.setLoginFailures(5);
		user.save();

		// Finn bruker
		UserFinder userFinder = new UserFinder();
		userFinder.setId("u1");
		user = userFinder.find();
		Assert.assertNotNull(user);
		Assert.assertEquals("u1", user.getId());
		Assert.assertEquals(5, user.getLoginFailures());
	}

	private User createUser(final String id) {
		User user = new User();
		user.setId(id);
		user.setPassword(id);
		return user.save();
	}

	private AccessRole createAccessRole(final String id) {
		AccessRole accessRole = new AccessRole();
		accessRole.setId(id);
		return accessRole.save();
	}

}*/
