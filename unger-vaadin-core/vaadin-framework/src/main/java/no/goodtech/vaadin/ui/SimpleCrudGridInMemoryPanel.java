package no.goodtech.vaadin.ui;

import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.UI;
import no.goodtech.vaadin.buttons.AddButton;
import no.goodtech.vaadin.buttons.CopyButton;
import no.goodtech.vaadin.buttons.DetailButton;
import no.goodtech.vaadin.buttons.RemoveButton;
import no.goodtech.vaadin.layout.ComponentWrapper;
import no.goodtech.vaadin.lists.ICopyable;
import no.goodtech.vaadin.lists.MessyGrid;
import no.goodtech.vaadin.main.SimpleInputBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * A generic crud panel for working with a list of objects in memory.
 * You are responsible for providing the data for the panel and implement the methods for updating the data
 * @param <LISTITEM> the type of the objects you want to handle.
 * If the type supports {@link ICopyable}, you will have a copy button available
 */
public abstract class SimpleCrudGridInMemoryPanel<LISTITEM>	extends ComponentWrapper
		implements ListProvider {


	private final MessyGrid<LISTITEM> grid;
	protected final Set<LISTITEM> items;
	private MenuBar.MenuItem removeButton, addButton, copyButton, detailsButton;
	private boolean readOnly;

	/**
	 * Create a form to show or edit the entity
	 * @param item the object to show/edit
	 * @param isNew true = the object is new, false = the object already exists in the grid
	 * @return a popup including the form
	 */
	protected abstract SimpleInputBox.IinputBoxContent createDetailForm(LISTITEM item, boolean isNew);

	/**
	 * @return a new entity
	 */
	protected abstract LISTITEM create();

	/**
	 * @return with on popup in %. If null, it will scale based on the content
	 */
	protected Integer getDetailsPopupWidth() {
		return null;
	}

	/**
	 * Please override this if you want to check anything before deletion
	 *
	 * @param LISTITEM the object to delete
	 * @return null if we can delete, or error message if not
	 */
	protected String isDeleteOk(LISTITEM LISTITEM) {
		return null;
	}

		/**
		 * Create CRUD component. You must call {@link #refresh()} to fill it with data afterwords
		 * @param caption the caption of the CRUD component
		 * @param items the items you like to CRUD. See {@link #clone(Set)}
		 * @param grid the grid to display the list of objects you like to CRUD
		 * @param readOnly if true, add, edit and delete will not be allowed. You may also call {@link #setReadOnly(boolean)} later
		 */
	public SimpleCrudGridInMemoryPanel(String caption, Set<LISTITEM> items, MessyGrid<LISTITEM> grid, boolean readOnly) {
		this.items = clone(items);
		this.grid = grid;
		this.readOnly = readOnly;

//TODO:
//		Panel panel = createShortcutListenersInPanel();
		if (caption != null) {
			grid.setCaption(caption);
		}
		wrapComponent(grid);

		this.addButton = addButton(new AddButton(null), selectedItem -> add());
		this.detailsButton = addButton(new DetailButton(null), selectedItem -> details());
		this.removeButton = addButton(new RemoveButton(null), selectedItem -> delete());
		this.copyButton = addButton(new CopyButton(null), selectedItem -> copy());

		for (MenuBar.MenuItem menuItem : Arrays.asList(addButton, removeButton, copyButton)) {
			menuItem.setEnabled(!readOnly);
		}

		//doubleclick opens details panel
		grid.addItemClickListener(event -> {
			if (event.getMouseEventDetails().isDoubleClick() && detailsButton.isVisible()) {
				Set<LISTITEM> selectedObjects = grid.getSelectedItems();
				if (selectedObjects.size() <= 1) {
					final Object item = event.getItem();
					if (item != null)
						popupDetails((LISTITEM) item, false);
				} else {
					details();
				}
			}
		});

		setMargin(false);
		setSizeFull();
	}

	public void refresh() {
		final List<LISTITEM> items = getItems();
		grid.refresh(items);
		if (!readOnly && items.size() > 0 && items.iterator().next() instanceof ICopyable) {
			copyButton.setVisible(true);
		} else {
			copyButton.setVisible(false);
		}
	}

	private void add() {
		popupDetails(create(), true);
	}

	private void details() {
		final Set<LISTITEM> selectedObjects = grid.getSelectedItems();
		if (selectedObjects.size() >= 1) {
			popupDetails(selectedObjects.iterator().next(), false);
		}
	}

	private void popupDetails(LISTITEM item, boolean isNew) {
		SimpleInputBox.IinputBoxContent form;
		form = createDetailForm(item, isNew);
		if (form != null) {
			final SimpleInputBox popup = new SimpleInputBox(form, new SimpleInputBox.IConfirmListener() {
				public void onConfirm() {
					if (isNew) {
						add(item);
						grid.select(item);
					}
					refresh();
				}
			});
			popup.setReadOnly(readOnly);
			UI.getCurrent().addWindow(popup);
			final Integer popupWidth = getDetailsPopupWidth();
			if (popupWidth != null) {
				popup.setWidth(popupWidth, Unit.PERCENTAGE);
			}
			popup.focus();
		}
	}

	protected void delete() {
		//TODO: Confirmation, see SimpleCrudAdminPanel
		for (LISTITEM entityStub : grid.getSelectedItems()) {
			remove(entityStub);
		}
		refresh();
	}

	private void copy() {
		final Set<LISTITEM> selectedItems = grid.getSelectedItems();
		boolean copyPerformed = false;
		for (LISTITEM object : grid.getSelectedItems()) {
			if (object instanceof ICopyable) {
				ICopyable source = (ICopyable) object;
				LISTITEM copy = (LISTITEM) source.copy();
				add(copy);
				copyPerformed = true;
			}
		}
		if (copyPerformed) {
			refresh();
		}
	}

	protected MenuBar.MenuItem addButton(String caption, Resource icon, MenuBar.Command command) {
		return getTools().addItem(caption, icon, command);
	}

	/**
	 * Convenience method for adding menu items. Will use provided button caption and icon, but NOT the button click listener
	 */
	private MenuBar.MenuItem addButton(Button button, MenuBar.Command command) {
		return getTools().addItem(button.getCaption(), button.getIcon(), command);
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * @return the items
	 */
	public List<LISTITEM> getItems() {
		return new ArrayList<>(items);
	}

	/**
	 * Add the provided item to the list
	 * @param item the item to add
	 */
	protected void add(LISTITEM item) {
		items.add(item);
	}

	/**
	 * Remove the provided item from the list
	 * @param item the item to add
	 */
	protected void remove(LISTITEM item) {
		items.remove(item);
	}

	/**
	 * In some circumstances you might need to clone the items before working with them.
	 * This is needed if the items are entities and you have nested SimpleCrudInMemoryPanels.
	 * You should make a clone of the items (with exact same pk and optimisticLock), or else you might get into problems when saving
	 * @param items the items to CRUD
	 * @return a clone of the items
	 */
	protected Set<LISTITEM> clone(Set<LISTITEM> items) {
		return items;
	}
}
