package no.goodtech.admin.tabs.report;

import com.vaadin.v7.data.Property;

public interface IReportParameter {

	/**
	 * This method should be called to get the entered parameter value
	 *
	 * @return The entered parameter value
	 */
	String getValueAsString();
	
	/**
	 * @return a human readable version of the parameter value
	 */
	default String getNiceValue() {
		return getValueAsString();
	}
	
	/**
	 * To set default value from url query params etc.
	 * @param value new value
	 */
	void setValueAsString(String value);
	
	/**
	 * @return unique name of the parameter for this report. 
	 * Used for named parameter queries and to set default values from url
	 */
	String getName();
}
