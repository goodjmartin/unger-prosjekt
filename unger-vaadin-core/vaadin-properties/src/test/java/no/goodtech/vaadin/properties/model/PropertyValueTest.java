package no.goodtech.vaadin.properties.model;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test av {@link PropertyValue}
 * @author oystein
 */
public class PropertyValueTest {

	/**
	 * Test av validering av tekst-verdier
	 */
	@Test
	public void testValidateStringValue() {
		Property string = new Property("String");
		final PropertyValue stringValue = string.createValue();
		
		//test av manglende eier
		Assert.assertEquals("Verdien må ha en eier", stringValue.validate());
		string.setPk(1);
		stringValue.setOwner(string);
		
		//tomme verdier er ikke tillatt
//see PropertyValue.validate()
//		final String messageEmptyValue = "Tomme eller blanke verdier kan ikke lagres. Slett heller verdien";
//		Assert.assertEquals(messageEmptyValue, stringValue.validate());
//		stringValue.setValue("");
//		Assert.assertEquals(messageEmptyValue, stringValue.validate());
//		stringValue.setValue("          ");
//		Assert.assertEquals(messageEmptyValue, stringValue.validate());
		
		//verdien kan ikke være for lang
		String tooLongString = "";
		for (int i = 0; i <= AbstractPropertyValue.MAX_VALUE_SIZE; i++)
			tooLongString += "Q";
		stringValue.setValue(tooLongString);
		final String message = "Lengden på verdien er " + tooLongString.length() + " tegn. Maks-lengde er " + AbstractPropertyValue.MAX_VALUE_SIZE;
		Assert.assertEquals(message, stringValue.validate());
	}
	
	/**
	 * Test av at validering blir gjort før lagring
	 */
	@Test
	public void testSaveInvalidObject() {
		try {
			new Property("String").createValue().save();
			Assert.fail("skulle ikke gått an å lagre");
		} catch (RuntimeException e) {
			Assert.assertEquals("Verdien må ha en eier", e.getMessage());
		}
	}
	
	/**
	 * Test av om verdier blir parset riktig og at man får tilbake verdiene på riktig format
	 * @throws ParseException blabla
	 */
	@Test
	public void testSetGetValue() throws ParseException {
		final PropertyValue stringValue = createPropertyValue("5", String.class);
		Assert.assertEquals("5", stringValue.getValue());
		
		final PropertyValue longObjectValue = createPropertyValue(5L, Long.class);
		Assert.assertEquals(5L, longObjectValue.getValue());

		//integers will be converted to long

		Assert.assertEquals(5L, createPropertyValue(5, Long.class).getValue());
		Assert.assertEquals(5L, createPropertyValue(Integer.valueOf(5), Long.class).getValue());

		short shortValue = 5;
		Assert.assertEquals(5L, createPropertyValue(shortValue, Long.class).getValue());
		Assert.assertEquals(5L, createPropertyValue(Short.valueOf(shortValue), Long.class).getValue());

		byte byteValue = 5;
		Assert.assertEquals(5L, createPropertyValue(byteValue, Long.class).getValue());
		Assert.assertEquals(5L, createPropertyValue(Byte.valueOf(byteValue), Long.class).getValue());

		final PropertyValue doubleValue = createPropertyValue("5", Double.class); //Skal vi kunne angi String som verdi når datatypen er Double?
		Assert.assertEquals(5.0, doubleValue.getValue());
	
		final PropertyValue doubleObjectValue = createPropertyValue(5.0, Double.class);
		Assert.assertEquals(5.0, doubleObjectValue.getValue());

		//float will be converted to double

		final PropertyValue floatValue = createPropertyValue(5f, Double.class); //Skal vi kunne angi String som verdi når datatypen er Double?
		Assert.assertEquals(5.0, floatValue.getValue());

		final PropertyValue floatObjectValue = createPropertyValue(Float.valueOf(5f), Double.class);
		Assert.assertEquals(5.0, floatObjectValue.getValue());

		final PropertyValue trueValue = createPropertyValue("true", Boolean.class); //Skal vi kunne angi String som verdi når datatypen er Boolean?
		Assert.assertEquals(true, trueValue.getValue());


		final PropertyValue trueObjectValue = createPropertyValue(true, Boolean.class);
		Assert.assertEquals(true, trueObjectValue.getValue());

		final PropertyValue falseValue = createPropertyValue("false", Boolean.class); //Skal vi kunne angi String som verdi når datatypen er Boolean?
		Assert.assertEquals(false, falseValue.getValue());
		
		final PropertyValue falseObjectValue = createPropertyValue(false, Boolean.class);
		Assert.assertEquals(false, falseObjectValue.getValue());

		final String dateAsString = "2012-02-05 11:59:30.012";
		final String formatPattern = "yyyy-MM-dd HH:mm:ss.sss";

		final PropertyValue dateValueWithDefaultFormat = createPropertyValue(dateAsString, Date.class);
		Assert.assertEquals(new SimpleDateFormat(Property.DATETIME_FORMAT).parse(dateAsString), dateValueWithDefaultFormat.getValue());

		final PropertyValue dateValueWithSpeficFormat = createPropertyValue(dateAsString, Date.class, formatPattern);
		Assert.assertEquals(new SimpleDateFormat(formatPattern).parse(dateAsString), dateValueWithSpeficFormat.getValue());
		
		try {
			SimpleDateFormat format = new SimpleDateFormat();
			createPropertyValue(format, SimpleDateFormat.class);
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(e.getMessage().startsWith("class java.text.SimpleDateFormat er ikke støttet"));
		}
	}

	/**
	 * Opprett en egenskaps-verdi
	 * @param value verdien
	 * @param datatype datatype
	 * @param formatPattern evt. format
	 * @return den nye egenskaps-verdien
	 */
	public static PropertyValue createPropertyValue(Object value, Class<?> datatype, String formatPattern) {
		Property property = new Property();
		property.setDataType(datatype);
		final PropertyValue propertyValue = new PropertyValue(property);
		property.setFormatPattern(formatPattern);
		propertyValue.setValueAsObject(value);
		return propertyValue;
	}
	
	/**
	 * Opprett en egenskaps-verdi
	 * @param value verdien
	 * @param datatype datatype
	 * @return den nye egenskaps-verdien
	 */
	public static PropertyValue createPropertyValue(Object value, Class<?> datatype) {
		return createPropertyValue(value, datatype, null);
	}

	@Test
	public void testGetFormattedValue() {
		Property property = new Property("property");
		property.setFormatPattern("#.000");
		property.setDataType(Double.class);

		final PropertyValue propertyValue = new PropertyValue(property);
		propertyValue.setValue(4.16567);

        String separator = String.valueOf(((DecimalFormat) DecimalFormat.getCurrencyInstance()).getDecimalFormatSymbols().getDecimalSeparator());

		Assert.assertEquals("4"+ separator + "166", propertyValue.getFormattedValue());
	}

}
