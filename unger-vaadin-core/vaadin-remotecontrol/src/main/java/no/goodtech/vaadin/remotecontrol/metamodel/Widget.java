package no.goodtech.vaadin.remotecontrol.metamodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Et GUI-element som kan vise og evt. endre en signalverdi
 */
public class Widget {

	private String caption;
	
	private boolean readOnly = true;
	
	private String tag;
	
	private Class<?> dataType;
	
	private List<Object> options = new ArrayList<Object>();;
	
	private String column;
	
	private Object initialValue = null;
	
	private Object value = null;

	/**
	 * @return ledetekst
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * Angi ledetekst
	 * @param caption tekst som skal vises foran GUI-elementet
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/**
	 * @return TRUE om verdien ikke kan endres
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * Angi om verdien kan endres eller ikke
	 * @param readOnly TRUE = verdien kan IKKE endres, FALSE = verdien kan endres
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * @return OPC-tag-adressen til signalet
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * Angi OPC-tag-adressen til signalet
	 * @param tag adressen til signalet
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * @return datatype til verdien
	 */
	public Class<?> getDataType() {
		return dataType;
	}

	/**
	 * Angi datatype til verdien
	 * @param dataType datatype
	 */
	public void setDataType(Class<?> dataType) {
		this.dataType = dataType;
	}

	/**
	 * Angi hvilke valg man har for verdien
	 * @param options valg
	 */
	public void setOptions(List<String> options) {
		for (String option : options) {
			if (dataType.equals(String.class))
				this.options.add(option);
			else if (dataType.equals(Short.class))
				this.options.add(Short.valueOf(option));
			else if (dataType.equals(Integer.class))
				this.options.add(Integer.valueOf(option));
//TODO!	er usikker på om vi skal konvertere til aktuell datatype før vi viser valgene eller etter bruker har valgt
//TODO! koden over er bare for testing
		}
	}
	
	/**
	 * @return tillatte valg for verdi. Tom liste hvis fritt valg
	 */
	public List<Object> getOptions() {
		return options;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}
	
//	public boolean isDirty(Object newValue) {
//		if (initialValue == null && newValue == null)
//			return false;
//		if (initialValue == null && newValue != null)
//			return true;
//		if(initialValue.toString().equals(newValue.toString()))
//			return false;
//		return true;
//	}
//
	public boolean isDirty() {
		if (initialValue == null && value == null)
			return false;
		if (initialValue == null && value != null)
			return true;
		if(initialValue.toString().equals(value.toString()))
			return false;
		return true;
	}

	public void setInitialValue(Object initialValue) {
		this.initialValue = initialValue;
		this.value = initialValue;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	
	
	
}
