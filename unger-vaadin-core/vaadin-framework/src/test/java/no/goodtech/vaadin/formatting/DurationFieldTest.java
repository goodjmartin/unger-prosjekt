package no.goodtech.vaadin.formatting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.management.timer.Timer;

import no.goodtech.vaadin.formatting.DurationField.Resolution;

import org.junit.Test;

public class DurationFieldTest {

	private DurationField field;
	
	@Test
	public void testSetGetValue() {
		field = new DurationField(null, Resolution.HOUR, Resolution.MIN);
		assertNull(field.getValue());

		field.setValue(Timer.ONE_DAY);
		assertEquals("24", field.getFields().get(Resolution.HOUR).getValue());
		assertNull(field.getFields().get(Resolution.MIN).getValue());
		assertEquals(Timer.ONE_DAY, field.getValue().longValue());
		
		field.setValue(Timer.ONE_HOUR);
		assertEquals("1", field.getFields().get(Resolution.HOUR).getValue());
		assertNull(field.getFields().get(Resolution.MIN).getValue());
		assertEquals(Timer.ONE_HOUR, field.getValue().longValue());

		field.setValue(3 * Timer.ONE_HOUR + 15 * Timer.ONE_MINUTE);
		assertEquals("3", field.getFields().get(Resolution.HOUR).getValue());
		assertEquals("15", field.getFields().get(Resolution.MIN).getValue());
		assertEquals(3 * Timer.ONE_HOUR + 15 * Timer.ONE_MINUTE, field.getValue().longValue());

		field.setValue(15 * Timer.ONE_MINUTE);
		assertNull(field.getFields().get(Resolution.HOUR).getValue());
		assertEquals("15", field.getFields().get(Resolution.MIN).getValue());
		assertEquals(15 * Timer.ONE_MINUTE, field.getValue().longValue());

		field.setValue(90 * Timer.ONE_MINUTE);
		assertEquals("1", field.getFields().get(Resolution.HOUR).getValue());
		assertEquals("30", field.getFields().get(Resolution.MIN).getValue());
		assertEquals(90 * Timer.ONE_MINUTE, field.getValue().longValue());

		field.setValue(0L);
		assertNull(field.getFields().get(Resolution.HOUR).getValue());
		assertNull(field.getFields().get(Resolution.MIN).getValue());
		assertNull(field.getValue());

		field.setValue(null);
		assertNull(field.getFields().get(Resolution.HOUR).getValue());
		assertNull(field.getFields().get(Resolution.MIN).getValue());
		assertNull(field.getValue());

		assertConversionOf(Timer.ONE_DAY);
		assertConversionOf(Timer.ONE_HOUR);
		assertConversionOf(Timer.ONE_MINUTE);
		assertConversionOf(Timer.ONE_DAY + 3 * Timer.ONE_HOUR + 15 * Timer.ONE_MINUTE);
	}
	
	private void assertConversionOf(Long value) {
		field.setValue(value);
		if (value == null)
			assertNull(field.getValue());
		else
			assertEquals(value.longValue(), field.getValue().longValue());		
	}
}
