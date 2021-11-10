package no.goodtech.vaadin.security;

import org.junit.Assert;
import org.junit.Test;

public class MD5HashTest {

	@Test
	public void testConvert() {
		Assert.assertEquals("5f4dcc3b5aa765d61d8327deb882cf99", MD5Hash.convert("password"));
		Assert.assertEquals("900150983cd24fb0d6963f7d28e17f72", MD5Hash.convert("abc"));
		Assert.assertEquals("3d6d9f40d1f47a57a5c1fc08cf3ffdf4", MD5Hash.convert("!\"#¤%&/()=?`^*;:"));
		Assert.assertEquals("3544848f820b9d94a3f3871a382cf138", MD5Hash.convert("New password"));
		Assert.assertEquals("033ea320055136fbbae3ddea3fdf8715", MD5Hash.convert("æøåÆØÅ"));
	}

}
