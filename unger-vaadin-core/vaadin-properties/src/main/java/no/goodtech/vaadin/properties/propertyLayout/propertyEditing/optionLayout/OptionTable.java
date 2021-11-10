package no.goodtech.vaadin.properties.propertyLayout.propertyEditing.optionLayout;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.themes.Runo;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Tabell som inneholder valglisten for en egenskap som støtter flere valg
 */
public class OptionTable extends Table implements IOptionTableActionHandler {
	public static final String OPTION_TABLE_COLUMN_HEADER = ApplicationResourceBundle.getInstance("vaadin-properties").getString("optionLayout.optionTable.ColumnHeader");
	private static final String OPTION_TABLE_DEFAULT_VALUE = ApplicationResourceBundle.getInstance("vaadin-properties").getString("optionLayout.optionTable.DefaultValue");

	/**
	 * Registrer actionhandler og
	 *
	 */
	public OptionTable() {
		setWidth(100, Unit.PERCENTAGE);
		setImmediate(true);
		setStyleName(Runo.TABLE_SMALL);

		//Legger til den ene kolonnen tabellen består av
		addContainerProperty(OPTION_TABLE_COLUMN_HEADER, String.class, null);
		//Redigering rett i kolonnen
		setEditable(true);
		//Ingen grunn til å skulle kunne velge en rad
		setSelectable(false);

		//Registrer actionhandler for høyreklikk hendelser
		OptionTableActionHandler optionTableActionHandler = new OptionTableActionHandler(this);
		addActionHandler(optionTableActionHandler);

	}

	/**
	 * Returnererer valgene i valg-tabellen
	 * @return liste av String variable som tilsvarer valgene i tabellen.
	 */
	public List<String> getOptions() {
		Collection<?> itemIds = getItemIds();
		Object[] objects = itemIds.toArray();
		List<String> optionList = new ArrayList<String>();
		for (Object object : objects) {
			String value = (String) getItem(object).getItemProperty(OptionTable.OPTION_TABLE_COLUMN_HEADER).getValue();
			optionList.add(value);
		}
		return optionList;
	}

	/**
	 * Fjerner alle items og oppfrisker PropertyValueTable med alle valgmulighetene til egenskapen, hvis den har noen
	 *
	 * @param selectedProperty valgt egenskap
	 */
	public void refreshPropertyValueTable(final no.goodtech.vaadin.properties.model.Property selectedProperty) {
		removeAllItems();
		List<String> options = selectedProperty.getOptionsList();
		for (String option : options) {
			addItem(new Object[]{option}, option);
		}
	}

	/**
	 * Fjerner Item fra tabellen
	 * @param target raden som har blitt høyreklikket på
	 */
	@Override
	public void actionDeleteItem(Object target) {
		removeItem(target);
	}

	/**
	 * Legger til et nytt item, med itemidentifier lik størrelsen på tabellen pluss 1
	 * Setter verdien til default verdi og velger den
	 */
	@Override
	public void actionAddItem() {
		Item newPropertyOption = addItem(size() + 1);
		newPropertyOption.getItemProperty(OPTION_TABLE_COLUMN_HEADER).setValue(OPTION_TABLE_DEFAULT_VALUE);
		select(newPropertyOption);
	}

	/**
	 * Returnerer true hvis tabellen inneholder items, false ellers
	 * @return true hvis tabellen inneholder items, false ellers
	 */
	@Override
	public boolean tableContainsItems() {
		return !getItemIds().isEmpty();
	}

	/**
	 * Legger til nytt Item etter targetItemId sin index
	 * @param targetItemId itemID til itemet det skal legges til et item etter
	 */
	@Override
	public void actionAddItemAfter(Object targetItemId) {
		Item newPropertyOption = addItemAfter(targetItemId, size() + 1);
		newPropertyOption.getItemProperty(OPTION_TABLE_COLUMN_HEADER).setValue(OPTION_TABLE_DEFAULT_VALUE);
		select(newPropertyOption);
	}

	/**
	 * Hvis target item og parent item eksisterer slettes de,
	 * og det legges til nye tilsvarende items med samme itemID og
	 * verdi, men med indexen byttet
	 * @param targetItemId itemID til itemet som skal flyttes opp
	 */
	@Override
	public void actionShiftItemUp(Object targetItemId) {
		IndexedContainer container = (IndexedContainer) getContainerDataSource();
		int indexOfSelectedItem = container.indexOfId(targetItemId);
		if (indexOfSelectedItem > 0) {
			Object parentItem = container.getIdByIndex(indexOfSelectedItem - 1);
			if (parentItem != null) {
				String targetValue = getItem(targetItemId).getItemProperty(OPTION_TABLE_COLUMN_HEADER).getValue().toString();
				String parentValue = getItem(parentItem).getItemProperty(OPTION_TABLE_COLUMN_HEADER).getValue().toString();
				removeItem(targetItemId);
				removeItem(parentItem);
				Item firstAddedItem = container.addItemAt(indexOfSelectedItem - 1, targetItemId);
				firstAddedItem.getItemProperty(OPTION_TABLE_COLUMN_HEADER).setValue(targetValue);
				Item secondAddedItem = container.addItemAt(indexOfSelectedItem, parentItem);
				secondAddedItem.getItemProperty(OPTION_TABLE_COLUMN_HEADER).setValue(parentValue);
			}
		}
	}


	/**
	 * Sorterer tabellen ascending på den ene kolonnen tabellen har
	 */
	@Override
	public void actionSortItemsAlphabetically() {
		sort(new Object[]{OPTION_TABLE_COLUMN_HEADER}, new boolean[]{true});
	}

	/**
	 * Hvis target item og child item eksisterer slettes de,
	 * og det legges til nye tilsvarende items med samme itemID og
	 * verdi, men med indexen byttet
	 * @param targetItemId itemID til itemet som skal flyttes opp
	 */
	@Override
	public void actionShiftItemDown(Object targetItemId) {
		IndexedContainer container = (IndexedContainer) getContainerDataSource();
		int indexOfSelectedItem = container.indexOfId(targetItemId);
		int indexOfChildItem = (indexOfSelectedItem + 1);
		int indexexSize = container.getItemIds().size();
		if (indexOfChildItem < indexexSize) {
			Object childItem = container.getIdByIndex(indexOfChildItem);
			if (childItem != null) {
				String targetValue = getItem(targetItemId).getItemProperty(OPTION_TABLE_COLUMN_HEADER).getValue().toString();
				String childValue = getItem(childItem).getItemProperty(OPTION_TABLE_COLUMN_HEADER).getValue().toString();
				removeItem(targetItemId);
				removeItem(childItem);
				Item secondAddedItem = container.addItemAt(indexOfSelectedItem, childItem);
				secondAddedItem.getItemProperty(OPTION_TABLE_COLUMN_HEADER).setValue(childValue);
				Item firstAddedItem = container.addItemAt(indexOfChildItem, targetItemId);
				firstAddedItem.getItemProperty(OPTION_TABLE_COLUMN_HEADER).setValue(targetValue);
			}
		}
	}
}
