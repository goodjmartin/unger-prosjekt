package no.goodtech.vaadin.lists.v7;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.ui.TreeTable;
import no.goodtech.vaadin.Icons;

/**
 * Knapper for å lukke eller åpne noder i et tre
 */
public class TreeTableButtonPanel extends HorizontalLayout {

	public TreeTableButtonPanel(final TreeTable table) {
		final Button expandButton = new Button("Utvid", new ClickListener() {
			public void buttonClick(ClickEvent event) {
				collapseOrExpandAll(false, table);
			}
		});
		expandButton.setIcon(Icons.get("expand"));
		
		final Button collapseButton = new Button("Slå sammen", new ClickListener() {
			public void buttonClick(ClickEvent event) {
				collapseOrExpandAll(true, table);
			}
		});
		collapseButton.setIcon(Icons.get("collapse"));
		
		addComponents(expandButton, collapseButton);
		setMargin(false);
	}
	
	/**
	 * Lukker eller åpner alle noder
	 * @param collapse true = lukker, false = åpner
	 */
	public void collapseOrExpandAll(boolean collapse, final TreeTable table) {
		for (Object node : table.getItemIds())
			if (table.hasChildren(node))
				table.setCollapsed(node, collapse);
	}
}
