package no.goodtech.vaadin.global;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations={"classpath:goodtech-server.xml"})
public class GlobalsTest {
	
	/**
	 * Verify correct default configuration
	 */
	@Test
	public void testConfiguration() {
		Assert.assertEquals(5, Globals.getMaxLoginFailureAttempts());
	}

}
