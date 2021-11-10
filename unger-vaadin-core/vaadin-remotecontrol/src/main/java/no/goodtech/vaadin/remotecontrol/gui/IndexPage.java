package no.goodtech.vaadin.remotecontrol.gui;

import no.goodtech.vaadin.global.VaadinSpringContextHelper;
import no.goodtech.vaadin.remotecontrol.metamodel.Menu;
import no.goodtech.vaadin.remotecontrol.metamodel.Screen;
import org.vaadin.touchkit.ui.NavigationButton;
import org.vaadin.touchkit.ui.NavigationView;
import org.vaadin.touchkit.ui.VerticalComponentGroup;

import java.util.List;


public class IndexPage extends NavigationView {
	public IndexPage() {
		setSizeFull();
		List<Screen> menuItems = VaadinSpringContextHelper.getBean(Menu.class).getMenuItems();
		VerticalComponentGroup menuItemsLayouts = new VerticalComponentGroup();
		setCaption("Hovedmeny");
		for (Screen screen : menuItems) {
			TablePanel screenPage = new TablePanel(screen, this);
			NavigationButton navigationButton = new NavigationButton(screen.getTitle());
			navigationButton.setTargetView(screenPage);
			menuItemsLayouts.addComponent(navigationButton);
		}
		setContent(menuItemsLayouts);
	}
}