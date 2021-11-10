package no.goodtech.dashboard.ui.admin;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.CustomField;
import com.vaadin.v7.ui.TextField;
import no.goodtech.dashboard.Globals;
import no.goodtech.dashboard.config.fetcher.FetcherConfig;
import no.goodtech.dashboard.model.IDashboardFetcher;
import no.goodtech.dashboard.ui.Texts;

import java.util.Map;

/**
 * A field which represents a series ID.
 * You may select an ID from a list (if the fetcher provides it) or type an ID yourself
 */
public class SeriesIdField extends CustomField<String> {

	private final TextField textField;
	private final ComboBox comboBox;

	public SeriesIdField(String caption) {
		comboBox = new ComboBox(Texts.get("seriesConfig.form.id.caption"));
		comboBox.setWidth("100%");
		comboBox.setNullSelectionAllowed(false);
		comboBox.setVisible(false);
		comboBox.setFilteringMode(Globals.getSeriesIdFilteringMode());

		textField = createTextField(caption);
		textField.setVisible(true);
	}

	public void refresh(FetcherConfig fetcherConfig) {
		if (fetcherConfig != null) {
			final IDashboardFetcher fetcher = fetcherConfig.createFetcher(0);
			refresh(fetcher.listSeriesIds());
		} else {
			clear();
		}
	}

	public void refresh(Map<String, String> seriesIds) {
		if (seriesIds != null && seriesIds.size() > 0) {
			textField.setVisible(false);
			comboBox.clear();
			for (Map.Entry<String, String> entry : seriesIds.entrySet()) {
				comboBox.addItem(entry.getKey());
				comboBox.setItemCaption(entry.getKey(), entry.getValue());
			}
			comboBox.setVisible(true);
		} else {
			comboBox.setVisible(false);
			textField.setVisible(true);
		}
		super.setInternalValue(null);
	}

	private TextField createTextField(String caption) {
		TextField field = new TextField(caption);
		field.setWidth("100%");
		field.setNullRepresentation("");
		field.setImmediate(true);
		return field;
	}

	protected Component initContent() {
		HorizontalLayout content = new HorizontalLayout(textField, comboBox);
		content.setMargin(false);
		content.setWidth("100%");
		return content;
	}

	public Class<? extends String> getType() {
		return String.class;
	}
	
	protected void setInternalValue(String newValue) {
		super.setInternalValue(newValue);
		if (textField.isVisible()) {
			textField.setValue(newValue);
		} else if (comboBox.isVisible()) {
			comboBox.setValue(newValue);
		}
	}
	
	protected String getInternalValue() {
		String value = null;
		if (textField.isVisible()) {
			value = textField.getValue();
		} else if (comboBox.isVisible()) {
			value = (String) comboBox.getValue();
		}
		if (value == null || value.trim().length() == 0)
			return null;
		return value;
	}

	/**
	 * @return user-friendly name of chosen series or ID if no separate name is available
	 */
	public String getSeriesName() {
		String value = null;
		if (textField.isVisible()) {
			value = textField.getValue();
		} else if (comboBox.isVisible()) {
			String id  = (String) comboBox.getValue();
			if (id != null) {
				value = comboBox.getItemCaption(id);
			}
		}
		if (value == null || value.trim().length() == 0)
			return null;
		return value;
	}

	@Override
	public void addValueChangeListener(ValueChangeListener listener) {
		comboBox.addValueChangeListener(listener);
		textField.addValueChangeListener(listener);
	}

	//	@Override
//	public void validate() throws InvalidValueException {
//		super.validate();
//		final String errorMessage = validateNice();
//		if (errorMessage != null)
//			throw new InvalidValueException(errorMessage);
//	}
}
