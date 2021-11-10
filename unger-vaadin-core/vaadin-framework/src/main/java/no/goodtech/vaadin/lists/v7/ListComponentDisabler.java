package no.goodtech.vaadin.lists.v7;

import com.vaadin.data.provider.DataChangeEvent;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.DataProviderListener;
import com.vaadin.data.provider.Query;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.v7.data.Container.ItemSetChangeEvent;
import com.vaadin.v7.data.Container.ItemSetChangeListener;
import com.vaadin.v7.data.Container.ItemSetChangeNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListComponentDisabler {

	private static final Logger logger = LoggerFactory.getLogger(ListComponentDisabler.class);

	/**
	 * Deaktiverer/ aktiverer aktuelle komponenter avhengig av om det er noen data i angitt liste/tabell
	 * @param componentsToControl knapper, tekstfelt e.l. som du oensker aa enable/disable
	 * @param tableToListenTo lista/tabellen som skal styre komponentene
	 */
	public static void control(ItemSetChangeNotifier tableToListenTo, Component... componentsToControl) {
		for (Component component : componentsToControl)
			new ListComponentDisabler(component, tableToListenTo, false);
	}

	public static void control(DataProvider tableToListenTo, Component... componentsToControl) {
		for (Component component : componentsToControl)
			new ListComponentDisabler(component, tableToListenTo, false);
	}

	public static void control(Grid<?> tableToListenTo, Component... componentsToControl) {
		for (Component component : componentsToControl)
			new ListComponentDisabler(component, tableToListenTo.getDataProvider(), false);
	}

	public static void disableWhenEmpty(ItemSetChangeNotifier tableToListenTo, Component... componentsToControl) {
		for (Component component : componentsToControl)
			new ListComponentDisabler(component, tableToListenTo, true);
	}

	public static void disableWhenEmpty(DataProvider tableToListenTo, Component... componentsToControl) {
		for (Component component : componentsToControl)
			new ListComponentDisabler(component, tableToListenTo, true);
	}

	public static void disableWhenEmpty(Grid<?> tableToListenTo, Component... componentsToControl) {
		for (Component component : componentsToControl)
			new ListComponentDisabler(component, tableToListenTo.getDataProvider(), true);
	}

	private ListComponentDisabler(final Component componentToControl, final ItemSetChangeNotifier tableToListenTo, boolean disableOnly) {
		tableToListenTo.addItemSetChangeListener(new ItemSetChangeListener() {
			
			public void containerItemSetChange(ItemSetChangeEvent event) {
				final int size = event.getContainer().getItemIds().size();
				logger.debug("Antall rader i tabell {} : {}", tableToListenTo.getClass(), size);
				if(size == 0) {
					componentToControl.setEnabled(false);
				} else if (!disableOnly) {
					componentToControl.setEnabled(true);
				}
			}
		});		
	}

	private ListComponentDisabler(final Component componentToControl, final DataProvider dataProvider, boolean disableOnly) {
		dataProvider.addDataProviderListener(new DataProviderListener() {
			@Override
			public void onDataChange(DataChangeEvent event) {
				final int size = event.getSource().size(new Query());
				logger.debug("Antall rader i tabell {} : {}", dataProvider.getClass(), size);
				if(size == 0) {
					componentToControl.setEnabled(false);
				} else if (!disableOnly) {
					componentToControl.setEnabled(true);
				}
			}
		});
	}
}
