package no.goodtech.vaadin.search;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.Table;
import no.goodtech.persistence.entity.AbstractChildEntityImpl;
import no.goodtech.persistence.entity.EntityStub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static no.goodtech.vaadin.utils.Utils.getEntityCaption;

@Deprecated
public class OwnerColumnGenerator implements Table.ColumnGenerator {
	public static final String OWNER_CLASS_CAPTION = "ownerClass";
	public static final String OWNER_PK_CAPTION = "ownerPk";

	private Map<String, Map<Long, EntityStub>> ownerClassToOwnerMap;

	public OwnerColumnGenerator() {
		ownerClassToOwnerMap = new LinkedHashMap<>();
	}

	/**
	 * Need to call this whenever the table is refreshed to update the owners
	 * @param clazz the class that implements the ownerClassCaption and ownerPkCaption functions
	 * @param entities that are owners of child entities (e.g. Comment or Attachment)
	 */
	public void refreshMap(Class<?> clazz, List<AbstractChildEntityImpl> entities) {
		Map<Class<?>, List<Long>> classToPks = new HashMap<>();
		for (AbstractChildEntityImpl entity : entities) {
			Class<?> ownerClass = entity.getOwnerClass();
			long ownerPk = entity.getOwnerPk();
			if (classToPks.get(ownerClass) == null) {
				ArrayList<Long> ownerPks = new ArrayList<>();
				ownerPks.add(ownerPk);
				classToPks.put(ownerClass, ownerPks);
			} else {
				classToPks.get(ownerClass).add(ownerPk);
			}
		}
		this.ownerClassToOwnerMap = new OwnerFinder(clazz).findOwnerEntities(classToPks);
	}

	@Override
	public Object generateCell(Table source, Object itemId, Object columnId) {
		EntityStub entity = (EntityStub) itemId;
		if (entity != null) {
			Property propertyClass = source.getItem(itemId).getItemProperty(OWNER_CLASS_CAPTION);
			if (propertyClass != null) {
				Class<?> ownerClass = (Class<?>) propertyClass.getValue();
				if (ownerClass != null) {
					if (columnId.equals(OWNER_CLASS_CAPTION)) {
						// Special captions for each ownerClass
						return getEntityCaption(ownerClass);
					} else if (columnId.equals(OWNER_PK_CAPTION)) {
						// Returns toString of owner if owner is found
						Property propertyPk = source.getItem(itemId).getItemProperty(OWNER_PK_CAPTION);
						if (propertyPk != null) {
							Long pk = (Long) propertyPk.getValue();
							Map<Long, EntityStub> ownerMap = ownerClassToOwnerMap.get(ownerClass.getName());
							if (pk != null && ownerMap != null) {
								EntityStub owner = ownerMap.get(pk);
								if (owner != null) {
									return owner.toString();
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

}
