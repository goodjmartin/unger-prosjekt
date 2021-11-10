package no.goodtech.vaadin.properties.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.goodtech.persistence.entity.EntityStub;

/**
 * Hjelpeklasse for å finne arvede verdier
 * @author oystein
 */
public class PropertyInheritanceUtils {

	private final List<? extends EntityStub<?>> inheritanceSources;

	/**
	 * Opprett objekt
	 * @param inheritanceSources objekter som man kan arve fra
	 */
	public PropertyInheritanceUtils(List<? extends EntityStub<?>> inheritanceSources) {
		this.inheritanceSources = inheritanceSources;
	}
	
	/**
	 * @return finner verdiene som eies av objektene i inheritanceSources
	 */
	private Set<PropertyValue> listInheritedValues() {
		//hvis kildene i inheritanceSources har forskjellig objekttype, kan man ikke hente alle properties på en gang
		//derfor deler jeg først opp kildene etter objekttype, og så henter jeg alle properties for hver objekttype 
		Set<PropertyValue> inheritedValues = new HashSet<PropertyValue>();
		//deler sources inn i objekttyper
		Map<Class<?>, List<Long>> inheritanceSourcesByClass = new HashMap<Class<?>, List<Long>>();
		for (EntityStub<?> source : inheritanceSources) {
			Class<?> clazz = source.getClass();
			List<Long> sourcesWithRelevantClass = inheritanceSourcesByClass.get(clazz);
			if (sourcesWithRelevantClass == null)
				sourcesWithRelevantClass = new ArrayList<Long>();
			sourcesWithRelevantClass.add(source.getPk());
			inheritanceSourcesByClass.put(clazz, sourcesWithRelevantClass);
		}
		for (Map.Entry<Class<?>, List<Long>> entry : inheritanceSourcesByClass.entrySet()) {
			//henter properties for alle kilder av denne typen
			PropertyValueFinder finder = new PropertyValueFinder(); //todo kopier evt. kriteria som er satt på this
			finder.setOwnerClass(entry.getKey()).setOwnerPk(entry.getValue());

			//putt verdiene inn i riktig rekkefølge (nærmeste forelder først)
			final Map<OwnerKey, List<PropertyValue>> propertiesByOwner = finder.getPropertiesByOwner();
			for (EntityStub<?> source : inheritanceSources) {
				OwnerKey key = new OwnerKey(source);
				final List<PropertyValue> propertiesOwnedByThisSource = propertiesByOwner.get(key);
				if (propertiesOwnedByThisSource != null)
				{
					for(PropertyValueStub propertyOwnedByThisSource : propertiesOwnedByThisSource){
						inheritedValues.add(propertyOwnedByThisSource.load());
					}
				}
			}
			//TODO: Ta hensyn til multiple verdier (ved multippel arv)
			for (PropertyValue value : inheritedValues)
				value.setInherited(true);
		}
		return inheritedValues;
	}
	
	/**
	 * Hent arvede verdier og legg disse til angitt liste
	 * @param values verdiene som eies av objektet som arver
	 * @return en liste med både egne og arvede verdier
	 */
	public List<PropertyValue> applyInheritedStubValues(Collection<PropertyValue> values) {
		Set<PropertyValue> result = new HashSet<PropertyValue>(values);
		result.addAll(listInheritedValues());
		return new ArrayList<PropertyValue>(result);
	}
}
