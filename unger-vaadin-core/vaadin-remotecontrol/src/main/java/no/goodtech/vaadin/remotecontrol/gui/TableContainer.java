package no.goodtech.vaadin.remotecontrol.gui;

import com.vaadin.ui.Component;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.TextField;
import no.goodtech.opc.NodeValue;
import no.goodtech.vaadin.remotecontrol.metamodel.Screen;
import no.goodtech.vaadin.remotecontrol.metamodel.Widget;
import no.gooodtech.vaadin.remotecontrol.data.DataSource;
import org.vaadin.touchkit.ui.Switch;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: rsan
 * Date: 07.08.13
 * Time: 10:22
 * To change this template use File | Settings | File Templates.
 */
public class TableContainer extends IndexedContainer {
	public TableContainer( Screen menuItems) {
		addContainerProperty("Tag",String.class,null);
			addContainerProperty(menuItems.getTitle(), Component.class, null);
			Map<Widget, NodeValue> widgetNodeValueMap = DataSource.readValues(menuItems.getWidgets());
			for(Map.Entry<Widget, NodeValue> entry : widgetNodeValueMap.entrySet()) {
				final Item item = addItem(entry);
				Widget widget = entry.getKey();
				NodeValue value = entry.getValue();
				item.getItemProperty("Tag").setValue(widget.getCaption());
				System.out.println(widget.getDataType());
				if(widget.getDataType().equals(java.lang.Boolean.class)){
					Switch aSwitch = new Switch();
					aSwitch.setValue(value.getValue() != null ? (Boolean) value.getValue() : false);
					item.getItemProperty(menuItems.getTitle()).setValue(aSwitch);
				}
				else {
					TextField textField = new TextField();
					textField.setValue(value.getValue() != null ? value.getValue().toString() : "");
					item.getItemProperty(menuItems.getTitle()).setValue(textField);

				}
			}
	}

	public void refresh(Screen menuItems) {
		removeAllItems();
		Map<Widget, NodeValue> widgetNodeValueMap = DataSource.readValues(menuItems.getWidgets());
		for(Map.Entry<Widget, NodeValue> entry : widgetNodeValueMap.entrySet()) {
			final Item item = addItem(entry);
			Widget widget = entry.getKey();
			NodeValue value = entry.getValue();
			item.getItemProperty("Tag").setValue(widget.getCaption());
			System.out.println(widget.getDataType());
			if(widget.getDataType().equals(java.lang.Boolean.class)){
				Switch aSwitch = new Switch();
				aSwitch.setValue(value.getValue() != null ? (Boolean) value.getValue() : false);
				item.getItemProperty(menuItems.getTitle()).setValue(aSwitch);
			}
			else {
				TextField textField = new TextField();
				textField.setValue(value.getValue() != null ? value.getValue().toString() : "");
				item.getItemProperty(menuItems.getTitle()).setValue(textField);

			}
		}
	}
}



