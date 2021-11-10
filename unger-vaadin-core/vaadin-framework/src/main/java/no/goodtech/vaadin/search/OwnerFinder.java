package no.goodtech.vaadin.search;

import no.goodtech.persistence.PersistenceFactory;
import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.persistence.jpa.AbstractFinder;

import java.util.*;

/**
 * This is supposed to be used with classes with the setup of ownerClass and ownerPk.
 * It gives an easy way to fetch the list of ownerClasses, and fetch a list of ownerPks when you have specified an ownerClass
 * TODO how to make tests for this one when there are no entities to be saved in vaadin-framework?
 */
public class OwnerFinder {
	private final Class<?> clazz;

	/**
	 * @param clazz the class that contains the format of OwnerClass and OwnerPk
	 */
	public OwnerFinder(Class<?> clazz) {
		this.clazz = clazz;
	}

	/**
	 * Lists all distinct ownerClasses
	 */
	public List<String> listOwnerClasses() {
		if (clazz != null) {
			return PersistenceFactory.getQueryExecutor("mes").execute("select distinct a.ownerClass from " +
					clazz.getSimpleName() + " as a", String.class);
		}
		return null;
	}

	/**
	 * Lists all distinct ownerPks for one ownerClass
	 */
	public List<Long> listOwnerPks(Class<?> ownerClass) {
		if (ownerClass != null && clazz != null) {
			return PersistenceFactory.getQueryExecutor("mes").execute("select distinct a.ownerPk from " +
					clazz.getSimpleName() + " as a where a.ownerClass='" + ownerClass.getName() + "'", Long.class);
		}
		return null;
	}

	/**
	 * Converting Map<String, List<Long>> to Map<String, Map<Long, EntityStub>> by using reflection
	 * @param ownerClassToOwnerPkMap the map of ownerClass with list of ownerPks
	 * @return map of ownerClass to a map with Pk to Entity
	 */
	public Map<String, Map<Long, EntityStub>> findOwnerEntities(Map<Class<?>, List<Long>> ownerClassToOwnerPkMap){
		Map<String, Map<Long, EntityStub>> ownerClassToOwnerMap = new LinkedHashMap<>();
		for (Class<?> ownerClass : ownerClassToOwnerPkMap.keySet()) {
			Map<Long, EntityStub> pkToOwnerMap = new LinkedHashMap<>();
			try {
				List<Long> ownerPks = ownerClassToOwnerPkMap.get(ownerClass);
				String finderName = ownerClass.getName() + "Finder";
				Class<?> finderClass = Class.forName(finderName);
				if (finderClass != null) {
					AbstractFinder<?, ?, ?> finder = (AbstractFinder<?, ?, ?>) finderClass.newInstance();
					finder.setPk(ownerPks);
					List<EntityStub> entities = (List<EntityStub>) finder.list();
					for (Long ownerPk : ownerPks) {
						long pk = ownerPk;
						EntityStub entity = null;
						for (EntityStub entityStub: entities) {
							if (entityStub != null && entityStub.getPk() == pk) {
								entity = entityStub;
								break;
							}
						}
						pkToOwnerMap.put(pk, entity);
					}
				}
				ownerClassToOwnerMap.put(ownerClass.getName(), pkToOwnerMap);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return ownerClassToOwnerMap;
	}
}
