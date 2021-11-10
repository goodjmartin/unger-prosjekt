package no.goodtech.vaadin.properties.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by mikkelsn on 28.08.2014.
 * TODO trenger vi tester på dette?
 */
public class PropertyMembershipTest {
	
	@Test
	public void testEditable() {
		PropertyMembership membership = new PropertyMembership();
		Assert.assertFalse(membership.isEditable());
		Assert.assertFalse(membership.getEditable());

		membership.setEditable(true);
		Assert.assertTrue(membership.isEditable());
		Assert.assertTrue(membership.getEditable());
		
		membership.setEditable(false);
		Assert.assertFalse(membership.isEditable());
		Assert.assertFalse(membership.getEditable());
	}
}
