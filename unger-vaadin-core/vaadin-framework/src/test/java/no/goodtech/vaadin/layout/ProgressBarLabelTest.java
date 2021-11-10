package no.goodtech.vaadin.layout;

import org.junit.Assert;
import org.junit.Test;

public class ProgressBarLabelTest {

	@Test
	public void setShare() {
		Assert.assertEquals(0.0, new ProgressBarLabel(null, null, null).getShare(), 0.001);
		Assert.assertEquals(0.0, new ProgressBarLabel(null, 0.0, null).getShare(), 0.001);
		Assert.assertEquals(0.0, new ProgressBarLabel(null, null, 0.0).getShare(), 0.001);
		Assert.assertEquals(0.0, new ProgressBarLabel(null, 100.0, null).getShare(), 0.001);
		Assert.assertEquals(100.0, new ProgressBarLabel(null, 100.0, 100.0).getShare(), 0.001);
		Assert.assertEquals(75.0, new ProgressBarLabel(null, 7.5, 10.0).getShare(), 0.001);
		Assert.assertEquals(100.0, new ProgressBarLabel(null, 110.0, 100.0).getShare(), 0.001);
	}

}
