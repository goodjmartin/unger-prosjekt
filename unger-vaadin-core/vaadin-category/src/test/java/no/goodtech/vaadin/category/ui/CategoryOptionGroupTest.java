package no.goodtech.vaadin.category.ui;

import org.junit.Assert;
import org.junit.Test;

public class CategoryOptionGroupTest{

	@Test
	public void testInstatiation() {
		
		CategoryOptionGroup combo = new CategoryOptionGroup("owner");
		Assert.assertNull(combo.getCaption());
		
		combo = new CategoryOptionGroup(String.class);
		Assert.assertNull(combo.getCaption());

		combo = new CategoryOptionGroup("caption", "owner");
		Assert.assertEquals("caption", combo.getCaption());
		
		combo = new CategoryOptionGroup("caption", String.class);
		Assert.assertEquals("caption", combo.getCaption());
	}
}
