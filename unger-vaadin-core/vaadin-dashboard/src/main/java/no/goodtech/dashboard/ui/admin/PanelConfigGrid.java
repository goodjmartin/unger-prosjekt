package no.goodtech.dashboard.ui.admin;

import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.components.grid.HeaderRow;
import no.goodtech.dashboard.config.ui.PanelConfig;
import no.goodtech.dashboard.ui.Texts;
import no.goodtech.vaadin.lists.IMessyGrid;
import no.goodtech.vaadin.lists.MessyGrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PanelConfigGrid extends MessyGrid<PanelConfig> {
	public PanelConfigGrid(IMessyGrid<PanelConfig> actionListener) {
		super(actionListener);

		addColumn(PanelConfig::getTitle).setCaption(Texts.get("panelConfig.table.title.caption"));
		final StyleGenerator<PanelConfig> rightAlignStyleGenerator = item -> "v-align-right";
		final Column<PanelConfig, Integer> startRowColumn = addColumn(PanelConfig::getStartRow)
				.setCaption(Texts.get("panelConfig.table.startRow.caption"))
				.setStyleGenerator(rightAlignStyleGenerator);
		final Column<PanelConfig, Integer> endRowColumn = addColumn(PanelConfig::getEndRow)
				.setCaption(Texts.get("panelConfig.table.endRow.caption"))
				.setStyleGenerator(rightAlignStyleGenerator);
		final Column<PanelConfig, Integer> startColumnColumn = addColumn(PanelConfig::getStartColumn)
				.setCaption(Texts.get("panelConfig.table.startColumn.caption"))
				.setStyleGenerator(rightAlignStyleGenerator);
		final Column<PanelConfig, Integer> endColumnColumn = addColumn(PanelConfig::getEndColumn)
				.setCaption(Texts.get("panelConfig.table.endColumn.caption"))
				.setStyleGenerator(rightAlignStyleGenerator);
		addColumn(PanelConfig::isTimeShift)
				.setCaption(Texts.get("panelConfig.table.timeShift.caption"))
				.setStyleGenerator(rightAlignStyleGenerator);
		addColumn(PanelConfig::getPeriodLengthInMinutes)
				.setCaption(Texts.get("panelConfig.table.periodLengthInMinutes.caption"))
				.setStyleGenerator(rightAlignStyleGenerator);

		final HeaderRow row = prependHeaderRow();
		row.join(row.getCell(startRowColumn), row.getCell(endRowColumn)).setText("Rad");
		row.join(row.getCell(startColumnColumn), row.getCell(endColumnColumn)).setText("Kolonne");

		setSortOrder(GridSortOrder.asc(startRowColumn).thenAsc(startColumnColumn));

		//TODO: Right-align numeric header rows
//		HeaderRow row = getDefaultHeaderRow();
//		row.getCell("id").setStyleName("v-align-right");
		setHeightByRowsWithMaxLimit(12);
		setCaption(Texts.get("panelConfig.table.caption"));
	}
}
