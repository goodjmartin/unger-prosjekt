package no.goodtech.vaadin.ui;

import com.vaadin.ui.Grid;
import com.vaadin.v7.ui.Table;
import no.goodtech.persistence.entity.EntityStub;

public class Utils {

	/**
	 * Select newest item (based on item with highest pk) in given table
	 */
	public static void selectNewestItem(Table table) {
		EntityStub<?> newestItem = null;
		for (Object item : table.getItemIds()) {
			if (item instanceof EntityStub<?>) {
				EntityStub<?> entity = (EntityStub<?>) item;
				if (newestItem == null || entity.getPk() > newestItem.getPk())
					newestItem = entity;
			}
		}
		if (newestItem != null) {
			table.setValue(null);
			table.select(newestItem);
		}
	}

	/**
	 * Select newest item (based on item with highest pk) in given table
	 */
//	public static void selectNewestItem(Grid table) {
//		EntityStub<?> newestItem = null;
//		for (Object item : table.getDataProvider().ItemIds()) {
//			if (item instanceof EntityStub<?>) {
//				EntityStub<?> entity = (EntityStub<?>) item;
//				if (newestItem == null || entity.getPk() > newestItem.getPk())
//					newestItem = entity;
//			}
//		}
//		if (newestItem != null) {
//			table.setValue(null);
//			table.select(newestItem);
//		}
//	}

}
