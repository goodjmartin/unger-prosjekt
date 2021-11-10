package no.goodtech.vaadin.tabs.status.ui;

import no.cronus.common.date.DateTimeFactory;
import no.goodtech.vaadin.frontpage.model.FrontPageCardComponent;
import no.goodtech.vaadin.frontpage.model.IFrontPageCardComponent;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.security.AccessFunctionManager;
import no.goodtech.vaadin.tabs.status.model.StatusLogEntryFinder;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Front-page card for status grid
 */
@FrontPageCardComponent
public class StatusGridCardComponent extends StatusIndicatorLogEntryDTOMessyGrid implements IFrontPageCardComponent {

	public StatusGridCardComponent() {
		ArrayList<String> hiddenColumns = new ArrayList<>(Arrays.asList("lastMessage", "lastHeartbeat"));
		for (Column column : getColumns()){
			if (hiddenColumns.contains(column.getId())){
				column.setHidable(true).setHidden(true);
			}
		}
	}

	/**
	 * Default search
	 */
	@Override
	public void refresh(){
		refresh(new StatusLogEntryFinder().setCreatedAfter(DateTimeFactory.dateTime(-1, ChronoUnit.DAYS)).listStatusIndicatorWithLogEntry());
	}

	@Override
	public String getViewName() {
		return ApplicationResourceBundle.getInstance("vaadin-status-indicator").getString("statusTab.name");
	}

	@Override
	public String getParentViewId() {
		return StatusTab.VIEW_ID;
	}

	@Override
	public boolean isAuthorized(User user, String args) {
		return AccessFunctionManager.isAuthorized(user, StatusTab.VIEW_ID);
	}
}
