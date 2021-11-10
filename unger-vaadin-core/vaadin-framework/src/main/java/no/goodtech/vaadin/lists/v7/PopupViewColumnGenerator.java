package no.goodtech.vaadin.lists.v7;

import com.vaadin.ui.PopupView;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.ColumnGenerator;

/**
 * Formats a cell value as a PopupView. The cell content will be shown when the user clicks the link
 */
@Deprecated
public class PopupViewColumnGenerator implements ColumnGenerator {

	private final String caption;
	private final ContentMode contentMode;
	
	/**
	 * Create column generator
	 * @param caption fixed caption / anchor. Use null if not relevant
	 * @param contentMode how to format popup text
	 */
	public PopupViewColumnGenerator(String caption, ContentMode contentMode) {
		this.caption = caption;
		this.contentMode = contentMode;
	}

	public Object generateCell(Table source, Object itemId, Object columnId) {
        
		final Item item = source.getItem(itemId);
		final Object cellValue = item.getItemProperty(columnId).getValue();
        if (cellValue != null) {
        	String caption = this.caption;
        	Label content = new Label(String.valueOf(cellValue));
        	content.setContentMode(contentMode);
        	PopupView popupView = new PopupView(caption, content);
			return popupView;
        }
		return null;
	}
}
