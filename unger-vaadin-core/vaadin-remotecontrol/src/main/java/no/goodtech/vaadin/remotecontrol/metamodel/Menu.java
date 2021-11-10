package no.goodtech.vaadin.remotecontrol.metamodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Hovedmeny for fjernkontroll-webapp
 */
public class Menu {
	
	private List<Screen> menuItems = new ArrayList<Screen>();

	/**
	 * @return skjermbilder som skal skal kunne velges fra menyen
	 */
	public List<Screen> getMenuItems() {
		return menuItems;
	}

	/**
	 * Angi hvilke skjermbilder som skal kunne velges fra menyen
	 * @param menuItems skjermbilder
	 */
	public void setMenuItems(List<Screen> menuItems) {
		this.menuItems = menuItems;
	}
}
