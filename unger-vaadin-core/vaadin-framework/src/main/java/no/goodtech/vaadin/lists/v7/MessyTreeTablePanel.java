package no.goodtech.vaadin.lists.v7;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

/**
 * En tre-tabell med knapper nederst for å utvide og slå sammen nodene i treet.
 * Kjør {@link #refresh()} for å vise gui
 */
@Deprecated
public class MessyTreeTablePanel extends VerticalLayout {

	private MessyTreeTable table;
	private final HorizontalLayout buttonPanel;
	
	public MessyTreeTablePanel() {
		this.table = new MessyTreeTable(); 
		buttonPanel = new TreeTableButtonPanel(table);
		setSizeFull();
	}

	public void refresh() {
		if (getComponentCount() == 0) {
			addComponents(table, buttonPanel);
			setExpandRatio(table, 1);
		}
	}
	
	public MessyTreeTable getTable() {
		return table;
	}
}
