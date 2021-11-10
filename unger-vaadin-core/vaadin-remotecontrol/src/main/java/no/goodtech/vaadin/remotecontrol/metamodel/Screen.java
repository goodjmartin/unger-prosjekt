package no.goodtech.vaadin.remotecontrol.metamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * En et skjermbilde som innegolder en gruppe av GUI-komponenter {@link Widget}
 */
public class Screen {

	private String title;
	
//TODO	private long pollrate; //ms eller manuell oppfriskning
//TODO	private boolean autoCommit = true;

	private Map<String, Widget> widgetMap = new HashMap<String, Widget>();
	private List<Widget> widgets = new ArrayList<Widget>();
	private List<String> columns = new ArrayList<String>();

	/**
	 * @return overskrift (tekst som vises øverst på skjermen)
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Angi tekst som vises øverst på skjermen
	 * @param title overskrift
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return GUI-komponentene som skal vises i dette bildet i den rekkefølgen de skal vises
	 */
	public List<Widget> getWidgets() {
		return widgets;
	}

	/**
	 * Angi GUI-komponentene som skal vises i dette bildet i den rekkefølgen de skal vises
	 * @param widgets GUI-komponenter
	 */
	public void setWidgets(List<Widget> widgets) {
		this.widgets = widgets;
	}

	public void setWidgetMap(Map<String, Widget> widgetMap) {
		this.widgetMap = widgetMap;
	}

	public SortedSet<String> getColumns() {
		SortedSet<String> columns = new TreeSet<String>();
		for (Widget widget : widgets)
			if (widget.getColumn() != null)
				columns.add(widget.getColumn());
		return columns;
	}
	
	/**
	 * @return alle ledetekster i riktig rekkefølge
	 */
	public List<String> getCaptions() {
		List<String> captions = new ArrayList<String>();
		Set<String> uniqueCaptions = new HashSet<String>();
		for (Widget widget : widgets) {
			final String caption = widget.getCaption();
			if (uniqueCaptions.add(caption))
				captions.add(caption);
		}
		return captions;
	}

	/**
	 * @param caption ledeteksten du er interessert i
	 * @return alle felt med denne ledeteksten, i riktig rekkefølge
	 */
	public List<Widget> getWidgets(String caption) {
		List<Widget> result = new ArrayList<Widget>();
		for (Widget widget : widgets)
			if (caption.equals(widget.getCaption()))
				result.add(widget);
		return result;
	}

	

	
	
	
	
}
