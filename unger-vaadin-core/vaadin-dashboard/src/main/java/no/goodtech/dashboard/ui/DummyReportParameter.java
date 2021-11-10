package no.goodtech.dashboard.ui;

import com.vaadin.ui.Component;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.TextField;
import no.goodtech.admin.tabs.report.IReportParameter;

public class DummyReportParameter implements IReportParameter {

	private String name, value;

	public DummyReportParameter(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public String getValueAsString() {
		return value;
	}

	@Override
	public String getNiceValue() {
		return value;
	}

	@Override
	public void setValueAsString(String value) {
	 this.value = value;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
