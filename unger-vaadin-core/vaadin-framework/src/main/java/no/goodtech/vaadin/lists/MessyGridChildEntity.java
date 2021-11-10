package no.goodtech.vaadin.lists;

import no.goodtech.persistence.entity.AbstractChildEntityImpl;
import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.search.OwnerFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static no.goodtech.vaadin.utils.Utils.getEntityCaption;

/**
 * MessyGrid for entities extending AbstractChildEntityImpl.
 * This displays information about the owners of the entities in an efficient manner
 */
public class MessyGridChildEntity<T extends AbstractChildEntityImpl> extends MessyGrid<T> {
	protected static final String OWNER_CLASS_CAPTION = ApplicationResourceBundle.getInstance("vaadin-core").getString("ownerClass.caption");
	protected static final String OWNER_PK_CAPTION = ApplicationResourceBundle.getInstance("vaadin-core").getString("owner.caption");

	protected Column ownerClassColumn, ownerColumn;

	private final Class<T> childEntityClass;
	private Map<String, Map<Long, EntityStub>> ownerClassToOwnerMap;

	public MessyGridChildEntity(Class<T> childEntityClass) {
		super();
		this.ownerClassToOwnerMap = new LinkedHashMap<>();
		this.childEntityClass = childEntityClass;
	}

	protected void addOwnerColumns(){
		ownerClassColumn = addColumn(attachment -> getEntityCaption(attachment.getOwnerClass())).setCaption(OWNER_CLASS_CAPTION).setHidable(true);
		ownerColumn = addColumn(attachment -> {
			if (attachment.getOwnerPk() != null && attachment.getOwnerClass() != null) {
				Map<Long, EntityStub> ownerMap = ownerClassToOwnerMap.get(attachment.getOwnerClass().getName());
				Long pk = attachment.getOwnerPk();
				if (pk != null && ownerMap != null) {
					EntityStub owner = ownerMap.get(pk);
					if (owner != null) {
						return owner.toString();
					}
				}
			}
			return null;
		}).setCaption(OWNER_PK_CAPTION).setHidable(true);
	}

	public void hideOwnerColumns(){
		hideOwnerColumns(true);
	}

	public void hideOwnerColumns(boolean hidden) {
		setOwnerClassColumnHidden(hidden);
		setOwnerColumnHidden(hidden);
	}

	public void setOwnerColumnHidden(boolean hidden){
		if (ownerColumn != null)
			ownerColumn.setHidden(hidden);
	}

	public void setOwnerClassColumnHidden(boolean hidden){
		if (ownerClassColumn != null)
			ownerClassColumn.setHidden(hidden);
	}

	/**
	 * Need to call this whenever the table is refreshed to update the owners
	 * @param entities that are owners of child entities (e.g. Comment or Attachment)
	 */
	private void refreshMap(List<? extends T> entities) {
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
		this.ownerClassToOwnerMap = new OwnerFinder(childEntityClass).findOwnerEntities(classToPks);
	}

	@Override
	public void refresh(List<? extends T> objects){
		super.refresh(objects);
		refreshMap(objects);
	}
}

