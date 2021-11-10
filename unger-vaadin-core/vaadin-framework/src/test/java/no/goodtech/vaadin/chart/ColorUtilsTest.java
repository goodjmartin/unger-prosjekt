package no.goodtech.vaadin.chart;

import com.vaadin.addon.charts.model.style.SolidColor;
import org.junit.Assert;
import org.junit.Test;

public class ColorUtilsTest {

	@Test
	public void testCreateColor() {
		Assert.assertEquals(SolidColor.RED.toString(), ColorUtils.createColor("#FF0000").toString());
		Assert.assertEquals(SolidColor.RED.toString(), ColorUtils.createColor("RED").toString());
		Assert.assertEquals(SolidColor.RED.toString(), ColorUtils.createColor("red").toString());

		Assert.assertNull(ColorUtils.createColor("ILLEGAL_COLOR"));
		Assert.assertNull(ColorUtils.createColor("#illegal"));
		Assert.assertNull(ColorUtils.createColor("#FFFFFG"));
	}
}
