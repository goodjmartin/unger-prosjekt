package no.goodtech.vaadin.tabs.status.model;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test av varslings-funksjon for status-indikatorer
 * @author oystein
 */
public class StatusIndicatorSubscriptionTest {

	/**
	 * Tester å legge til e-post-adresser
	 */
	@Test
	public void testAddEmailRecipient() {
		StatusIndicatorSubscription subscription = new StatusIndicatorSubscription();

		Assert.assertEquals(0, subscription.getEmailRecipients().size());
		
		subscription.addEmailRecipient("a@b.c");
		Assert.assertEquals(1, subscription.getEmailRecipients().size());
		Assert.assertEquals("a@b.c", subscription.getEmailRecipients().iterator().next());
		
		subscription.addEmailRecipient("b@b.c");
		Assert.assertEquals(2, subscription.getEmailRecipients().size());
		Assert.assertTrue(subscription.getEmailRecipients().contains("a@b.c"));
		Assert.assertTrue(subscription.getEmailRecipients().contains("b@b.c"));
		
		subscription.addEmailRecipient("b@b.c"); //prøver å legge til duplikat => skal ikke skje noe
		Assert.assertEquals(2, subscription.getEmailRecipients().size());

		subscription.setEmailRecipients("");
		subscription.setEmailRecipients("a@b.c");
		Assert.assertEquals(1, subscription.getEmailRecipients().size());
		Assert.assertEquals("a@b.c", subscription.getEmailRecipients().iterator().next());

		subscription.setEmailRecipients("");
		subscription.setEmailRecipients("a@b.c,b@b.c");
		Assert.assertEquals(2, subscription.getEmailRecipients().size());
		Assert.assertTrue(subscription.getEmailRecipients().contains("a@b.c"));
		Assert.assertTrue(subscription.getEmailRecipients().contains("b@b.c"));
		
		subscription.setEmailRecipients(Arrays.asList("b@b.c", "c@b.c"));
		Assert.assertEquals(2, subscription.getEmailRecipients().size());
		Assert.assertTrue(subscription.getEmailRecipients().contains("c@b.c"));
		Assert.assertTrue(subscription.getEmailRecipients().contains("b@b.c"));
	}
}
