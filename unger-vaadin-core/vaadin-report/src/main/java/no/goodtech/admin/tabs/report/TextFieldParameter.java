package no.goodtech.admin.tabs.report;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.TextField;
import no.goodtech.admin.report.TextFieldParameterType;

public class TextFieldParameter extends TextField implements IReportParameterComponent {

	private String parameterValue = "";
	private final boolean wideSearch;
	private final String name;

	public TextFieldParameter(TextFieldParameterType type) {

		// Add a style name
		addStyleName("textFieldParameter");

		// Set the caption
		setCaption(type.getLabel());

		this.name = type.getName();
		this.wideSearch = type.getWideSearch();

		// Add ValueChangeListener
		addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				parameterValue = (String)event.getProperty().getValue();
			}
		});

		// Set immediate rendering
		setImmediate(true);
	}
	
	@Override
	public String getValueAsString() {
		return wideSearch ? ("%" + parameterValue + "%") : parameterValue;
	}

	public String getName() {
		return name;
	}

	public void setValueAsString(String value) {
		setValue(value);
	}
}
