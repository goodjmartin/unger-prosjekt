package no.goodtech.vaadin.lists;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.MenuBar;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * An improved {@link ListSelectionAwareComponentDisabler}.
 * This supports a more lean framework for adding new stuff to the disabler configuration.
 * Moreover, this framework supports a custom check if the components should be enabled or not.
 */
public class GridSelectionComponentDisabler<T> {

	private List<Component> components;
	private List<MenuBar.MenuItem> menuItems;
	private final Grid<T> grid;
	private Boolean singleRowSelectionRequired, multiRowSelectionRequired;
	private ISelectionOk selectionOk;
	private IContextAwareSelectionOk<T> contextAwareSelectionOk;

	public GridSelectionComponentDisabler(Grid<T> grid, Component... components) {
		this.components = Arrays.asList(components);
		this.grid = grid;
	}

	public GridSelectionComponentDisabler(Grid<T> grid, MenuBar.MenuItem... menuItems) {
		this.menuItems = Arrays.asList(menuItems);
		this.grid = grid;
	}

	/**
	 * Add some more components to control
	 */
	public GridSelectionComponentDisabler addComponents(Component... components) {
		if (this.components == null) {
			this.components = Arrays.asList(components);
		} else {
			this.components.addAll(Arrays.asList(components));
		}
		return this;
	}

	/**
	 * Add some more components to control
	 */
	public GridSelectionComponentDisabler addComponents(MenuBar.MenuItem... components) {
		if (this.menuItems == null) {
			this.menuItems = Arrays.asList(components);
		} else {
			this.menuItems.addAll(Arrays.asList(components));
		}
		return this;
	}

	/**
	 * Add a custom check to enable or disable the components
	 *
	 * @param selectionOk the interface called
	 */
	public GridSelectionComponentDisabler selectionOk(ISelectionOk selectionOk) {
		this.selectionOk = selectionOk;
		return this;
	}

	/**
	 * Add a custom check that can check the selected items of the grid to enable or disable the components
	 *
	 * @param contextAwareSelectionOk the interface called
	 */
	public GridSelectionComponentDisabler contextAwareSelectionOk(IContextAwareSelectionOk<T> contextAwareSelectionOk) {
		this.contextAwareSelectionOk = contextAwareSelectionOk;
		return this;
	}

	/**
	 * If this is set, components are only enabled if the grid has a single item selected
	 */
	public GridSelectionComponentDisabler singleRowSelectionRequired(Boolean singleRowSelectionRequired) {
		this.singleRowSelectionRequired = singleRowSelectionRequired;
		return this;
	}

	/*
	 * If this is set, components are only enabled if the grid has more than one item selected
	 */

	public GridSelectionComponentDisabler multiRowSelectionRequired(Boolean multiRowSelectionRequired) {
		this.multiRowSelectionRequired = multiRowSelectionRequired;
		return this;
	}

	/**
	 * Finish the configurer by hitting apply
	 */
	public void apply() {
		if (grid == null) {
			throw new RuntimeException("A grid is required.");
		}

		if (menuItems == null && components == null) {
			throw new RuntimeException("Any type of component to control is required.");
		}

		if (components != null) {
			for (Component componentToControl : components) {
				grid.getSelectionModel().addSelectionListener(event -> {
					final Set<T> selectedItems = event.getAllSelectedItems();
					enableOrDisableComponentsBasedOnSelection(selectedItems, componentToControl);
				});
				if (grid instanceof DataProvider) {
					ListComponentDisabler.disableWhenEmpty((DataProvider) grid, componentToControl);
				}
				initiallyDisableComponent(componentToControl, grid);
			}
		}
		if (menuItems != null) {
			for (MenuBar.MenuItem componentToControl : menuItems) {
				grid.getSelectionModel().addSelectionListener(event -> {
					//TODO: When a wrapper changes selectionmode the listneres doenst work anymore. Add functionality for this in setSelectionMode.
					final Set<T> selectedItems = event.getAllSelectedItems();
					enableOrDisableComponentsBasedOnSelection(selectedItems, componentToControl);
				});
				if (grid instanceof DataProvider) {
					ListComponentDisabler.disableWhenEmpty((DataProvider) grid, componentToControl);
				}
				initiallyDisableComponent(componentToControl, grid);
			}
		}
	}

	private void enableOrDisableComponentsBasedOnSelection(Set<T> selectedValues, Component componentToControl) {
		if (componentToControl != null)
			componentToControl.setEnabled(checkEnabled(selectedValues));
	}

	private void enableOrDisableComponentsBasedOnSelection(Set<T> selectedValues, MenuBar.MenuItem componentToControl) {
		if (componentToControl != null)
			componentToControl.setEnabled(checkEnabled(selectedValues));
	}

	private boolean checkEnabled(Set<T> selectedValues) {
		boolean enabled = false;
		if (selectedValues != null) {
			// Check multi select against single row selection required
			final int size = selectedValues.size();
			if (singleRowSelectionRequired != null && singleRowSelectionRequired)
				enabled = size == 1;
			else if (multiRowSelectionRequired !=null && multiRowSelectionRequired)
				enabled = size > 1;
			else
				enabled = size > 0;
		}
		if (selectionOk != null && enabled) {
			enabled = selectionOk.ok();
		}
		if (contextAwareSelectionOk != null && enabled) {
			enabled = contextAwareSelectionOk.ok(selectedValues);
		}

		return enabled;
	}


	private void initiallyDisableComponent(Component componentToControl, Grid tableToListenTo) {
		if (tableToListenTo.getSelectedItems().isEmpty()) {
			componentToControl.setEnabled(false);
		}
	}

	private void initiallyDisableComponent(MenuBar.MenuItem componentToControl, Grid tableToListenTo) {
		if (tableToListenTo.getSelectedItems().isEmpty()) {
			componentToControl.setEnabled(false);
		}
	}
}
