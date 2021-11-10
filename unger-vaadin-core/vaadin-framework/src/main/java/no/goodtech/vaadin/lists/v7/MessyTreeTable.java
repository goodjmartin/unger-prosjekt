package no.goodtech.vaadin.lists.v7;

import com.vaadin.server.ThemeResource;
import com.vaadin.v7.ui.TreeTable;

import java.util.ArrayList;
import java.util.List;

/**
 * En tre-tabell med et litt hyggeligere grensesnitt.
 * Bruk {@link #refresh(List)} for å fylle tabellen med data.
 */
@Deprecated
public class MessyTreeTable extends TreeTable {

	public MessyTreeTable() {
		setSizeFull();
	}
	
	/**
	 * Oppfrisker innholdet i tabellen
	 * @param rows en liste av rader
	 */
	public void refresh(List<IMessyTreeTableRow> rows) {
    	removeAllItems();
    	final int maxColumnIndex = getContainerPropertyIds().size();
    	for (IMessyTreeTableRow row : rows) {
    		final ArrayList<?> columns = new ArrayList<Object>(row.getCellValues());

    		//legger til dummy-kolonner bakerst om denne raden har færre kolonner enn tabellen er konfigurert med
    		for (int i = 0; i < maxColumnIndex; i++)
    			if (i+1 > columns.size())
    				columns.add(null);  
    		
			final Object itemId = addItem(columns.toArray(), row.getItemId());
			final Object parentItemId = row.getParentItemId();
			if (parentItemId != null)
				setParent(itemId, parentItemId);
			
			final ThemeResource icon = row.getIcon();
			if (icon != null)
				setItemIcon(itemId, icon);
			
			setCollapsed(itemId, false);
		}
	}

	/**
	 * Utvider eller slår sammen alle noder
	 * @param expand true = utvid, false = slå sammen
	 */
	public void expandOrCollapseAll(boolean expand) {
		for (Object node : getItemIds())
			if (hasChildren(node))
				setCollapsed(node, expand);
	}

	public interface IMessyTreeTableRow {
		/**
		 * @return unik ID til raden. Du kan gjerne bruke entitets-objektet som raden representerer 
		 */
		Object getItemId();
		
		/**
		 * Innholdet i hver celle i denne raden.
		 * Kolonnene bør passe med kolonne-definisjonen du har angitt når du opprettet {@link MessyTreeTable},
		 * men du kan godt ha færre celler enn det er plass til. Da vil de siste cellene bli blanke.
		 * @return en liste av celle-verdier. 
		 */
		List<?> getCellValues();
		
		/**
		 * @return mora til denne raden. Null om raden skal være en rot-node
		 */
		Object getParentItemId();
		
		/**
		 * @return ikon til denne raden. Null om ingen ikon
		 */
		ThemeResource getIcon();
	}
}
