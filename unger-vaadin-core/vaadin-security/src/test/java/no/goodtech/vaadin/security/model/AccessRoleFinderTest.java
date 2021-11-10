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
public class AccessRoleFinderTest {

    @Test
    public void testFindById() {
		populateTestData();

        Assert.assertEquals("ar1", new AccessRoleFinder().setId("ar1").find().getId());
        Assert.assertEquals("ar2", new AccessRoleFinder().setId("ar2").find().getId());
        Assert.assertEquals("ar3", new AccessRoleFinder().setId("ar3").find().getId());
        Assert.assertNull(new AccessRoleFinder().setId("u4").find());
	}

	@Test
	public void testFindByAccessFunction(){
		populateTestData();
		String accessFunction = "functionId";
		List<AccessRoleStub> accessRoles = new AccessRoleFinder().setAccessFunction(accessFunction).list();
		Assert.assertEquals(1, accessRoles.size());
	}

    @Test
	public void testFindAll() {
		populateTestData();

		List<AccessRole> accessRoles = new AccessRoleFinder().loadList();
        Assert.assertEquals(3, accessRoles.size());
	}
    @Test
	public void testOrderById() {
		populateTestData();

		AccessRoleFinder accessRoleFinder = new AccessRoleFinder();
		accessRoleFinder.orderById(true);		// Ascending
		List<AccessRole> accessRoles = accessRoleFinder.loadList();
        Assert.assertEquals(3, accessRoles.size());
        Assert.assertEquals("ar1", accessRoles.get(0).getId());
        Assert.assertEquals("ar2", accessRoles.get(1).getId());
        Assert.assertEquals("ar3", accessRoles.get(2).getId());

		accessRoleFinder = new AccessRoleFinder();
		accessRoleFinder.orderById(false);		// Descending
		accessRoles = accessRoleFinder.loadList();
        Assert.assertEquals(3, accessRoles.size());
        Assert.assertEquals("ar3", accessRoles.get(0).getId());
        Assert.assertEquals("ar2", accessRoles.get(1).getId());
        Assert.assertEquals("ar1", accessRoles.get(2).getId());
	}

	private void populateTestData() {
		AccessRole role1 = createAccessRole("ar1");
		role1.addAccessFunctionId("functionId");
		role1.save();
		createAccessRole("ar2");
		createAccessRole("ar3");
	}

    private AccessRole createAccessRole(final String id) {
        AccessRole accessRole = new AccessRole();
        accessRole.setId(id);
        return accessRole.save();
    }

}*/
