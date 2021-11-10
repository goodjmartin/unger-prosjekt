package no.goodtech.vaadin.ui;


import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import no.cronus.common.utils.ReflectionUtils;
import no.goodtech.persistence.entity.Entity;
import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.vaadin.lists.ICopyable;
import no.goodtech.vaadin.main.ConfirmWindow;
import no.goodtech.vaadin.main.SimpleInputBox;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PersistenceException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 * TODO after upgrade to Java 9 -> change methods to private/protected. This is not possible in Java 8..
 * This contains common functionality and methods for all SimpleCrud panels.
 */
public interface ISimpleCrud<ENTITY extends Entity, ENTITYSTUB extends EntityStub<?>> {

	Logger LOGGER = LoggerFactory.getLogger(ISimpleCrud.class);

	void refresh();

	SimpleInputBox.IinputBoxContent createDetailForm(EntityStub entity);

	EntityStub createEntity();

	/**
	 * @return with on popup in %. If null, it will scale based on the content
	 */
	default Integer getDetailsPopupWidth() {
		return null;
	}

	/**
	 * Navigates to the detail page if you provide an url for it
	 * @param url the url to navigate to
	 * @see #getDetailPageUrl(EntityStub)
	 */
	default void navigateTo(String url) {
		if (url != null) {
			UI.getCurrent().getNavigator().navigateTo(url);
		}
	}

	/**
	 * If you want to go to a separate detail page when the user clicks "Details", provide the url for the page here
	 * @param entity the entity, which will never be null
	 * @return url of the detail page
	 */
	default String getDetailPageUrl(ENTITYSTUB entity) {
		return null;
	}

	/**
	 * Please override this if you want to check anything before deletion
	 *
	 * @param entity the object to delete
	 * @return null if we can delete, or error message if not
	 */
	default String isDeleteOk(ENTITYSTUB entity) {
		return null;
	}

	Set<ENTITYSTUB> getSelectedItems();

	default void add() {
		final EntityStub entity = createEntity();
		if (!entity.isNew()) {
			//entity is saved, so navigate to detail page if it exists
			final String url = getDetailPageUrl((ENTITYSTUB) entity);
			if (url != null) {
				navigateTo(url);
			}
		}
		popupDetails(entity);
	}

	default ENTITYSTUB getSelectedObject() {
		final Set<?> selectedObjects = getSelectedItems();
		if (selectedObjects.size() > 0) {
			return (ENTITYSTUB) selectedObjects.iterator().next();
		}
		return null;
	}

	/**
	 * Override this method when panel has edit available for multiselect
	 *
	 * @param entities - the selected entities
	 * @return - a popup for editing the selected entities
	 */
	default SimpleInputBox.IinputBoxContent createDetailForm(Set<? extends EntityStub> entities) {
		return null;
	}

	default void details() {
		Set<ENTITYSTUB> selectedItems = getSelectedItems();
		if (selectedItems.size() == 1) {
			details(selectedItems.iterator().next());
		} else if (!selectedItems.isEmpty()) {
			popupDetails(selectedItems);
		}
	}

	default void details(ENTITYSTUB entity) {
		if (entity != null) {
			final String url = getDetailPageUrl(entity);
			if (url != null) {
				navigateTo(url);
			} else {
				popupDetails(entity);
			}
		}
	}

	default void popupDetails(EntityStub entity) {
		if (entity != null) {
			popupDetails(Collections.singleton(entity));
		}
	}

	default void popupDetails(Set<? extends EntityStub> entities) {
		SimpleInputBox.IinputBoxContent form;
		if (entities.size() == 1) {
			form = createDetailForm(entities.iterator().next());
		} else {
			form = createDetailForm(entities);
		}
		if (form != null) {
			final String okCaption = Texts.get("simpleCrudAdminPanel.popup.save.caption");
			final String cancelCaption = Texts.get("simpleCrudAdminPanel.popup.cancel.caption");

			final SimpleInputBox popup = new SimpleInputBox(form, okCaption, cancelCaption, new SimpleInputBox.IConfirmListener() {
				public void onConfirm() {
					Set<ENTITYSTUB> selectedItems = getSelectedItems();
					refresh();

					if (!selectedItems.isEmpty()) {
						setSelectedItems(selectedItems);
					}

//TODO					if (entities.size() == 1 && entities.iterator().next() != null) {
//TODO						if (entities.iterator().next().isNew())
//TODO							Utils.selectNewestItem(table);
//TODO					}
				}
			});

			Button[] customButtons = getCustomDetailButtons(form);
			if (customButtons != null) {
				popup.getButtonLayout().addComponents(customButtons);
			}
			if (isReadOnly())
				popup.setReadOnly(true);
			UI.getCurrent().addWindow(popup);
			final Integer popupWidth = getDetailsPopupWidth();
			if (popupWidth != null) {
				popup.setWidth(popupWidth, Sizeable.Unit.PERCENTAGE);
			}
			popup.focus();
		}
	}

	/**
	 * Add custom buttons to the edit form
	 */
	default Button[] getCustomDetailButtons(SimpleInputBox.IinputBoxContent form){
		return null;
	}

	String getDeleteConfirmMessage(ENTITYSTUB entity);

	void createShortcutKeyListeners();

	default void deleteSelectedObjects() {
		try {
			for (ENTITYSTUB entity : getSelectedItems()) {
				//entity.delete();
			}
		} catch (PersistenceException e) {
			String feedback = Texts.get("simpleCrudAdminPanel.delete.persistenceException");
			LOGGER.warn(feedback + " Items to be deleted: " + StringUtils.join(getSelectedItems(), ','));
			Notification.show(feedback, Notification.Type.WARNING_MESSAGE);
		}
		refresh();
	}

	default void delete(){
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

	default void copy() {
		boolean refreshNeeded = false;
		final Set<ENTITYSTUB> selectedObjects = getSelectedItems();
		for (ENTITYSTUB entityToCopy : selectedObjects) {
			//TODO: We want a copy function in Entity interface to force entities to have this function
			ENTITYSTUB copy = copy(entityToCopy);
			if (copy.isNew()) {
				popupDetails(copy);
			} else {
				refreshNeeded = true;
			}
		}
		if (refreshNeeded && selectedObjects.size() > 0) {
			refresh();
		}
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
	default ENTITYSTUB copy(ENTITYSTUB entityToCopy) {
		if (entityToCopy instanceof ICopyable) {
			ICopyable source = (ICopyable) entityToCopy.load();
			return (ENTITYSTUB) source.copy();
		} else {
			throw new UnsupportedOperationException();
		}
	}

	boolean isReadOnly();

	void setReadOnly(boolean readOnly);

	/**
	 * @return true if copy is supported
	 */
	default boolean isCopySupported() {
		try {
			if (ReflectionUtils.hasParameterizedType(this, 0)) {
				Class<ENTITY> type = (Class<ENTITY>) ReflectionUtils.getParameterizedType(this, 0);
				return Arrays.asList(type.getInterfaces()).contains(ICopyable.class);
			}
		} catch (ClassCastException ignored) {
		}
		return false;
	}

	default void setSelectedItems(Set<ENTITYSTUB> items) {

	}

}
