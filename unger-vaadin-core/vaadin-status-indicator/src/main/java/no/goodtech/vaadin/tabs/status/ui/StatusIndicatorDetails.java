package no.goodtech.vaadin.tabs.status.ui;

import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import no.goodtech.vaadin.tabs.status.common.StateType;
import no.goodtech.vaadin.tabs.status.model.StatusIndicatorStub;
import no.goodtech.vaadin.tabs.status.model.StatusLogEntryFinder;

import java.util.ArrayList;

public class StatusIndicatorDetails extends VerticalLayout {

    private final StatusLogEntryGrid grid;

	public StatusIndicatorDetails() {
		setSpacing(true);

        // Add a horizontal layout for the text fields
		setSizeFull();

		grid = new StatusLogEntryGrid();
		grid.setColumnReorderingAllowed(false);
		grid.setSelectionMode(Grid.SelectionMode.NONE);
		for (Grid.Column column : grid.getColumns()) {
			column.setSortable(false);
		}

		addComponent(grid);
    }

    public void refresh(final StatusIndicatorStub statusLogger, final StatusIndicatorFilterPanel filterPanel) {
		grid.refresh(new ArrayList<>());
		if (statusLogger != null) {
			StatusLogEntryFinder finder = new StatusLogEntryFinder().setStatusIndicator(statusLogger).setStateTypes(filterPanel.getStateTypes().toArray(new StateType[0]));
			if (filterPanel.getCreatedAfter() != null)
				finder.setCreatedAfter(filterPanel.getCreatedAfter());
			if (filterPanel.getCreatedBefore() != null)
				finder.setCreatedBefore(filterPanel.getCreatedBefore());
			grid.refresh(finder.list());
		}
    }
}
