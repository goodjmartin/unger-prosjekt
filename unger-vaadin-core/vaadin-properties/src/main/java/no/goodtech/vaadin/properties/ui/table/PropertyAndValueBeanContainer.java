package no.goodtech.vaadin.properties.ui.table;

import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.util.BeanContainer;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.NativeSelect;
import com.vaadin.v7.ui.OptionGroup;
import no.goodtech.vaadin.properties.model.Property;
import no.goodtech.vaadin.properties.model.PropertyClass;
import no.goodtech.vaadin.properties.model.PropertyFinder;
import no.goodtech.vaadin.properties.model.PropertyMembership;
import no.goodtech.vaadin.properties.model.PropertyMembershipFinder;
import no.goodtech.vaadin.properties.model.PropertyStub;
import no.goodtech.vaadin.properties.model.PropertyValue;
import no.goodtech.vaadin.properties.model.PropertyValueFinder;
import no.goodtech.vaadin.properties.ui.IPropertyValueChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * BeanContainer som inneholder bønner av type PropertyAndValueBean
 * Inneholder metoder for uthenting og manipulering av verdier på
 * bønnene og innholdet i de.
 */
public class PropertyAndValueBeanContainer extends BeanContainer<Long, PropertyAndValueBean> {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Object[] sortFields = new Object[]{"property.id"};
	private volatile IPropertyValueChangeListener propertyValueChangeListener;
	private Class<?> booleanRepresentation = CheckBox.class;
	private boolean showTooltip = true;

	public PropertyAndValueBeanContainer() {
		super(PropertyAndValueBean.class);
		for (String field : Property.Fields.getAll()) {
			addNestedContainerProperty("property." + field);
		}
		addNestedContainerProperty(PropertyAndValueBean.Fields.PROPERTY_VALUE_PREFIX + PropertyValue.Fields.DESCRIPTION);
		setBeanIdProperty("property.id");
	}

