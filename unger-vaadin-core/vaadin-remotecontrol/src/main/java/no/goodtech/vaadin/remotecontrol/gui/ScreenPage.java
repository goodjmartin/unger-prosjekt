package no.goodtech.vaadin.remotecontrol.gui;

import com.vaadin.v7.ui.NativeSelect;
import com.vaadin.v7.ui.TextField;
import no.goodtech.opc.NodeValue;
import no.goodtech.vaadin.remotecontrol.metamodel.Screen;
import no.goodtech.vaadin.remotecontrol.metamodel.Widget;
import no.gooodtech.vaadin.remotecontrol.data.DataSource;
import org.vaadin.touchkit.ui.NavigationView;
import org.vaadin.touchkit.ui.VerticalComponentGroup;

import java.util.List;
import java.util.Map;

public class ScreenPage extends NavigationView {
	public ScreenPage(Screen screen, IndexPage indexPage) {
		setSizeFull();
		setPreviousComponent(indexPage);
		setCaption(screen.getTitle());
		VerticalComponentGroup widgetItemsLayout = new VerticalComponentGroup();
		final List<Widget> widgets = screen.getWidgets();
		for (Widget widget : widgets) {
			final Map<Widget, NodeValue> valuesPerWidget = DataSource.readValues(widgets);
			final NodeValue nodeValue = valuesPerWidget.get(widget);
			if (widget.getOptions().size() > 0) {
				NativeSelect select = new NativeSelect(widget.getCaption());
				select.setNullSelectionAllowed(false);
				for (Object option : widget.getOptions())
					select.addItem(option);
				widgetItemsLayout.addComponent(select);
			} else {
				widgetItemsLayout.addComponent(new TextField(widget.getCaption() + ": " + nodeValue.getValue()));
			}
				
//			WidgetPage widgetPage = new WidgetPage(widget, this);
//			NavigationButton navigationButton = new NavigationButton(widget.getCaption());
//			navigationButton.setTargetView(widgetPage);
//			widgetItemsLayout.addComponent(navigationButton);
		}
		setContent(widgetItemsLayout);
	}
}
