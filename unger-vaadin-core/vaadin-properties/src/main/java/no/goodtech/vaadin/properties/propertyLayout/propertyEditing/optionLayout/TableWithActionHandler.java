package no.goodtech.vaadin.properties.propertyLayout.propertyEditing.optionLayout;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Table;

public class TableWithActionHandler extends Table implements IOptionTableActionHandler {
	public static final String SORT_COLUMN = "Valg";


	public TableWithActionHandler() {
		OptionTableActionHandler optionTableActionHandler = new OptionTableActionHandler(this);
		addActionHandler(optionTableActionHandler);

	}


	@Override
	public boolean tableContainsItems() {
		return !getItemIds().isEmpty();
	}

	@Override
	public void actionAddItemAfter(Object previousItemId) {
		Item newPropertyOption = addItemAfter(previousItemId, size() + 1);
		select(newPropertyOption);
	}

	@Override
	public void actionDeleteItem(Object target) {
		removeItem(target);
	}

	@Override
	public void actionAddItem() {
		Item newPropertyOption = addItem(size() + 1);
		select(newPropertyOption);
	}

	@Override
	public void actionShiftItemUp(Object targetItemId) {
		IndexedContainer container = (IndexedContainer) getContainerDataSource();
		int indexOfSelectedItem = container.indexOfId(targetItemId);
		if (indexOfSelectedItem > 0) {
			Object parentItemId = container.getIdByIndex(indexOfSelectedItem - 1);
			if (parentItemId != null) {
			    Item target = getItem(targetItemId);
				Item parent = getItem(parentItemId);

				removeItem(targetItemId);
				removeItem(parentItemId);

				Item shiftUpItem = container.addItemAt(indexOfSelectedItem - 1, targetItemId);
				Item shiftDownItem = container.addItemAt(indexOfSelectedItem, parentItemId);
				for(Object shiftUpItemPropertyId :target.getItemPropertyIds()){
					shiftUpItem.getItemProperty(shiftUpItemPropertyId).setValue(target.getItemProperty(shiftUpItemPropertyId).getValue());
				}
				for(Object shiftDownItemPropertyId : parent.getItemPropertyIds()){
					shiftDownItem.getItemProperty(shiftDownItemPropertyId).setValue(target.getItemProperty(shiftDownItemPropertyId).getValue());
				}
			}
		}
	}

	@Override
	public void actionSortItemsAlphabetically() {
		sort(new Object[]{SORT_COLUMN}, new boolean[]{true});
	}

	@Override
	public void actionShiftItemDown(Object target) {
		IndexedContainer container = (IndexedContainer) getContainerDataSource();
		int indexOfSelectedItem = container.indexOfId(target);
		int indexOfChildItem = (indexOfSelectedItem + 1);
		int indexexSize = container.getItemIds().size();
		if (indexOfChildItem < indexexSize) {
			Object childItem = container.getIdByIndex(indexOfChildItem);
			if (childItem != null) {
				removeItem(target);
				removeItem(childItem);
				container.addItemAt(indexOfSelectedItem, childItem);
				container.addItemAt(indexOfChildItem, target);
			}
		}
	}
}
