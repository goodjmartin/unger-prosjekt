package no.goodtech.vaadin.ui;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import no.goodtech.persistence.entity.AbstractEntityImpl;
import no.goodtech.persistence.entity.Entity;
import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.persistence.jpa.AbstractFinder;
import no.goodtech.vaadin.buttons.AddButton;
import no.goodtech.vaadin.buttons.CopyButton;
import no.goodtech.vaadin.buttons.DetailButton;
import no.goodtech.vaadin.buttons.RemoveButton;
import no.goodtech.vaadin.buttons.export.CsvExportButton;
import no.goodtech.vaadin.buttons.export.ExcelExportButton;
import no.goodtech.vaadin.lists.ICopyable;
import no.goodtech.vaadin.lists.MessyGrid;
import no.goodtech.vaadin.lists.v7.ListComponentDisabler;
import no.goodtech.vaadin.lists.v7.ListSelectionAwareComponentDisabler;
import no.goodtech.vaadin.lists.v7.MessyButtonLayout;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.main.ConfirmWindow;
import no.goodtech.vaadin.main.SimpleInputBox;
import no.goodtech.vaadin.search.FilterPanel;
import no.goodtech.vaadin.security.AccessFunction;
import no.goodtech.vaadin.security.AccessFunctionManager;
import no.goodtech.vaadin.tabs.IMenuView;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class SimpleCrudAdminPanel<ENTITY extends Entity, ENTITYSTUB extends EntityStub<?>, FINDER extends AbstractFinder>
		extends VerticalLayout
		implements IMenuView {

	protected final MessyGrid table;
	protected final FilterPanel filterPanel;
	protected Button addButton, deleteButton, copyButton, detailsButton, excelExportButton, csvExportButton;

	protected final MessyButtonLayout buttonLayout;
	protected final String accessFunctionViewId, accessFunctionEditId;
	protected boolean isReadOnly;

	protected final boolean isMultiEdit;
	protected Boolean isCopySupported = null;

	protected abstract SimpleInputBox.IinputBoxContent createDetailForm(ENTITYSTUB entity);

	protected abstract ENTITYSTUB createEntity();

	protected abstract AccessFunction getAccessFunctionView();

	protected abstract AccessFunction getAccessFunctionEdit();

	public SimpleCrudAdminPanel(FilterPanel filterPanel, MessyGrid table) {
		this(filterPanel, table, false);
	}

	public SimpleCrudAdminPanel(FilterPanel filterPanel, MessyGrid table, boolean isMultiEdit) {

		final AccessFunction accessFunctionView = getAccessFunctionView();
		accessFunctionViewId = accessFunctionView.getId();
		AccessFunctionManager.registerAccessFunction(accessFunctionView);

		final AccessFunction accessFunctionEdit = getAccessFunctionEdit();
		accessFunctionEditId = accessFunctionEdit.getId();
		AccessFunctionManager.registerAccessFunction(accessFunctionEdit);

		this.filterPanel = filterPanel;
		this.table = table;
		buttonLayout = createButtons();

		this.isMultiEdit = isMultiEdit;

		//refresh table when user clicks refresh button in filter panel
		filterPanel.setActionListener(new FilterPanel.FilterActionListener<FINDER>() {
			public Integer pleaseSearch(FINDER finder) {
				List list = finder.list();
				table.refresh(list);

				//TODO: Hack beacuse ListComponentDisabler doesn't work with MessyGrid
				final boolean containsData = list.size() > 0;
				excelExportButton.setEnabled(containsData);
				csvExportButton.setEnabled(containsData);
				copyButton.setVisible(isCopySupported());
				return list.size();
			}
		});

		//doubleclick opens details panel
		table.addItemClickListener(event -> {
			if (event.getMouseEventDetails().isDoubleClick() && buttonLayout.getDetailButton().isVisible()) {
				Set<ENTITYSTUB> selectedObjects = table.getSelectedItems();
				if (selectedObjects.size() <= 1) {
					final Object item = event.getItem();
					if (item != null)
						edit((ENTITYSTUB) item);
				} else {
					edit(getSelectedObject());
				}
			}
		});

		createShortcutKeyListeners();

		setSizeFull();
	}

	@Override
	protected boolean isReadOnly() {
		return isReadOnly;
	}

	@Override
	protected void setReadOnly(boolean readOnly) {
		isReadOnly = readOnly;
	}

	public boolean isAuthorized(User user, String value) {
		setReadOnly(!AccessFunctionManager.isAuthorized(user, accessFunctionEditId));

		if (isReadOnly()) {
			buttonLayout.enableTableSelectionButtons(false);
			buttonLayout.enableAddButton(false);
			ListSelectionAwareComponentDisabler.control(table, !this.isMultiEdit, detailsButton);
		} else {
			ListSelectionAwareComponentDisabler.control(table, !this.isMultiEdit, detailsButton);
			ListSelectionAwareComponentDisabler.control(table, deleteButton, copyButton);
			addButton.setEnabled(true);
		}
		ListComponentDisabler.control(table, excelExportButton, csvExportButton);
		return AccessFunctionManager.isAuthorized(user, accessFunctionViewId);
	}

	public void enter(ViewChangeListener.ViewChangeEvent event) {
		if (getComponentCount() == 0) {
			addComponents(filterPanel, table, buttonLayout);
			setExpandRatio(table, 1);
		}
		filterPanel.refresh(event.getParameters());
		refresh();
	}

	protected void edit(ENTITYSTUB entityStub) {
		edit(Collections.singleton(entityStub));
	}

	protected void edit(Set<ENTITYSTUB> entityStubs) {
		SimpleInputBox.IinputBoxContent form;
		if (entityStubs.size() == 1) {
			form = createDetailForm(entityStubs.iterator().next());
		} else {
			form = createDetailForm(entityStubs);
		}
		if (form != null) {
			final String okCaption = Texts.get("simpleCrudAdminPanel.popup.save.caption");
			final String cancelCaption = Texts.get("simpleCrudAdminPanel.popup.cancel.caption");

			final SimpleInputBox popup = new SimpleInputBox(form, okCaption, cancelCaption, new SimpleInputBox.IConfirmListener() {
				public void onConfirm() {
					refresh();
					if (entityStubs.size() == 1 && entityStubs.iterator().next() != null) {
//TODO						if (entityStubs.iterator().next().isNew())
//TODO							Utils.selectNewestItem(table);
					}
				}
			});
			popup.setReadOnly(isReadOnly());
			UI.getCurrent().addWindow(popup);
			final Integer popupWidth = getDetailsPopupWidth();
			if (popupWidth != null) {
				popup.setWidth(popupWidth, Unit.PERCENTAGE);
			}
			popup.focus();
		}
	}

	/**
	 * Override this method when panel has edit available for multiselect
	 *
	 * @param entities - the selected entities
	 * @return - a popup for editing the selected entities
	 */
	protected SimpleInputBox.IinputBoxContent createDetailForm(Set<ENTITYSTUB> entities) {
		return null;
	}

	/**
	 * @return with on popup in %. If null, it will scale based on the content
	 */
	protected Integer getDetailsPopupWidth() {
		return null;
	}

	/**
	 * Creates the layout for the buttons below the main-table.
	 * The listeners for the buttons is added as well.
	 */
	private MessyButtonLayout createButtons() {
		addButton = new AddButton(this::add);
		deleteButton = new RemoveButton(this::delete);
		detailsButton = new DetailButton(this::details);
		excelExportButton = new ExcelExportButton(table, getExportFileCaption());
		csvExportButton = new CsvExportButton(table, getExportFileCaption());
		copyButton = new CopyButton(this::copy);
		copyButton.setVisible(false);
		MessyButtonLayout layout = new MessyButtonLayout((AddButton) addButton, (RemoveButton) deleteButton, (CopyButton) copyButton, null, (DetailButton) detailsButton);
		layout.addButton(excelExportButton);
		layout.addButton(csvExportButton);
		excelExportButton.setEnabled(false);
		csvExportButton.setEnabled(false);
		layout.enableTableSelectionButtons(false);

		//TODO: Hidden because they don't work for vaadin 8 - grid, see T+ 10189 and 10190
		excelExportButton.setVisible(false);
		csvExportButton.setVisible(false);
		return layout;
	}

	/**
	 * @return title / caption of exported excel or csv file
	 */
	protected String getExportFileCaption() {
		return null;
	}

	private void details() {
		Set<ENTITYSTUB> selected = table.getSelectedItems();
		if (!selected.isEmpty()) {
			edit(selected);
		}
	}

	protected ENTITYSTUB getSelectedObject() {
		final Set<?> selectedObjects = table.getSelectedItems();
		if (selectedObjects.size() > 0) {
			return (ENTITYSTUB) selectedObjects.iterator().next();
		}
		return null;
	}

	/**
	 * Generates shortcut listeners for the table
	 */
	private void createShortcutKeyListeners() {
		addShortcutListener(new ShortcutListener("ENTER-listener", ShortcutAction.KeyCode.ENTER, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				if (target != null && target.equals(table) && detailsButton.isEnabled() && detailsButton.isVisible()) {
					details();
				}
			}
		});
		addShortcutListener(new ShortcutListener("DELETE-listener", ShortcutAction.KeyCode.DELETE, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				if (target != null && target.equals(table) && deleteButton.isEnabled() && deleteButton.isVisible()) {
					delete();
				}
			}
		});
		addShortcutListener(new ShortcutListener("INSERT-listener", ShortcutAction.KeyCode.INSERT, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				if (target != null && target.equals(table) && addButton.isEnabled() && addButton.isVisible()) {
					add();
				}
			}
		});
	}

	private void add() {
		edit(createEntity());
	}

	/**
	 * This may be used as a "copy from" feature. A copy is made, but instead of saving it an edit window is displayed
	 *
	 * @param entity
	 */
	protected void createWithCopyFromEntity(ENTITYSTUB entity) {
		edit(entity);
	}

	private void copy() {
		boolean refreshNeeded = false;
		final Set<ENTITYSTUB> selectedObjects = table.getSelectedItems();
		for (ENTITYSTUB entityToCopy : selectedObjects) {
			//TODO: We want a copy function in Entity interface to force entities to have this function
			ENTITYSTUB copy = copy(entityToCopy);
			if (copy.isNew()) {
				edit(copy);
			} else {
				refreshNeeded = true;
			}
		}
		if (refreshNeeded && selectedObjects.size() > 0) {
			refresh();
		}
	}

	/**
	 * Please override this if you want to check anything before deletion
	 *
	 * @param entity the object to delete
	 * @return null if we can delete, or error message if not
	 */
	protected String isDeleteOk(ENTITYSTUB entity) {
		return null;
	}

	protected void delete() {
		final ENTITYSTUB selectedObject = getSelectedObject();
		if (selectedObject != null) {
			final String message = isDeleteOk(selectedObject);
			if (message != null) {
				Notification.show(Texts.get("simpleCrudAdminPanel.delete.error.caption"), message, Notification.Type.WARNING_MESSAGE);
			} else {
				String confirmQuestion = getDeleteConfirmMessage(selectedObject);
				if (confirmQuestion == null) {
					deleteSelectedObjects();
				} else {
					String yesCaption = Texts.get("simpleCrudAdminPanel.delete.yes.caption");
					final ConfirmWindow confirm = new ConfirmWindow(confirmQuestion, yesCaption, new ConfirmWindow.IConfirmWindowListener() {
						public void onCancel() {
						}

						public void onAccept() {
							deleteSelectedObjects();
						}
					});
					UI.getCurrent().addWindow(confirm);
				}
			}
		}
	}

	protected void deleteSelectedObjects() {
		for (Object entityStub : table.getSelectedItems()) {
			//TODO: Implement delete in Entity
			//ENTITY entity = (ENTITY) entityStub;
			//entity.delete();
			if (entityStub instanceof AbstractEntityImpl<?>) {
				AbstractEntityImpl<?> entity = (AbstractEntityImpl<?>) entityStub;
				entity.delete();
				refresh();
			}
		}
	}

	/**
	 * Please override this if you want a better confirm message before deletion or no confirmation at all
	 *
	 * @param entity the object to delete
	 * @return confirmation question or null to don't ask for confirmation
	 */
	protected String getDeleteConfirmMessage(ENTITYSTUB entity) {
		return Texts.get("simpleCrudAdminPanel.delete.confirmation");
	}

	public void refresh() {
		filterPanel.search();
	}

	/**
	 * Checks if we are allowed to copy. Will remember what we found out for later reference
	 * @return true if copy is supported
	 */
	protected boolean isCopySupported() {
		if (isCopySupported != null) {
			return isCopySupported;
		}
		final Collection items = table.getItems();
		if (items.size() > 0) {
			try {
				copy((ENTITYSTUB) items.iterator().next());
			} catch (UnsupportedOperationException e) {
				isCopySupported = false;
				return false;
			}
		} else {
			return false; //we don't know yet if copy is supported since we haven't tested to copy any objects
		}
		isCopySupported = true; //remember this so we don't have to check again
		return true;
	}

	/**
	 * Provide a copy of entityToCopy.
	 * If you want to override this:
	 * Remember to load a fresh instance before you copy.
	 * If the entity have any unique identifiers, it may be best to let the user edit the copy and change identifiers, before you save it
	 * If not, please save the copy at once
	 * If entity is {@link ICopyable} it will be copied automatically, but it's up to you to save it
	 * @param entityToCopy the entity to copy
	 * @return the copy. If the copy is not saved, the copy will be shown in an edit window
	 */
	protected ENTITYSTUB copy(ENTITYSTUB entityToCopy) {
		if (entityToCopy instanceof ICopyable) {
			ICopyable source = (ICopyable) entityToCopy.load();
			return (ENTITYSTUB) source.copy();
		} else {
			throw new UnsupportedOperationException();
		}
	}
}

