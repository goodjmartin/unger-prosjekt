package no.goodtech.vaadin.ui.v7;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import no.goodtech.persistence.entity.AbstractEntityImpl;
import no.goodtech.persistence.entity.Entity;
import no.goodtech.persistence.jpa.AbstractFinder;
import no.goodtech.vaadin.buttons.AddButton;
import no.goodtech.vaadin.buttons.CopyButton;
import no.goodtech.vaadin.buttons.DetailButton;
import no.goodtech.vaadin.buttons.RemoveButton;
import no.goodtech.vaadin.buttons.export.CsvExportButton;
import no.goodtech.vaadin.buttons.export.ExcelExportButton;
import no.goodtech.vaadin.lists.v7.ListComponentDisabler;
import no.goodtech.vaadin.lists.v7.ListSelectionAwareComponentDisabler;
import no.goodtech.vaadin.lists.v7.MessyButtonLayout;
import no.goodtech.vaadin.lists.v7.MessyTable;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.main.ConfirmWindow;
import no.goodtech.vaadin.main.ConfirmWindow.IConfirmWindowListener;
import no.goodtech.vaadin.main.SimplerInputBox;
import no.goodtech.vaadin.main.SimplerInputComponent;
import no.goodtech.vaadin.search.FilterPanel;
import no.goodtech.vaadin.search.FilterPanel.FilterActionListener;
import no.goodtech.vaadin.security.AccessFunction;
import no.goodtech.vaadin.security.AccessFunctionManager;
import no.goodtech.vaadin.tabs.IMenuView;
import no.goodtech.vaadin.ui.Texts;
import no.goodtech.vaadin.ui.Utils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Panel for administration of an entity
 */
@Deprecated
public abstract class SimplerCrudAdminPanel<ENTITY extends Entity, FINDER extends AbstractFinder> extends VerticalLayout implements IMenuView {

	protected final MessyTable table;
	protected final FilterPanel filterPanel;
	protected Button addButton, deleteButton, copyButton, detailsButton, excelExportButton, csvExportButton;

	protected final MessyButtonLayout buttonLayout;
	protected final String accessFunctionViewId, accessFunctionEditId;
	protected boolean isReadOnly;

	protected final boolean isMultiEdit;

	protected abstract SimplerInputComponent<ENTITY> createDetailForm(ENTITY entity);

	protected abstract ENTITY createEntity();

	protected abstract AccessFunction getAccessFunctionView();

	protected abstract AccessFunction getAccessFunctionEdit();

	public SimplerCrudAdminPanel(FilterPanel filterPanel, MessyTable table){
		this(filterPanel, table, false);
	}

	public SimplerCrudAdminPanel(FilterPanel filterPanel, MessyTable table, boolean isMultiEdit) {

		this.isMultiEdit = isMultiEdit;

		final AccessFunction accessFunctionView = getAccessFunctionView();
		accessFunctionViewId = accessFunctionView.getId();
		AccessFunctionManager.registerAccessFunction(accessFunctionView);

		final AccessFunction accessFunctionEdit = getAccessFunctionEdit();
		accessFunctionEditId = accessFunctionEdit.getId();
		AccessFunctionManager.registerAccessFunction(accessFunctionEdit);

		this.filterPanel = filterPanel;
		this.table = table;
		buttonLayout = createButtons();

		filterPanel.setActionListener(new FilterActionListener<FINDER>() {
			public Integer pleaseSearch(FINDER finder) {
				List list = finder.list();
				table.refresh(list);
				return list.size();
			}
		});

		table.addItemClickListener(e -> {
			if (e.isDoubleClick() && detailsButton.isEnabled() && detailsButton.isVisible()) {
				Set<ENTITY> selectedObjects = table.getSelectedObjects();
				if (selectedObjects.size() <= 1) {
					final Object itemId = e.getItemId();
					if (itemId != null)
						edit((ENTITY) e.getItemId());
				} else {
					edit(getSelectedObject());
				}
			}

		});

		createShortcutKeyListeners();

		setSizeFull();

		ListSelectionAwareComponentDisabler.control(table, !this.isMultiEdit, detailsButton);
		ListSelectionAwareComponentDisabler.control(table, deleteButton, copyButton);
		ListComponentDisabler.control(table, excelExportButton, csvExportButton);
	}

	public boolean isAuthorized(User user, String value) {
		if (!AccessFunctionManager.isAuthorized(user, accessFunctionEditId)) {
			buttonLayout.enableTableSelectionButtons(false);
			buttonLayout.enableAddButton(false);
		} else {
			addButton.setEnabled(true);
		}
		return AccessFunctionManager.isAuthorized(user, accessFunctionViewId);
	}

	public void enter(ViewChangeEvent event) {
		if (getComponentCount() == 0) {
			addComponents(filterPanel, table, buttonLayout);
			setExpandRatio(table, 1);
		}
		filterPanel.refresh(event.getParameters());
		refresh();
	}

	protected void edit(ENTITY entityStub) {
		edit(Collections.singleton(entityStub));
	}

