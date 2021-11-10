package no.goodtech.dashboard.ui.admin;

import no.goodtech.dashboard.config.ui.DashboardConfigStub;
import no.goodtech.dashboard.ui.Texts;
import no.goodtech.vaadin.lists.MessyGrid;

public class DashboardConfigGrid extends MessyGrid<DashboardConfigStub> {

	public DashboardConfigGrid() {
		addColumn(column -> {
			if (column.getArea() != null) {
				return column.getArea().getName();
			}
			return null;
		}).setCaption(Texts.get("dashboardConfig.table.area.caption"));

		addColumn(DashboardConfigStub::getId).setCaption(Texts.get("dashboardConfig.table.id.caption"));
		addColumn(DashboardConfigStub::getTitle).setCaption(Texts.get("dashboardConfig.table.title.caption"));
		addColumn(DashboardConfigStub::getNumRows)
				.setCaption(Texts.get("dashboardConfig.table.numRows.caption"))
				.setStyleGenerator(item -> "v-align-right");
		addColumn(DashboardConfigStub::getNumColumns)
				.setCaption(Texts.get("dashboardConfig.table.numColumns.caption"))
				.setStyleGenerator(item -> "v-align-right");
		addColumn(DashboardConfigStub::getRefreshIntervalInSeconds)
				.setCaption(Texts.get("dashboardConfig.table.refreshIntervalInSeconds.caption"))
				.setStyleGenerator(item -> "v-align-right");

		//TODO: Enable this when styling in Valo theme is fixed: setSelectionMode(SelectionMode.MULTI);
		//TODO: Right-align numeric header rows
//		HeaderRow headerRow = getDefaultHeaderRow();
//		headerRow.getCell("id").setStyleName("v-align-right");
	}
}
