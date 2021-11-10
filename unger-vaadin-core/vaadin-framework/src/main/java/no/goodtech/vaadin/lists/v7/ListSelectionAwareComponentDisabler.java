package no.goodtech.vaadin.lists.v7;

import com.vaadin.data.HasValue;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.v7.data.Container.ItemSetChangeEvent;
import com.vaadin.v7.data.Container.ItemSetChangeListener;
import com.vaadin.v7.data.Container.ItemSetChangeNotifier;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.data.Property.ValueChangeNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;

/**
 * Enable or disable context sensitive controls based on table or list selection
 * Hvis man har valgt en rad i angitt liste, vil komponentene du har angitt bli enablet
 * Hvis man ikke har valgt i angitt liste, vil komponentene du har angitt bli disablet
 * Basert på tilsvarende funksjonalitet for Swing som finnes under samme navn i Utils-prosjektet 
 * @author oystein
 */
public class ListSelectionAwareComponentDisabler {
	private static final Logger logger = LoggerFactory.getLogger(ListSelectionAwareComponentDisabler.class);

	/**
	 * Disable given components if no rows are selected in given table or list
	 * @param tableToListenTo the list or table that will control the components
	 * @param componentsToControl buttons, textfields and such wich you want to enable/disable
	 */
	public static void control(ValueChangeNotifier tableToListenTo, Component... componentsToControl) {
		for (Component component : componentsToControl)
			new ListSelectionAwareComponentDisabler(component, false, tableToListenTo);
	}

	/**
	 * Disable given components if no rows are selected in given table or list
	 * @param tableToListenTo the list or table that will control the components
	 * @param componentsToControl buttons, textfields and such wich you want to enable/disable
	 */
	public static void control(HasValue tableToListenTo, Component... componentsToControl) {
		for (Component component : componentsToControl)
			new ListSelectionAwareComponentDisabler(component, false, tableToListenTo);
	}

	/**
	 * Disable given components if no rows are selected in given table or list
	 * @param tableToListenTo the list or table that will control the components
	 * @param componentsToControl buttons, textfields and such wich you want to enable/disable
	 */
	public static void control(Grid tableToListenTo, Component... componentsToControl) {
		for (Component component : componentsToControl)
			new ListSelectionAwareComponentDisabler(component, false, tableToListenTo);
	}

	/**
	 * Disable given components if no rows are selected in given table or list
	 * @param tableToListenTo the list or table that will control the components
	 * @param singleRowSelectionRequired set to true if you require that only one row must be selected to enable components. Not relevant for single selection mode
	 * @param componentsToControl buttons, text fields and such which you want to enable/disable
	 */
	public static void control(ValueChangeNotifier tableToListenTo, final boolean singleRowSelectionRequired, Component... componentsToControl) {
		for (Component component : componentsToControl)
			new ListSelectionAwareComponentDisabler(component, singleRowSelectionRequired, tableToListenTo);
	}

	/**
	 * Disable given components if no rows are selected in given table or list
	 * @param tableToListenTo the list or table that will control the components
	 * @param singleRowSelectionRequired set to true if you require that only one row must be selected to enable components. Not relevant for single selection mode
	 * @param componentsToControl buttons, text fields and such which you want to enable/disable
	 */
	public static void control(HasValue tableToListenTo, final boolean singleRowSelectionRequired, Component... componentsToControl) {
		for (Component component : componentsToControl)
			new ListSelectionAwareComponentDisabler(component, singleRowSelectionRequired, tableToListenTo);
	}

	/**
	 * Disable given components if no rows are selected in given table or list
	 * @param tableToListenTo the list or table that will control the components
	 * @param singleRowSelectionRequired set to true if you require that only one row must be selected to enable components. Not relevant for single selection mode
	 * @param componentsToControl buttons, text fields and such which you want to enable/disable
	 */
	public static void control(Grid tableToListenTo, final boolean singleRowSelectionRequired, Component... componentsToControl) {
		for (Component component : componentsToControl)
			new ListSelectionAwareComponentDisabler(component, singleRowSelectionRequired, tableToListenTo);
	}

	private ListSelectionAwareComponentDisabler(final Component componentToControl, final boolean singleRowSelectionRequired, final ValueChangeNotifier tableToListenTo) {
		tableToListenTo.addValueChangeListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				final Object value = event.getProperty().getValue();
				logger.debug("Valgte rader i tabell {} : {}", tableToListenTo.getClass(), value);
				enableOrDisableComponentsBasedOnSelection(value, singleRowSelectionRequired, componentToControl);
			}
		});
		
		if (tableToListenTo instanceof ItemSetChangeNotifier) {
			//will disable component if table/list is empty 
			ItemSetChangeNotifier notifier = (ItemSetChangeNotifier) tableToListenTo;
			notifier.addItemSetChangeListener(new ItemSetChangeListener() {
				
				public void containerItemSetChange(ItemSetChangeEvent event) {
					final int size = event.getContainer().getItemIds().size();
					ListComponentDisabler.disableWhenEmpty(notifier, componentToControl);
				}
			});
		}
	}

	private ListSelectionAwareComponentDisabler(final Component componentToControl, final boolean singleRowSelectionRequired, final HasValue tableToListenTo) {
		tableToListenTo.addValueChangeListener(new HasValue.ValueChangeListener() {
			@Override
			public void valueChange(HasValue.ValueChangeEvent event) {
				final Object value = event.getValue();
				logger.debug("Valgte rader i tabell {} : {}", tableToListenTo.getClass(), value);
				enableOrDisableComponentsBasedOnSelection(value, singleRowSelectionRequired, componentToControl);
			}
		});
		if (tableToListenTo instanceof DataProvider) {
			ListComponentDisabler.control((DataProvider) tableToListenTo, componentToControl);
		}
	}

	private ListSelectionAwareComponentDisabler(final Component componentToControl, final boolean singleRowSelectionRequired, final Grid tableToListenTo) {
		tableToListenTo.getSelectionModel().addSelectionListener(new SelectionListener() {
			@Override
			public void selectionChange(SelectionEvent event) {
				final Set selectedItems = event.getAllSelectedItems();
				logger.debug("Valgte rader i tabell {} : {}", tableToListenTo.getClass(), selectedItems);
				enableOrDisableComponentsBasedOnSelection(selectedItems, singleRowSelectionRequired, componentToControl);
			}
		});
		if (tableToListenTo instanceof DataProvider) {
			ListComponentDisabler.disableWhenEmpty((DataProvider) tableToListenTo, componentToControl);
		}
	}

	private void enableOrDisableComponentsBasedOnSelection(Object value, boolean singleRowSelectionRequired, Component componentToControl) {
		boolean enabled = false;
		if (value != null) {
			if (value instanceof Collection<?>) {
				//for multiselect
				final int size = ((Collection<?>) value).size();
				if (singleRowSelectionRequired)
					enabled = size == 1;
				else
					enabled = size > 0;
			} else {
				enabled = true; //single select
			}
		}
		if (componentToControl != null)
			componentToControl.setEnabled(enabled);
	}
}