	/**
	 * Removes all the empty PropertyAndValueBeans from the container.
	 */
	public boolean removeBeansWithEmptyValue(Class ownerClass, Long ownerPk) {
		if (ownerClass != null && ownerPk != null) {
			for (Long pk : getItemIds()) {
				PropertyAndValueBean propertyAndValueBean = getItem(pk).getBean();

				if (propertyAndValueBean != null && propertyAndValueBean.getPropertyClass() != null) {
					Field field = propertyAndValueBean.getPropertyValueField();
					if (field != null && (field.getValue() == null || field.getValue().toString().length() == 0)) {
						PropertyMembership membership = new PropertyMembershipFinder().setPropertyClassPk(propertyAndValueBean.getPropertyClass().getPk()).setPropertyPk(pk).find();
						if (membership != null && membership.isRequired()) {
							return false;
						}

						PropertyValueFinder propertyValueFinder = new PropertyValueFinder().setOwnerClass(ownerClass);
						propertyValueFinder.setProperty(new PropertyFinder().setPk(pk).find());
						ArrayList<Long> longs = new ArrayList<Long>();
						longs.add(ownerPk);
						propertyValueFinder.setOwnerPk(longs);

						if (propertyValueFinder.exists()) {
							PropertyValue overriddenPropertyValue = propertyValueFinder.find();

							overriddenPropertyValue.delete();
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * Fjerner bønne med valgt propertyPk, bruker canBeRemoved
	 * for å bestemme om den bønnen skal fjernes fra containeren, eller
	 * om det skal vises arvet verdi.
	 * @param propertyPk pk til propertien brukt av PropertyValue
	 */
	public boolean removeBean(Long propertyPk) {
		if (containsId(propertyPk) && getPropertyValue(propertyPk) != null) {
			BeanItem<PropertyAndValueBean> propertyAndValueBeanBeanItem = getItem(propertyPk);
			PropertyAndValueBean propertyAndValueBeanBean = propertyAndValueBeanBeanItem.getBean();

			PropertyMembership membership = propertyAndValueBeanBean.getPropertyClass().getPropertyMembership(propertyAndValueBeanBean.getProperty());
			if (membership.isRequired()) {
				logger.error("Du prøver å sle slette en obligatorisk egenskap");
				return false;
			}
			//Hvis PropertyValue finnes i db og PropertyValue ikke er arvet
			if (propertyAndValueBeanBean.canBeRemoved()) {
				//Fjern bønnen fra containeren.
				removeItem(propertyPk);
			} else {
				logger.error("Egenskapen er arvet og kan ikke fjernes");
				return false;
			}
			sort();
		}
		return true;
	}

	/**
	 * Returnerer propertyvalue for property med valgt pk
	 * @param propertyPk pk til property
	 * @return PropertyValue for property med valgt pk
	 */
	public PropertyValue getPropertyValue(Object propertyPk) {
		BeanItem<PropertyAndValueBean> propertyContainer2 = getItem(propertyPk);
		return propertyContainer2.getBean().getPropertyValue();
	}

	/**
	 * Viser propertyValues baser på finderen
	 * @param propertyValueFinder finder for PropertyValues.
	 */
	public void displayPropertyValues(PropertyValueFinder propertyValueFinder, PropertyClass propertyClass, boolean readOnly) {
		for (PropertyValue propertyValue : propertyValueFinder.loadList()) {
			PropertyAndValueBean propertyAndValueBean = new PropertyAndValueBean(propertyValue.getProperty().load());
			propertyAndValueBean.setPropertyClass(propertyClass);
			propertyAndValueBean.setPropertyValue(propertyValue);
			propertyAndValueBean.setPropertyValueChangeListener(propertyValueChangeListener);
			propertyAndValueBean.setReadOnly(readOnly);
			addItem(propertyValue.getProperty().getPk(), propertyAndValueBean);
		}
		sort();
	}

	/**
	 * Lagrer propertyValues i containeren med ownerPk og OwnerClass satt til parameterenes verdi
	 * @param ownerPk    eierPk for verdiene
	 * @param ownerClass eierklasse for verdiene
	 * @return true hvis alt gikk bra
	 */
	public boolean savePropertyValues(Long ownerPk, Class<?> ownerClass) {
		for (Long propertyPk : getItemIds()) {
			BeanItem<PropertyAndValueBean> propertyContainer2 = getItem(propertyPk);
			propertyContainer2.getBean().saveNewPropertyValue(ownerPk, ownerClass);
		}
		return true;
	}

	public void commit() throws Validator.InvalidValueException {
		for (Long pk : getAllItemIds()) {
			PropertyAndValueBean propertyAndValueBean = getItem(pk).getBean();

			propertyAndValueBean.getPropertyValueField().validate();
			propertyAndValueBean.getPropertyValueField().commit();
		}
	}

	/**
	 * Legger til properties som ikke allerede er vist i containeren.
	 */
	public void showAllProperties(PropertyClass propertyClass, boolean readonly) {
		List<Property> properties;
		if (propertyClass != null) {
			properties = new PropertyFinder().setPropertyClassId(propertyClass.getId()).loadList();
		} else {
			properties = new PropertyFinder().loadList();
		}

		for (Property property : properties) {
			PropertyAndValueBean propertyAndValueBean = new PropertyAndValueBean(property);
			propertyAndValueBean.setPropertyClass(propertyClass);
			propertyAndValueBean.setPropertyValueChangeListener(propertyValueChangeListener);
			//Legg til properties som ikke er vist allerede
			if (!containsId(property.getPk())) {
				propertyAndValueBean.setReadOnly(readonly);
				addItem(property.getPk(), propertyAndValueBean);
			}
		}
		sort();
	}

	/**
	 * Legger til bønner for arvede propertyValues i containeren, gitt at det ikke allerede
	 * eksisterer en bønne med en property som har samme pk som den arvede propertyValue sin PK
	 * @param propertyValues arvede propertyvalues(kan være overskrevet)
	 */
	public void refreshHeridataryPropertyValues(List<PropertyValue> propertyValues) {
		for (PropertyValue propertyValue : propertyValues) {
			//Hvis det finnes en bønne for arvet propertyValue sin property
			if (getItem(propertyValue.getProperty().getPk()) != null) {
				BeanItem<PropertyAndValueBean> propertyContainer2 = getItem(propertyValue.getProperty().getPk());
				//Legg til pk og arvet verdi i mappet over arvede verdier
				PropertyValue inheritedPropertyValue = propertyValue.copy();
				propertyContainer2.getBean().setInheritedPropertyValue(inheritedPropertyValue);
			}
			//Hvis det ikke finnes en bønne for arvet propertyValue sin property
			else {
				//Opprett ny bønne for arvet PropertyValue
				PropertyAndValueBean propertyAndValueBean = new PropertyAndValueBean(propertyValue.getProperty());
				propertyAndValueBean.setBooleanRepresentation(booleanRepresentation);
				propertyAndValueBean.setShowTooltip(showTooltip);

				propertyAndValueBean.setPropertyValue(propertyValue);
				propertyAndValueBean.setPropertyValueChangeListener(propertyValueChangeListener);

				PropertyValue inheritedPropertyValue = propertyValue.copy();
				propertyAndValueBean.setInheritedPropertyValue(inheritedPropertyValue);

				addItem(propertyValue.getProperty().getPk(), propertyAndValueBean);
			}
		}
		sort();
	}

	/**
	 * Henter eventuell arvet propertyvalue sin value for propertyPk
	 * @param propertyPk pk til property som skal sjekkes for arv
	 * @return arvet verdi hvis den finnes, null hvis ikke
	 */
	public Object getInheritedPropertyValue(Long propertyPk) {
		BeanItem<PropertyAndValueBean> propertyContainer2 = getItem(propertyPk);
		if (propertyContainer2.getBean().getInheritedPropertyValue() != null) {
			if (propertyContainer2.getBean().getInheritedPropertyValue().getProperty().getPk().equals(propertyPk)) {
				if (propertyContainer2.getBean().getInheritedPropertyValue().getValue() != null) {
					return propertyContainer2.getBean().getInheritedPropertyValue();
				}
			}
		}
		return null;
	}

	/**
	 * Fjerner alle arvede propertyvalues fra containeren
	 */
	public void removeHereditedPropertyValues() {
		for (Long propertyPk : getItemIds()) {
			BeanItem<PropertyAndValueBean> propertyContainer2 = getItem(propertyPk);
			propertyContainer2.getBean().setPropertyValue(null);
			propertyContainer2.getBean().setInheritedPropertyValue(null);
		}
		sort();
	}

	/**
	 * Henter en property basert på ID
	 * @param id PK til property
	 * @return property med gitt pk
	 */
	public PropertyStub getProperty(Long id) {
		return getItem(id).getBean().getProperty();
	}

	/**
	 * Fjerner alle bønner og oppretter nye bønner fra listen over properites
	 * @param propertiesList liste over properties
	 */
	public void displayProperties(List<Property> propertiesList) {
		removeAllItems();
		for (Property property : propertiesList) {
			PropertyAndValueBean propertyAndValueBean = new PropertyAndValueBean(property);
			propertyAndValueBean.setBooleanRepresentation(booleanRepresentation);
			propertyAndValueBean.setPropertyValueChangeListener(propertyValueChangeListener);
			addItem(property.getPk(), propertyAndValueBean);
		}
		sort();
	}

	private void sort() {
		sort(sortFields, new boolean[]{true});
	}

	/**
	 * Sorter stigende på angitte kolonner
	 */
	public void setSortFields(Object[] sortFields) {
		this.sortFields = sortFields;
	}

	public void setPropertyValueChangeListener(IPropertyValueChangeListener propertyValueChangeListener) {
		this.propertyValueChangeListener = propertyValueChangeListener;
	}

	public void setPropertyReadOnly(final long propertyPk) {
		if (getItemIds().size() > 0.0) {
			if (getItem(propertyPk) != null) {
				getItem(propertyPk).getBean().setReadOnly(true);
			}
		}
	}
	
	/**
	 * @return shows property description as tooltip, default = true
	 */
	public boolean isShowTooltip() {
		return showTooltip;
	}

	/**
	 * @param showTooltip true = show property description as tooltip, false = do not show tooltip
	 */
	public void setShowTooltip(boolean showTooltip) {
		this.showTooltip = showTooltip;
	}
	
	/**
	 * @return how to show boolean values
	 * @see #setBooleanRepresentation(Class)
	 */
	public Class<?> getBooleanRepresentation() {
		return booleanRepresentation;
	}

	/**
	 * @param booleanRepresentation how to show boolean values. Use {@link OptionGroup}, {@link ComboBox}/{@link NativeSelect} or {@link CheckBox}. null => CheckBox
	 */
	public void setBooleanRepresentation(Class<?> booleanRepresentation) {
		this.booleanRepresentation = booleanRepresentation;
	}
}
