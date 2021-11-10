package no.goodtech.vaadin.security;

import org.junit.Assert;
import org.junit.Test;

public class AccessFunctionManagerTest {

	@Test
	public void testRegisterAccessFunction(){
		AccessFunction af = new AccessFunction("testid", "testdescription");
		AccessFunctionManager.registerAccessFunction(af);

		Assert.assertEquals(1, AccessFunctionManager.getAccessFunctions().size());
		Assert.assertEquals(af, AccessFunctionManager.getAccessFunctions().get(0));
	}

	@Test
	public void testRemoveAccessFunction(){
		String id = "testid";
		AccessFunction af = new AccessFunction(id, "testdescription");
		AccessFunctionManager.registerAccessFunction(af);

		Assert.assertEquals(1, AccessFunctionManager.getAccessFunctions().size());
		Assert.assertEquals(af, AccessFunctionManager.getAccessFunctions().get(0));

		AccessFunctionManager.removeAccessFunction(id);

		Assert.assertEquals(0, AccessFunctionManager.getAccessFunctions().size());
	}
}
