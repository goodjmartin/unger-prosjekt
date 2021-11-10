package no.goodtech.dashboard.model;

import no.goodtech.dashboard.config.ui.ChartConfig;
import no.goodtech.dashboard.config.ui.SeriesConfig;
import org.junit.Assert;
import org.junit.Test;

public class SeriesConfigTest {

	@Test
	public void equalsTest() {
		SeriesConfig seriesConfig1 = new SeriesConfig();
		Assert.assertEquals(seriesConfig1, seriesConfig1);
		Assert.assertFalse(seriesConfig1.equals(null));
		Assert.assertFalse(seriesConfig1.equals(new String("wrong type")));

		SeriesConfig seriesConfig2 = new SeriesConfig();
		Assert.assertNotEquals(seriesConfig1, seriesConfig2); //missing pk and ID

		seriesConfig1.setId("1");
		Assert.assertNotEquals(seriesConfig1, seriesConfig2); // missing ID for seriesConfig2

		seriesConfig2.setId("2");
		Assert.assertNotEquals(seriesConfig1, seriesConfig2); //different ID

		seriesConfig2.setId("1");
		final ChartConfig panelConfig1 = new ChartConfig();
		panelConfig1.setPk(1L);
		seriesConfig1.setPanelConfig(panelConfig1);
		seriesConfig2.setPanelConfig(panelConfig1);
		Assert.assertEquals(seriesConfig1, seriesConfig2); //same ID on same panel

		final ChartConfig panelConfig2 = new ChartConfig();
		panelConfig2.setPk(2L);
		seriesConfig2.setPanelConfig(panelConfig2);
		Assert.assertNotEquals(seriesConfig1, seriesConfig2); //same ID, but different panels

		seriesConfig1.setPanelConfig(null);
		seriesConfig2.setPanelConfig(null);
		Assert.assertEquals(seriesConfig1, seriesConfig2); //same ID but not assigned to any specific panel
	}
}
