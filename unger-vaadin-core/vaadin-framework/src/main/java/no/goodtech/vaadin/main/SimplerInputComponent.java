package no.goodtech.vaadin.main;

import com.vaadin.data.Binder;
import com.vaadin.server.SizeWithUnit;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import no.goodtech.persistence.entity.AbstractEntityImpl;
import no.goodtech.persistence.entity.Entity;
import no.goodtech.vaadin.ui.Texts;

public abstract class SimplerInputComponent<ENTITY extends Entity> extends VerticalLayout {

	private final Binder<ENTITY> binder = new Binder<>();

	private boolean isReadOnly = false;

	private final ENTITY entity;

	public SimplerInputComponent(final ENTITY entity) {
		this.entity = entity;
	}

	public Binder<ENTITY> getBinder() {
		return binder;
	}

	public void setReadOnly(boolean readOnly) {
		binder.setReadOnly(isReadOnly = readOnly);
	}

	public boolean isReadOnly() {
		return isReadOnly;
	}

	/**
	 * Use this to control height of your component if you want to use expand feature.
	 * Implement it and set your height as either pixels or percentage
	 *
	 * @return overridden component height or this default( setHeightUndefined)
	 */
	@SuppressWarnings("WeakerAccess")
	protected SizeWithUnit getComponentHeight() {
		return new SizeWithUnit(-1, Unit.PIXELS);
	}

	public boolean save() {
		if (binder.writeBeanIfValid(entity)) {
			if (preSaveAction(entity)) {
				((AbstractEntityImpl)entity).save();
				postSaveAction(entity);
			}
			return true;
		}
		Notification.show(Texts.get("confirmBox.error.save"), Notification.Type.WARNING_MESSAGE);
		return false;
	}

	protected boolean preSaveAction(@SuppressWarnings("unused") final ENTITY entity) {
		return true;
	}

	protected  void postSaveAction(@SuppressWarnings("unused") final ENTITY entity) {
	}

}
