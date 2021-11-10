package no.goodtech.vaadin.properties.propertyLayout.propertyEditing.optionLayout;

import com.vaadin.event.Action;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

/**
 * Actionhandler for tabellen over valg, inneholder actions og kall til IOptionTableActionHandler interfacet
 * når de blir trigget
 */
public class OptionTableActionHandler implements Action.Handler {
	private static final Action ACTION_ADD = new Action(ApplicationResourceBundle.getInstance("vaadin-properties").getString("optionLayout.optionTable.action.add"));
	private static final Action ACTION_DELETE = new Action(ApplicationResourceBundle.getInstance("vaadin-properties").getString("optionLayout.optionTable.action.delete"));
	private static final Action ACTION_SHIFT_DOWN = new Action(ApplicationResourceBundle.getInstance("vaadin-properties").getString("optionLayout.optionTable.action.shiftDown"));
	private static final Action ACTION_SHIFT_UP = new Action(ApplicationResourceBundle.getInstance("vaadin-properties").getString("optionLayout.optionTable.action.shiftUp"));
	private static final Action ACTION_SORT_ALPHABETICALLY = new Action(ApplicationResourceBundle.getInstance("vaadin-properties").getString("optionLayout.optionTable.action.sortAlphabetically"));
	private final IOptionTableActionHandler iOptionTableActionHandler;

	public OptionTableActionHandler(IOptionTableActionHandler iOptionTableActionHandler) {
		this.iOptionTableActionHandler = iOptionTableActionHandler;
	}

	@Override
	public Action[] getActions(Object target, Object sender) {
		Action[] ACTIONS = new Action[]{ACTION_DELETE, ACTION_ADD, ACTION_SHIFT_UP, ACTION_SHIFT_DOWN, ACTION_SORT_ALPHABETICALLY};
		if (target != null) {
			return ACTIONS;
		} else {
			if (iOptionTableActionHandler.tableContainsItems()) {
				return new Action[]{ACTION_ADD, ACTION_SORT_ALPHABETICALLY};
			} else {
				return new Action[]{ACTION_ADD};
			}
		}
	}

	@Override
	public void handleAction(Action action, Object sender, Object target) {
		if (ACTION_DELETE == action) {
			iOptionTableActionHandler.actionDeleteItem(target);
		} else if (ACTION_ADD == action) {
			if (target != null) {
				iOptionTableActionHandler.actionAddItemAfter(target);
			} else {
				iOptionTableActionHandler.actionAddItem();
			}
		} else if (ACTION_SHIFT_UP == action) {
			iOptionTableActionHandler.actionShiftItemUp(target);
		} else if (ACTION_SHIFT_DOWN == action) {
			iOptionTableActionHandler.actionShiftItemDown(target);
		} else if (ACTION_SORT_ALPHABETICALLY == action) {
			iOptionTableActionHandler.actionSortItemsAlphabetically();
		}
	}
}
