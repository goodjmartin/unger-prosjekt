package no.gooodtech.vaadin.remotecontrol.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.goodtech.opc.Globals;
import no.goodtech.opc.NodeValue;
import no.goodtech.vaadin.remotecontrol.metamodel.Widget;

/**
 * Hjelpefunksjoner for å kommunisere med OPC
 */
public class DataSource {
	
	/**
	 * Henter signal-verdier for angitte GUI-komponenter fra OPC
	 * @param widgets GUI-komponentene du skal hente verdier for
	 * @return verdier per GUI-komponent
	 */
	public static Map<Widget, NodeValue> readValues(List<Widget> widgets) {
		Map<Widget, NodeValue> result = new HashMap<Widget, NodeValue>();
		
		Set<String> tags = new HashSet<String>();
		Map<String, Widget> widgetsPerTag = new HashMap<String, Widget>();
		for (Widget widget : widgets) {
			final String tag = widget.getTag();
			tags.add(tag);
			widgetsPerTag.put(tag, widget);
		}
		final Map<String, NodeValue> nodeValues = Globals.getOpcRepository().getNodeValues(new ArrayList<String>(tags));
		for (Widget widget : widgets) {
			final String tag = widget.getTag();
			final NodeValue nodeValue = nodeValues.get(tag);
			result.put(widget, nodeValue);	
		}
		return result;
	}
	
	/**
	 * Lagre verdier i PLS
	 * @param valuesPerWidget verdier for hvert GUI-element
	 * @return null om det gikk bra, feilmelding hvis ikke
	 */
	public static String writeValues(Map<Widget, Object> valuesPerWidget) {
		Map<String, Object> nodeValues = new HashMap<String, Object>(); 
		for (Map.Entry<Widget, Object> entry : valuesPerWidget.entrySet()) {
			Widget widget = entry.getKey();
			String tag = widget.getTag();
			Object value = entry.getValue();
			//TODO: Sjekk og evt. konverter til riktig datatype
			nodeValues.put(tag, value);
		}
		if (nodeValues.size() > 0)
			return Globals.getOpcRepository().setNodeValues(nodeValues);
		else
			return "Ingen felter å lagre";
	}

}
