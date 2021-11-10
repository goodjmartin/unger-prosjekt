package no.goodtech.vaadin.lists.v7;

import com.vaadin.server.ThemeResource;
import no.goodtech.vaadin.lists.v7.MessyTreeTable.IMessyTreeTableRow;

import java.util.Arrays;
import java.util.List;

/**
 * En rad i {@link MessyTreeTable}
 */
@Deprecated
public class MessyTreeTableRow implements IMessyTreeTableRow {

	private final Object itemId;
	private final List<?> columns;
	private final Object parentItemId;
	private final ThemeResource icon;
	
	
	public MessyTreeTableRow(Object itemId, Object parentItemId, ThemeResource icon, Object... columns) {
		this.itemId = itemId;
		this.columns = Arrays.asList(columns);
		this.parentItemId = parentItemId;
		this.icon = icon;
	}

	
	public Object getItemId() {
		return itemId;
	}


	public List<?> getCellValues() {
		return columns;
	}


	public Object getParentItemId() {
		return parentItemId;
	}


	public ThemeResource getIcon() {
		return icon;
	}
}
