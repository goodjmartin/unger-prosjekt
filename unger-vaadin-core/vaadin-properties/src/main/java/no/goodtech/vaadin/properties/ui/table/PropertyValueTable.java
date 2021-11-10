package no.goodtech.vaadin.properties.ui.table;

import com.vaadin.event.Action;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.NativeSelect;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.Table;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.properties.model.PropertyClass;
import no.goodtech.vaadin.properties.model.PropertyClassFinder;
import no.goodtech.vaadin.properties.model.PropertyValue;
import no.goodtech.vaadin.properties.model.PropertyValueFinder;
import no.goodtech.vaadin.properties.ui.IPropertyValueChangeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Property verdi table som inneholder items av type Property og PropertyValue
 */
public class PropertyValueTable extends Table {
	//Action for bruk i actionlistener
	private static final Action ACTION_DELETE = new Action("Fjern egenskap");
	//Container for tabellen
	private final PropertyAndValueBeanContainer propertyValueContainer;
	//Eierklassen
	private Class<?> ownerClass;
	//EierPk
	private Long ownerPk;

	private Class<?> booleanRepresentation = CheckBox.class;
	private boolean showTooltip = true;

	private static String getText(String key) {
		return ApplicationResourceBundle.getInstance("vaadin-properties").getString(key);
	}

	public PropertyValueTable() {
		//Opprett containeren og sett datakilden
		setImmediate(true);
		propertyValueContainer = new PropertyAndValueBeanContainer();
		setContainerDataSource(propertyValueContainer);
		setColumnCollapsingAllowed(true);
		for (String fieldName : PropertyAndValueBean.Fields.getAll()) {
			setColumnHeader(fieldName, getText("propertyValueTable.column.header." + fieldName));
		}

		setSortAscending(false);
		setSizeFull();
		setMultiSelect(false);
		setSelectable(false);
	}

	@Override
	public List<PropertyAndValueBean> getItemIds(){
		List<PropertyAndValueBean> propertyAndValueBeans = new ArrayList<>();
		for (long pk : propertyValueContainer.getItemIds()){
			PropertyAndValueBean propertyAndValueBean = propertyValueContainer.getItem(pk).getBean();
			propertyAndValueBeans.add(propertyAndValueBean);
		}
		return propertyAndValueBeans;
	}

	/**
	 * Skjul angitte kolonner
	 * @param columns navn på kolonnene
	 */
	public void hideColumns(String... columns) {
		for (String column : columns) {
			setColumnCollapsed(column, true);
		}
	}

	/**
	 * Kalles på når propertyValueTabellen skal oppfriskes
	 * @param propertyValueFinder finder med filtere satt
	 * @param ownerClass          hvilken klasse som eier propertiene
	 * @param ownerPk             pk til klassen som eier propertiene
	 */
	public void refresh(PropertyValueFinder propertyValueFinder, Class<?> ownerClass, String propertyClassId, Long ownerPk) {
		this.ownerClass = ownerClass;
		this.ownerPk = ownerPk;
		propertyValueContainer.removeAllItems();
		propertyValueContainer.setBooleanRepresentation(booleanRepresentation);
		propertyValueContainer.setShowTooltip(showTooltip);
		String classId = (propertyClassId != null ? propertyClassId : ownerClass.getSimpleName());
		PropertyClass propertyClass = new PropertyClassFinder().setId(classId).find();
		propertyValueContainer.displayPropertyValues(propertyValueFinder, propertyClass, isReadOnly());
		setColumnExpandRatio("propertyValueField", 1.0f);
	}

	public void setOwnerPk(Long ownerPk) {
		this.ownerPk = ownerPk;
	}

	public void setOwnerclass(Class<?> ownerClass) {
		this.ownerClass = ownerClass;
	}


	/**
	 * Lagrer propertyvalues
	 * @return true om det gikk bra eller false om validering feilet og ingenting ble lagret
	 */
	public boolean saveProperties() {
		boolean success = propertyValueContainer.savePropertyValues(ownerPk, ownerClass);
		refreshRowCache();

		return success;
	}

	public void commitContainer() throws Validator.InvalidValueException {
		propertyValueContainer.commit();
	}

	/**
	 * Removes all the empty PropertyAndValueBeans from the container.
	 */
	public boolean removeBeansWithEmptyValues() {
		return propertyValueContainer.removeBeansWithEmptyValue(ownerClass, ownerPk);
	}

	/**
	 * Legg til alle mulige egenskaper i containeren.
	 */
	public void showAllProperties(PropertyClass propertyClass) {
		propertyValueContainer.showAllProperties(propertyClass, isReadOnly());
	}

	/**
	 * Legg til arvede egenskapsverdier i containeren
	 * @param inheritedPropertyValues arvede egenskaper
	 */
	public void addInheritanceProperties(List<PropertyValue> inheritedPropertyValues) {
		propertyValueContainer.refreshHeridataryPropertyValues(inheritedPropertyValues);
	}

	public void registerDeletePropertyValueActionHandler() {
		addActionHandler(new Action.Handler() {
			/**
			 * Gi mulighet til å slette hvis verdien ikke er arvet
			 * @param target itemID til en PropertyAndValueBean(Alltid Long)
			 * @param sender containeren
			 * @return ACTION_DELETE hvis lovlig
			 */
			public Action[] getActions(Object target, Object sender) {
				Action[] ACTIONS = new Action[]{ACTION_DELETE};
				if (target != null) {
					if (target.getClass().equals(java.lang.Long.class)) {
						PropertyValue propertyValue = propertyValueContainer.getPropertyValue(target);
						if (propertyValue != null) {
							//Hvis det er en reel propertyvalue(bruker pk og ownerclass for å kontrollere dette)
							if (propertyValue.getOwnerPk() != null && propertyValue.getOwnerClass() != null) {
								//Hvis det ikke er en arvet propertyvalue
								if (propertyValue.getOwnerPk().equals(ownerPk) && propertyValue.getOwnerClass() == ownerClass) {
									//Hvis verdien ikke er den samme som arvet verdi
									if (!propertyValue.getValue().equals(propertyValueContainer.getInheritedPropertyValue((Long) target)))
									//Gi mulighet til å slette.
									{
										return ACTIONS;
									}
								}
							}
						}
					}
				}
				return null;
			}

			/**
			 * Sletter propertyValue og fjerner bønnen fra tabellen,
			 * eller sletter propertyValue og viser arvet propertyValue i tabellen.
			 *
			 * @param action forespurt handling
			 * @param sender containeren
			 * @param target valgt bønne
			 */
			public void handleAction(Action action, Object sender, Object target) {
				if (ACTION_DELETE == action) {
					propertyValueContainer.removeBean((Long) target);
					refreshRowCache(); //Gjenskap radene i tabellen
				}
			}
		});
	}

	/**
	 * Removes the value from the selected property
	 */
	public boolean removeBean() {
		Object selected = getValue();
		if (selected != null) {
			propertyValueContainer.removeBean((Long) selected);
			refreshRowCache();
			return true;
		}
		return false;
	}

	public String getPropertyValue() {
		if (getValue() != null && propertyValueContainer.getPropertyValue(getValue()) != null) {
			return propertyValueContainer.getPropertyValue(getValue()).getValue().toString();
		}
		return null;
	}

	public void setPropertyValueChangeListener(IPropertyValueChangeListener propertyValueChangeListener) {
		propertyValueContainer.setPropertyValueChangeListener(propertyValueChangeListener);
	}

	public void setPropertyReadOnly(final long propertyPk) {
		propertyValueContainer.setPropertyReadOnly(propertyPk);
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
}
