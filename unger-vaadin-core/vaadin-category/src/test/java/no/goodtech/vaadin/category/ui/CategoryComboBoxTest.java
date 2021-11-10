package no.goodtech.vaadin.category.ui;

import org.junit.Assert;
import org.junit.Test;

public class CategoryComboBoxTest{

	@Test
	public void testInstatiation() {
		
		CategoryComboBox combo = new CategoryComboBox("owner");
		Assert.assertNull(combo.getCaption());
		
		combo = new CategoryComboBox(String.class);
		Assert.assertNull(combo.getCaption());

		combo = new CategoryComboBox("caption", "owner");
		Assert.assertEquals("caption", combo.getCaption());
		
		combo = new CategoryComboBox("caption", String.class);
		Assert.assertEquals("caption", combo.getCaption());
	}
}
