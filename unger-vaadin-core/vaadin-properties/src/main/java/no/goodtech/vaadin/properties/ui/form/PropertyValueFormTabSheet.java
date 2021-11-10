package no.goodtech.vaadin.properties.ui.form;

import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import no.goodtech.persistence.entity.Entity;
import no.goodtech.vaadin.properties.model.PropertyClassFinder;
import no.goodtech.vaadin.properties.model.PropertyClassStub;

import java.util.Iterator;
import java.util.List;

/**
 * A tabsheet containing property value forms, one tab per property group 
 */
public class PropertyValueFormTabSheet extends TabSheet {

	private final List<String> propertyClassIds;
	private boolean isReadOnly = false;

	public PropertyValueFormTabSheet(List<String> propertyClassIds) {
		this.propertyClassIds = propertyClassIds;
		setHeight(100,Unit.PERCENTAGE);
	}
	
	public void refresh(Entity owner) {
		removeAllComponents();
		for (PropertyClassStub group : new PropertyClassFinder().setIds(propertyClassIds).list()) {
			final PropertyValueForm form = new PropertyValueForm();
			form.setReadOnly(isReadOnly());

			form.refresh(group.getId(), owner);
			form.setMargin(true);
			addTab(form, group.getDescription());
		}
	}
	
	/**
	 * Validate
	 */
	public void commit() throws CommitException {
		final Iterator<Component> iterator = iterator();
		while (iterator.hasNext()) {
			final PropertyValueForm form = (PropertyValueForm) iterator.next();
			form.commit();
		}
	}
	
	public void save(Entity owner) throws CommitException {
		final Iterator<Component> iterator = iterator();
		while (iterator.hasNext()) {
			final PropertyValueForm form = (PropertyValueForm) iterator.next();
			form.setOwner(owner);
			form.commit();
			form.save();
		}
	}
	
	public void setReadOnly(boolean readOnly) {
		isReadOnly = readOnly;
		final Iterator<Component> iterator = iterator();
		while (iterator.hasNext()) {
			final PropertyValueForm form = (PropertyValueForm) iterator.next();
			form.setReadOnly(readOnly);
		}		
	}

	@Override
	protected boolean isReadOnly() {
		return isReadOnly;
	}
}
