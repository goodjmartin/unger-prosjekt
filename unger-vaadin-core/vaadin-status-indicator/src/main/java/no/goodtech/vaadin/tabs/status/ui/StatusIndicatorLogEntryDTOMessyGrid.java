package no.goodtech.vaadin.tabs.status.ui;

import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.LocalDateTimeRenderer;
import no.cronus.common.date.DateTimeUtils;
import no.goodtech.vaadin.lists.MessyGrid;
import no.goodtech.vaadin.tabs.status.Texts;
import no.goodtech.vaadin.tabs.status.common.StateType;
import no.goodtech.vaadin.tabs.status.model.StatusIndicatorLogEntryDTO;
import no.goodtech.vaadin.tabs.status.model.StatusIndicatorStub;

import java.time.LocalDateTime;

public class StatusIndicatorLogEntryDTOMessyGrid extends MessyGrid<StatusIndicatorLogEntryDTO> {

	static final String PROPERTY_NAME = getHeaderText("name");
	static final String PROPERTY_LAST_HEARTBEAT = getHeaderText("lastHeartBeat");
	static final String PROPERTY_TYPE = "TYPE";
	static final String PROPERTY_LAST_MESSAGE = getHeaderText("lastMessage");

	public StatusIndicatorLogEntryDTOMessyGrid() {
		setSizeFull();

		// Set a style name (so we can style rows and cells)
		addStyleName("statusTable");

		addColumn((ValueProvider<StatusIndicatorLogEntryDTO, String>) statusIndicatorLogEntryDTO -> (statusIndicatorLogEntryDTO.getStatusIndicator().isOkBool()) ? VaadinIcons.SMILEY_O.getHtml() : null, new HtmlRenderer()).setId("ok");
		addColumn((ValueProvider<StatusIndicatorLogEntryDTO, String>) statusIndicatorLogEntryDTO -> statusIndicatorLogEntryDTO.getStatusIndicator().getName()).setCaption(PROPERTY_NAME).setId("name");
		addColumn((ValueProvider<StatusIndicatorLogEntryDTO, String>) statusIndicatorLogEntryDTO -> statusIndicatorLogEntryDTO.getStatusIndicator().getMessage()).setCaption(PROPERTY_LAST_MESSAGE).setExpandRatio(1).setId("lastMessage");
		addColumn((ValueProvider<StatusIndicatorLogEntryDTO, LocalDateTime>) statusIndicatorLogEntryDTO -> statusIndicatorLogEntryDTO.getStatusIndicator().getLastHeartbeatAt()).setRenderer(new LocalDateTimeRenderer(DateTimeUtils.DATE_TIME_DETAIL_FORMAT)).setCaption(PROPERTY_LAST_HEARTBEAT).setId("lastHeartbeat");
		addColumn(StatusIndicatorLogEntryDTO::getSuccesses).setCaption(getHeaderText(StateType.SUCCESS)).setHidable(true).setHidden(true).setStyleGenerator(item -> "v-align-right v-table-cell-content-statusIndicator-SUCCESS").setMinimumWidth(90).setId("success");
		addColumn(StatusIndicatorLogEntryDTO::getWarnings).setCaption(getHeaderText(StateType.WARNING)).setMinimumWidth(90).setStyleGenerator((StyleGenerator<StatusIndicatorLogEntryDTO>) item -> {
			if (item != null && (item.getWarnings() == null || item.getWarnings() == 0))
				return "v-align-right  v-table-cell-content-statusIndicator-SUCCESS";
			return "v-align-right  v-table-cell-content-statusIndicator-WARNING";
		}).setId("warning");
		addColumn(StatusIndicatorLogEntryDTO::getFails).setCaption(getHeaderText(StateType.FAILURE)).setMinimumWidth(90).setStyleGenerator((StyleGenerator<StatusIndicatorLogEntryDTO>) item -> {
			if (item != null && (item.getFails() == null || item.getFails() == 0))
				return "v-align-right  v-table-cell-content-statusIndicator-SUCCESS";
			return "v-align-right  v-table-cell-content-statusIndicator-FAILURE";
		}).setId("failure");

		setSelectionMode(SelectionMode.SINGLE);
	}

	static String getHeaderText(StateType stateType) {
		return getHeaderText(stateType.toString());
	}

	public StatusIndicatorStub getSelectedStatusIndicator(){
		return getSelectedItem() != null ? getSelectedItem().getStatusIndicator() : null;
	}

	private static String getHeaderText(String header) {
		return Texts.get("statusTable.header." + header);
	}
}
