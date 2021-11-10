package no.goodtech.vaadin.lists;

import com.vaadin.data.provider.DataChangeEvent;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.DataProviderListener;
import com.vaadin.data.provider.Query;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.MenuBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListComponentDisabler {

	private static final Logger logger = LoggerFactory.getLogger(ListComponentDisabler.class);

	public static void control(DataProvider tableToListenTo, Component ... componentsToControl) {
		for (Component component : componentsToControl)
			new ListComponentDisabler(component, tableToListenTo, false);
	}

	public static void control(Grid<?> tableToListenTo, Component ... componentsToControl) {
		for (Component component : componentsToControl)
			new ListComponentDisabler(component, tableToListenTo.getDataProvider(), false);
	}

	public static void disableWhenEmpty(DataProvider tableToListenTo, Component ... componentsToControl) {
		for (Component component : componentsToControl)
			new ListComponentDisabler(component, tableToListenTo, true);
	}

	public static void disableWhenEmpty(Grid<?> tableToListenTo, Component ... componentsToControl) {
		for (Component component : componentsToControl)
			new ListComponentDisabler(component, tableToListenTo.getDataProvider(), true);
	}

	public static void control(DataProvider tableToListenTo, MenuBar.MenuItem ... componentsToControl) {
		for (MenuBar.MenuItem component : componentsToControl)
			new ListComponentDisabler(component, tableToListenTo, false);
	}

	public static void control(Grid<?> tableToListenTo, MenuBar.MenuItem ... componentsToControl) {
		for (MenuBar.MenuItem component : componentsToControl)
			new ListComponentDisabler(component, tableToListenTo.getDataProvider(), false);
	}

	public static void disableWhenEmpty(DataProvider tableToListenTo, MenuBar.MenuItem ... componentsToControl) {
		for (MenuBar.MenuItem component : componentsToControl)
			new ListComponentDisabler(component, tableToListenTo, true);
	}

	public static void disableWhenEmpty(Grid<?> tableToListenTo, MenuBar.MenuItem ... componentsToControl) {
		for (MenuBar.MenuItem component : componentsToControl)
			new ListComponentDisabler(component, tableToListenTo.getDataProvider(), true);
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

	private ListComponentDisabler(final MenuBar.MenuItem componentToControl, final DataProvider dataProvider, boolean disableOnly) {
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
