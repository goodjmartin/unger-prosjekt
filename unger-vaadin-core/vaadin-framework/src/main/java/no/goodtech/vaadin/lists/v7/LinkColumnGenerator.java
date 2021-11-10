package no.goodtech.vaadin.lists.v7;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.Link;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.ColumnGenerator;

/**
 * Formats a cell value as a link
 */
@Deprecated
public class LinkColumnGenerator implements ColumnGenerator {

	private final boolean openInNewWindow;
	private final String caption;
	private final Resource icon;
	private final boolean showCellValueAsCaption;
	
	/**
	 * Create column generator
	 * @param icon clickable icon. Null if not relevant
	 * @param openInNewWindow if true, the link will open in a new browser window
	 * @param showCellValueAsCaption if true, the url will be shown as a clickable link
	 */
	public LinkColumnGenerator(Resource icon, boolean openInNewWindow, boolean showCellValueAsCaption) {
		this(icon, openInNewWindow, null, showCellValueAsCaption);
	}

	/**
	 * Create column generator with fixed caption / link anchor
	 * @param icon clickable icon. Null if not relevant
	 * @param openInNewWindow if true, the link will open in a new browser window
	 * @param caption fixed caption / anchor. Use null if not relevant
	 */
	public LinkColumnGenerator(Resource icon, boolean openInNewWindow, String caption) {
		this(icon, openInNewWindow, caption, false);
	}

	private LinkColumnGenerator(Resource icon, boolean openInNewWindow, String caption, boolean showCellValueAsCaption) {
		this.openInNewWindow = openInNewWindow;
		this.caption = caption;
		this.icon = icon;
		this.showCellValueAsCaption = showCellValueAsCaption;
	}

	public Object generateCell(Table source, Object itemId, Object columnId) {
        
		final Item item = source.getItem(itemId);
		final Object cellValue = item.getItemProperty(columnId).getValue();
        if (cellValue != null) {
        	String url = (String) cellValue;
        	String caption = this.caption;
        	if (showCellValueAsCaption)
        		caption = url;
        	
        	Link link = new Link(caption, new ExternalResource(url));
        	link.setIcon(icon);
        	if (openInNewWindow)
        		link.setTargetName("_blank");
			return link;
        }
		return null;
	}
}
