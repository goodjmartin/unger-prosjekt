package no.goodtech.vaadin.lists.v7;

import com.vaadin.v7.ui.Table;

/**
 * Styles table rows according to the value of one specific column.
 * When the column value changes from one row to another, the style alternates
 */
@Deprecated
public class ColumnValueAlternateRowStyleGenerator implements Table.CellStyleGenerator {

	private final String evenStyleName, oddStyleName;
	private boolean odd = false;
	private Object lastValue = null;
	private final Object column;

	/**
	 * @param column the column that controls when the styles changes
	 */
	public ColumnValueAlternateRowStyleGenerator(Object column, String evenStyleName, String oddStyleName) {
		this.evenStyleName = evenStyleName;
		this.oddStyleName = oddStyleName;
		this.column = column;
	}

	@Override
	public String getStyle(Table table, Object itemId, Object propertyId) {
		if (propertyId == null) {
			Object value = table.getItem(itemId).getItemProperty(column).getValue();
			if (value == null) {
				if (lastValue != null) {
					changeStyle(value);
				}
			} else if (lastValue == null || !value.equals(lastValue)) {
				changeStyle(value);
			}
			return getCurrentStyle();
		}
		return null;
	}

	private String getCurrentStyle() {
		if (odd)
			return oddStyleName;
		else
			return evenStyleName;
	}

	private void changeStyle(Object value) {
		lastValue = value;
		odd = !odd;
	}
}
