package no.goodtech.admin.tabs.report;

import com.vaadin.ui.Component;
import com.vaadin.v7.data.Property;

public interface IReportParameterComponent extends IReportParameter, Component.Focusable {

	/**
	 * Add a valueChangeListener to the report parameter
	 * @param valueChangeListener customized valueChangeListener
	 */
	void addValueChangeListener(Property.ValueChangeListener valueChangeListener);

}
