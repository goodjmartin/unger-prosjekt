package no.goodtech.vaadin.validation;

import com.vaadin.v7.ui.Field;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Use this to check if a field in a form or in a table with inline editing is changed
 * Call {@link #commit()} before {@link #isDirty(Object)}
 */
public class DirtyChecker {

	private final Map<Field<?>, Object> fieldDataSources = new HashMap<Field<?>, Object>();
	private final Set<Object> dirtyDataSources = new HashSet<Object>();

	public void add(Field<?> field, Object dataSource) {
		fieldDataSources.put(field, dataSource);
	}
	
	public void commit() {
		for (Field<?> field : fieldDataSources.keySet()) {
			if (field.isModified()) {
				//must do this before field commit because dirty flag is cleared after commit 
				dirtyDataSources.add(fieldDataSources.get(field));
			}
			field.commit(); 
		}
	}

	/**
	 * Check if given given data source is changed
	 * Example:
	 * final Container binder = table.getContainerDataSource();
	 * for (Object itemId : binder.getItemIds()) {
	 *    final MaintenanceJob job = (MaintenanceJob) itemId;
	 *    if (dirtyChecker.isDirty(itemId)) {
	 *    job.setLastChangedByUsername(getUser().getFullName());
	 *    job.save();
	 * }
	 * @param source if used in a table, this is the row
	 * @return true if given data source is changed
	 */
	public boolean isDirty(Object source) {
		if (dirtyDataSources.contains(source))
			return true;
		return false;
	}
}
