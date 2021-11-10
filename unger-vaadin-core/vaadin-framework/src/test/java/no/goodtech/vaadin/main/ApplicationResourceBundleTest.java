package no.goodtech.vaadin.main;

import org.junit.Assert;
import org.junit.Test;

public class ApplicationResourceBundleTest {

	@Test
    public void testPropertyFoundWithText() {
        String property = ApplicationResourceBundle.getInstance("admin").getString("test.property.withText");
		Assert.assertEquals("This is a test", property);
    }

	@Test
    public void testPropertyFoundWithoutText() {
        String property = ApplicationResourceBundle.getInstance("admin").getString("test.property.withoutText");
		Assert.assertEquals("", property);
    }

	@Test
    public void testPropertyNotMapped() {
        String property = ApplicationResourceBundle.getInstance("admin").getString("test.property.propertyNotMapped");
		Assert.assertEquals("PropertyNotMapped", property);
    }

	@Test
    public void testInstanceNotFound() {
        String property = ApplicationResourceBundle.getInstance("noInstance").getString("");
		Assert.assertEquals("PropertyNotMapped", property);
    }

}
