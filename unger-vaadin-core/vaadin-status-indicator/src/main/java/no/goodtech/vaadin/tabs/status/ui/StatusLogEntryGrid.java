package no.goodtech.vaadin.tabs.status.ui;

import com.vaadin.data.ValueProvider;
import com.vaadin.ui.Component;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.LocalDateTimeRenderer;
import no.cronus.common.date.DateTimeUtils;
import no.goodtech.vaadin.lists.IMessyGrid;
import no.goodtech.vaadin.lists.MessyGrid;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.tabs.status.model.StatusLogEntryStub;

public class StatusLogEntryGrid extends MessyGrid<StatusLogEntryStub> {

	public StatusLogEntryGrid() {
		this(null);
	}

	public StatusLogEntryGrid(IMessyGrid<StatusLogEntryStub> actionListener) {
		super(actionListener);
		addColumn(p -> StateTypeIcon.getIconAsHtml(p.getStateType()), new HtmlRenderer()).setMinimumWidth(50);
		addColumn(StatusLogEntryStub::getCreated, new LocalDateTimeRenderer(DateTimeUtils.DATE_TIME_DETAIL_FORMAT)).setCaption(ApplicationResourceBundle.getInstance("vaadin-status-indicator").getString("statusLogEntryGrid.column.created")).setMinimumWidth(160);
		addComponentColumn((ValueProvider<StatusLogEntryStub, Component>) MessageInfo::new).setCaption(ApplicationResourceBundle.getInstance("vaadin-status-indicator").getString("statusLogEntryGrid.column.message")).setExpandRatio(1);
	}
}