	protected void edit(Set<ENTITY> entityStubs) {
		SimplerInputComponent<ENTITY> simplerInputComponent;
		if (entityStubs.size() == 1){
			simplerInputComponent = createDetailForm(entityStubs.iterator().next());
		}else {
			simplerInputComponent = createDetailForm(entityStubs);
		}
		if (simplerInputComponent != null) {
			final String okCaption = Texts.get("simpleCrudAdminPanel.popup.save.caption");
			final String cancelCaption = Texts.get("simpleCrudAdminPanel.popup.cancel.caption");

			final SimplerInputBox<ENTITY> popup = new SimplerInputBox<>(simplerInputComponent, okCaption, cancelCaption, () -> {
				refresh();
				if (entityStubs.size() == 1 && entityStubs.iterator().next() != null) {
					if (entityStubs.iterator().next().isNew())
						Utils.selectNewestItem(table);
				}
			});
			UI.getCurrent().addWindow(popup);
			final Integer popupWidth = getDetailsPopupWidth();
			if (popupWidth != null)
				popup.setWidth(popupWidth, Unit.PERCENTAGE);
			popup.focus();
		}
	}

	/**
	 * Override this method when panel has edit available for multiselect
	 *
	 * @param entities - the selected entities
	 * @return - a popup for editing the selected entities
	 */
	protected SimplerInputComponent<ENTITY> createDetailForm(Set<ENTITY> entities) {
		return null;
	}

	/**
	 * @return with on popup in %. If null, it will scale based on the content
	 */
	protected Integer getDetailsPopupWidth() {
		return 30;
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
		copyButton.setVisible(isCopySupported());
		MessyButtonLayout layout = new MessyButtonLayout((AddButton) addButton, (RemoveButton) deleteButton, (CopyButton) copyButton, null, (DetailButton) detailsButton);
		layout.addButton(excelExportButton);
		layout.addButton(csvExportButton);
		excelExportButton.setEnabled(false);
		csvExportButton.setEnabled(false);
		layout.enableTableSelectionButtons(false);
		return layout;
	}

	/**
	 * @return title / caption of exported excel or csv file
	 */
	protected String getExportFileCaption() {
		return null;
	}

	private void details() {
		if (!table.isMultiSelect() || table.getSelectedObjects().size() <= 1) {
			final ENTITY selectedObject = getSelectedObject();
			edit(selectedObject);
		} else {
			edit(table.getSelectedObjects());
		}
	}

	protected ENTITY getSelectedObject() {
		final Set<?> selectedObjects = table.getSelectedObjects();
		if (selectedObjects.size() > 0)
			return (ENTITY) selectedObjects.iterator().next();
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
	protected void createWithCopyFromEntity(ENTITY entity) {
		edit(entity);
	}

	private void copy() {
		boolean refreshNeeded = false;
		final Set<ENTITY> selectedObjects = table.getSelectedObjects();
		for (ENTITY entityToCopy : selectedObjects) {
			//TODO: We want a copy function in Entity interface to force entities to have this function
			ENTITY copy = copy(entityToCopy);
			if (copy.isNew())
				edit(copy);
			else
				refreshNeeded = true;
		}
		if (refreshNeeded && selectedObjects.size() > 0)
			refresh();
	}

	/**
	 * Please override this if you want to check anything before deletion
	 *
	 * @param entity the object to delete
	 * @return null if we can delete, or error message if not
	 */
	protected String isDeleteOk(ENTITY entity) {
		return null;
	}

	protected void delete() {
		final ENTITY selectedObject = getSelectedObject();
		if (selectedObject != null) {
			final String message = isDeleteOk(selectedObject);
			if (message != null) {
				Notification.show(Texts.get("simpleCrudAdminPanel.delete.error.caption"), message, Type.WARNING_MESSAGE);
			} else {
				String confirmQuestion = getDeleteConfirmMessage(selectedObject);
				if (confirmQuestion == null) {
					deleteSelectedObjects();
				} else {
					String yesCaption = Texts.get("simpleCrudAdminPanel.delete.yes.caption");
					final ConfirmWindow confirm = new ConfirmWindow(confirmQuestion, yesCaption, new IConfirmWindowListener() {
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
		Set<ENTITY> selectedObjects = (Set<ENTITY>)table.getSelectedObjects();
		for (Object entityStub : selectedObjects) {
			//TODO: Implement delete in Entity
			//ENTITY entity = (ENTITY) entityStub;
			//entity.delete();
			if (entityStub instanceof AbstractEntityImpl<?>) {
				AbstractEntityImpl<?> entity = (AbstractEntityImpl<?>) entityStub;
				entity.delete();
			}
		}

		// Perform post-delete actions
		postDeleteAction(selectedObjects);

		refresh();
	}

	/**
	 * Please override this if you want a better confirm message before deletion or no confirmation at all
	 *
	 * @param entity the object to delete
	 * @return confirmation question or null to don't ask for confirmation
	 */
	protected String getDeleteConfirmMessage(ENTITY entity) {
		return Texts.get("simpleCrudAdminPanel.delete.confirmation");
	}

	public void refresh() {
		filterPanel.search();
	}

	protected boolean isCopySupported() {
		try {
			copy(null);
		} catch (UnsupportedOperationException e) {
			return false;
		} catch (RuntimeException e) {
		}
		return true;
	}

	/**
	 * Provide a copy of entityToCopy. Remember to load a fresh instance before you copy.
	 * If the entity have any unique identifiers, it may be best to let the user edit the copy and change identifiers, before you save it
	 * If not, please save the copy at once
	 * @param entityToCopy the entity to copy
	 * @return the copy. If the copy is not saved, the copy will be shown in an edit window
	 */
	protected ENTITY copy(ENTITY entityToCopy) {
		throw new UnsupportedOperationException();
	}

	protected void postDeleteAction(Set<ENTITY> deletedEntities) {
	}

}

