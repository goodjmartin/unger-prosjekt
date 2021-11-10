package no.goodtech.vaadin.properties.model;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Enhetstester av {@link Property}
 * @author oystein
 */
public class PropertyTest {

//	/**
//	 * Tester at sortering av implementasjoner funker
//	 */
//	@Test
//	public void testGetImplementations() {
//		Property property = new Property("property");
//		PropertyImplementation implementation4 = createImplementation(4);		
//		PropertyImplementation implementation1 = createImplementation(1);		
//		PropertyImplementation implementation5 = createImplementation(5);
//		property.setImplementation(implementation4);
//		property.setImplementation(implementation1);
//		property.setImplementation(implementation5);
//		
//		final List<PropertyImplementation> implementations = property.getImplementations();
//		Assert.assertEquals(1, implementations.get(0).getIndexNo());
//		Assert.assertEquals(4, implementations.get(1).getIndexNo());
//		Assert.assertEquals(5, implementations.get(2).getIndexNo());
//	}
//
//	private PropertyImplementation createImplementation(int indexNo) {
//		PropertyImplementation implementation = new PropertyImplementation();
//		implementation.setIndexNo(indexNo);
//		return implementation;
//	}
	
	/**
	 * Test av {@link Property#getOptionsList()}
	 */
	@Test
	public void testGetOptions() {
		Property property = new Property("property");
		property.setOptions("1|2|3");
		List<String> options = property.getOptionsList();
		Assert.assertEquals("1", options.get(0));
		Assert.assertEquals("2", options.get(1));
		Assert.assertEquals("3", options.get(2));
		
		property.setOptions(null);
		Assert.assertEquals(0, property.getOptionsList().size());

		property.setOptions("");
		Assert.assertEquals(0, property.getOptionsList().size());
		
		property.setOptions("    ");
		Assert.assertEquals(0, property.getOptionsList().size());
	}

	//TODO setFormatPattern med decimalseperator ?, hvordan kunne det feile i linux når pattern var definert til ".00"
	/**
	 * Test av {@link Property#format(Object)}
	 * @throws ParseException
	 */
	@Test public void testFormat() throws ParseException {
		Property property = new Property("property");
		property.setFormatPattern("#.00");
		final char decimalSeparator = new DecimalFormat().getDecimalFormatSymbols().getDecimalSeparator();

		property.setDataType(Double.class);
		Assert.assertEquals("25" + decimalSeparator + "20", property.format(25.2));
		Assert.assertEquals("25" + decimalSeparator + "20", property.format(25.2));

		property.setDataType(Long.class);
		Assert.assertEquals("25" + decimalSeparator + "00", property.format(25L));

		property.setDataType(Date.class);
		final String dateFormat = "yyyy-MM-dd HH:mm:ss";
		property.setFormatPattern(dateFormat);
		final String dateAsString = "2013-07-31 09:04:59";
		final Date date = new SimpleDateFormat(dateFormat).parse(dateAsString);
		Assert.assertEquals(dateAsString, property.format(date));

		property.setDataType(Boolean.class);
		Assert.assertEquals("true", property.format(true));
		Assert.assertEquals("false", property.format(false));

		//ingen andre datatyper har mulighet for styrt formatering, så format() returnerer toString-representasjonen 
		property.setDataType(String.class);
		Assert.assertEquals("25.2", property.format(25.2));
		Assert.assertEquals("25.2", property.format("25.2"));
		
		//tester formatering hvis formatPattern ikke er satt
		property.setFormatPattern(null);
		property.setDataType(Long.class);
		Assert.assertEquals("", property.format(null));
		Assert.assertEquals("3", property.format(3L));
	}

	@Test
	public void testParseValue() throws ParseException {
		Property property = new Property("property");
		property.setDataType(Double.class);
		Double doubleValue = (Double) property.parseValue("3.14");
		Assert.assertEquals(3.14, doubleValue.doubleValue(), 0.000001);

		doubleValue = (Double) property.parseValue("3");
		Assert.assertEquals(3.0, doubleValue.doubleValue(), 0.000001);

		property.setDataType(Long.class);
		Long longValue = (Long) property.parseValue("3");
		Assert.assertEquals(3L, longValue.longValue());
		
		property.setDataType(Long.class);
		longValue = (Long) property.parseValue("3.0");
		Assert.assertEquals(3L, longValue.longValue());
		
		longValue = (Long) property.parseValue("3.000000");
		Assert.assertEquals(3L, longValue.longValue());

		longValue = (Long) property.parseValue("3.14");
		Assert.assertEquals(3L, longValue.longValue());
	}

//	/**
//	 * Tester at jeg håndterer at beskrivelser i valglister ikke samstemmer med valgene
//	 */
//	@Test
//	public void testGetOptionDescriptions() {
//		Property property = new Property("property");
//		property.setOptions("1|2|3");
//		property.setOptionDescriptions("A|B");
//		Map<String, String> descriptions = property.getOptionDescriptions();
//		Assert.assertEquals("A", descriptions.get("1"));
//		Assert.assertEquals("B", descriptions.get("2"));
//		Assert.assertEquals("Ikke samsvar mellom beskrivelse og valg", descriptions.get("3"));
//		
//		property.setOptionDescriptions("A|B|"); //siste element mangler beskrivelse: Skal være tillatt
//		descriptions = property.getOptionDescriptions();
//		Assert.assertEquals("A", descriptions.get("1"));
//		Assert.assertEquals("B", descriptions.get("2"));
//		Assert.assertEquals("", descriptions.get("3"));
//	}
		
	/**
	 * Test av at rekkefølgen på valgene blir riktig ved lagring av valglister 
	 */
	@Test
	public void testSetOptionsOrdering() {
		Property property = new Property("property");
		List<String> options = Arrays.asList("c", "a", "b");
//		LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
//		options.put("c", "c-beskrivelse");
//		options.put("a", "a-beskrivelse");
//		options.put("b", "b-beskrivelse");
		property.setOptionsAsList(options);
		
		Assert.assertEquals("c", property.getOptionsList().get(0));
		Assert.assertEquals("a", property.getOptionsList().get(1));
		Assert.assertEquals("b", property.getOptionsList().get(2));

//		Assert.assertEquals("c-beskrivelse", property.getOptionDescriptions().get("c"));
//		Assert.assertEquals("a-beskrivelse", property.getOptionDescriptions().get("a"));
//		Assert.assertEquals("b-beskrivelse", property.getOptionDescriptions().get("b"));
	}

	/**
	 * Test av at beskrivelsen i valglister blir linket mot riktig valg 
	 */
//	@Test
//	public void testSetOptionDescriptions() {
//		Property property = new Property("property");
//		LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
//		options.put("c", "c-beskrivelse");
//		options.put("a", null);
//		options.put("b", "");
//		property.setOptions(options);
//		
//		Assert.assertEquals("c-beskrivelse", property.getOptionDescriptions().get("c"));
//		Assert.assertEquals("", property.getOptionDescriptions().get("a"));
//		Assert.assertEquals("", property.getOptionDescriptions().get("b"));
//	}
	
	/**
	 * Test av {@link Property#getNiceDatatypeName(Class)}
	 */
	@Test
	public void testGetNiceDatatypeName() {
		for (Class<?> datatype : Property.getDatatypesSupported()) {
			Assert.assertFalse(Property.getNiceDatatypeName(datatype).equals("<ukjent>"));
		}
	}
}
