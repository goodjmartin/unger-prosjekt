package no.goodtech.vaadin.properties.ui.table;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.NativeSelect;
import com.vaadin.v7.ui.OptionGroup;
import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.properties.model.Property;
import no.goodtech.vaadin.properties.model.PropertyClass;
import no.goodtech.vaadin.properties.model.PropertyClassFinder;
import no.goodtech.vaadin.properties.model.PropertyValue;
import no.goodtech.vaadin.properties.model.PropertyValueFinder;
import no.goodtech.vaadin.properties.ui.IPropertyValueChangeListener;
import no.goodtech.vaadin.properties.ui.table.PropertyAndValueBean.Fields;

import java.util.ArrayList;
import java.util.List;

/**
 * Viser kundespesifikke egenskaper for en bestemt eier og gir mulighet for å redigere disse
 */
public class PropertyValueEditPanel extends HorizontalLayout {
	private final PropertyValueTable table;

	private String propertyClassId;

	private Class<?> booleanRepresentation = CheckBox.class;
	private boolean showTooltip = true;
	private boolean isReadOnly;

	public PropertyValueEditPanel(final String propertyClassId) {
		this.propertyClassId = propertyClassId;

		table = new PropertyValueTable();
		setVisibleColumns(Property.Fields.NAME, PropertyAndValueBean.Fields.VALUE_FIELD,
				Fields.PROPERTY_VALUE_PREFIX + PropertyValue.Fields.DESCRIPTION, Property.Fields.UNIT_OF_MEASURE);

		setColumnCollapsed(Fields.PROPERTY_VALUE_PREFIX + PropertyValue.Fields.DESCRIPTION, true);
		table.setItemDescriptionGenerator(new PropertyValueDescriptionGenerator());

		setCaption(getCaption("tableCaption"));
		addComponent(table);
		setExpandRatio(table, 1.0f);
		setSizeFull();
		setMargin(false);
	}

	/**
	 * Find the propertyClass for the owner
	 * @return the PropertyClass
	 */
	private PropertyClass getPropertyClass() {
		return new PropertyClassFinder().setId(propertyClassId).find();
	}

	/**
	 * Henter egenskaper for angitt eier og viser disse
	 * @param owner eieren av egenskapene
	 */
	public void refresh(final EntityStub<?> owner) {
		table.setBooleanRepresentation(booleanRepresentation);
		table.setShowTooltip(showTooltip);
		table.refresh(new PropertyValueFinder().setOwner(owner), owner.getClass(), propertyClassId, owner.getPk());
		table.showAllProperties(getPropertyClass());
	}

	/**
	 * Gets the owner's properties. The PropertyClass is set to propertyClassId.
	 * If the propertyClassId is null the PropertyClass for the panel has id = owner.getSimpleName()
	 * @param owner
	 * @param propertyClassId
	 */
	public void refresh(final EntityStub<?> owner, final String propertyClassId) {
		this.propertyClassId = propertyClassId;
		refresh(owner);
	}

	@Override
	public void setReadOnly(boolean readOnly){
		//getState().readOnly = readOnly;
		isReadOnly = readOnly;
		table.setReadOnly(readOnly);
	}


	public boolean isReadOnly() {
		return isReadOnly;
	}


	/**
	 * Removes all the empty PropertyAndValueBeans from the container.
	 */
	public boolean removeBeansWithEmptyValues() {
		return table.removeBeansWithEmptyValues();
	}

	/**
	 * Legger til arvede egenskaper i tabellen
	 * @param inheritedPropertyValues liste over arvede egenskaper
	 */
	public void refreshInheritedPropertyValues(List<PropertyValue> inheritedPropertyValues) {
		table.addInheritanceProperties(inheritedPropertyValues);
	}

	/**
	 * Skjul angitte kolonner
	 * @param columns navn på kolonnene
	 */
	public void hideColumns(String... columns) {
		table.hideColumns(columns);
	}

	/**
	 * @param propertyId  columns property id
	 * @param expandRatio the expandRatio used to divide excess space for this column
	 */
	public void setColumnExpandRatio(Object propertyId, float expandRatio) {
		table.setColumnExpandRatio(propertyId, expandRatio);
	}

	/**
	 * Saves all the propertyvalues
	 * TODO better way to save new entries than to set the owner for every save
	 * @return true om egenskapene ble lagret, false om noe gikk galt
	 */
	public boolean save(EntityStub owner) {
		table.setOwnerPk(owner.getPk());
		return save();
	}

	public boolean save() {
		if (table.saveProperties()) {
			table.showAllProperties(getPropertyClass());
		}
		return true;
	}

	/**
	 * Removes beans with empty values and validates the fields
	 * @throws Validator.InvalidValueException
	 */
	public void commit() throws Validator.InvalidValueException {
		table.commitContainer();

/*
	if (!removeBeansWithEmptyValues()) {
			throw new Validator.InvalidValueException("Kan ikke slette egenskap som er obligatorisk!");
		}*/
	}

	/**
	 * Vis kolonner med angitt navn. Du kan bruke konstanter fra
	 * {@link no.goodtech.vaadin.properties.model.Property.Fields} og {@link Fields}
	 */
	public void setVisibleColumns(Object... columns) {
		List<String> columnList = new ArrayList<String>();
		for (Object column : columns) {
			columnList.add(Fields.get(column));
		}
		table.setVisibleColumns(columnList.toArray());
	}

	public void setColumnCollapsed(final String column, boolean collapsed) {
		table.setColumnCollapsed(column, collapsed);
	}

	/**
	 * Binder en actionhandler for sletting av egenskapverdi til tabellen
	 */
	public void registerDeletePropertyValueActionHandler() {
		table.registerDeletePropertyValueActionHandler();
	}

	public void setPropertyValueChangeListener(IPropertyValueChangeListener propertyValueChangeListener) {
		table.setPropertyValueChangeListener(propertyValueChangeListener);
	}

	private static String getCaption(String key) {
		return ApplicationResourceBundle.getInstance("vaadin-properties").getString("propertyValueEditPanel.caption." + key);
	}

	public void setPropertyReadOnly(final long propertyPk) {
		table.setPropertyReadOnly(propertyPk);
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
