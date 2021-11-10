package no.goodtech.admin.tabs.report;

import com.vaadin.v7.ui.ComboBox;

import java.util.List;

public class NativeSelectParameter extends ComboBox implements IReportParameterComponent {
	
	private final String name;

	public NativeSelectParameter(String caption, List<SelectionEntry> selectionEntries, String name) {

		this.name = name;
		addStyleName("nativeSelectParameter");
		setCaption(caption);

		// The selection entries may never be null
		assert selectionEntries != null;

		refreshItems(selectionEntries);
	}

	public void refreshItems(List<SelectionEntry> selectionEntries){
		removeAllItems();
		// Set the selection values
		for (SelectionEntry selectionEntry : selectionEntries) {
			final String item = selectionEntry.getValue();
			addItem(item);
			final String itemCaption = selectionEntry.getKey();
			if (itemCaption == null)
				setItemCaption(item, "");
			else
				setItemCaption(item, itemCaption);
		}

		// Set the default value
		if (selectionEntries.size() > 0) {
			Object defaultKey = getItemIds().iterator().next();
			setValue(defaultKey);
			setNullSelectionAllowed(false);
		}
	}

	@Override
	public String getValueAsString() {
		return (String) getValue();
	}

	public void setValueAsString(String value) {
		setValue(value);
	}
	
	@Override
	public String getNiceValue() {
		Object value = getValue();
		if (value == null)
			return "";
		
		return getItemCaption(value);
	}

	public String getName() {
		return name;
	}
}
