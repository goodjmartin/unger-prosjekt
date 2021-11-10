package no.goodtech.vaadin.tabs.status.ui;

import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.ui.*;
import no.cronus.common.date.DateTimeFactory;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.search.FilterPanel;
import no.goodtech.vaadin.tabs.status.common.StateType;
import no.goodtech.vaadin.tabs.status.model.StatusLogEntryFinder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Created by IntelliJ IDEA
 * <p/>
 * User: bakke
 */
public class StatusIndicatorFilterPanel extends FilterPanel<StatusLogEntryFinder> {

	private static final Locale LOCALE = new Locale("no", "NO");

	private SuccessCheckBox successCheckBox;
	private WarningCheckBox warningCheckBox;
	private FailureCheckBox failureCheckBox;
	private DateTimeField startDate;
	private DateTimeField endDate;
	private TextField searchTextField;

	public StatusIndicatorFilterPanel(FilterActionListener<StatusLogEntryFinder> listener) {
		super(listener);
		showFinderCount = false;
		VerticalLayout statuses = new VerticalLayout();
		statuses.setSizeFull();

		// Success Checkbox
		successCheckBox = new SuccessCheckBox();
		statuses.addComponent(successCheckBox);

		// Warning Checkbox
		warningCheckBox = new WarningCheckBox();
		statuses.addComponent(warningCheckBox);

		// Failure Checkbox
		failureCheckBox = new FailureCheckBox();
		statuses.addComponent(failureCheckBox);

		// Set component alignment
		statuses.setComponentAlignment(successCheckBox, Alignment.TOP_LEFT);
		statuses.setComponentAlignment(warningCheckBox, Alignment.TOP_LEFT);
		statuses.setComponentAlignment(failureCheckBox, Alignment.TOP_LEFT);

		// Start time
		startDate = new DateTimeField(ApplicationResourceBundle.getInstance("vaadin-status-indicator").getString("statusIndicatorFilterPanel.startDate"));
		startDate.setResolution(DateTimeResolution.MINUTE);
		startDate.setValue(DateTimeFactory.dateTime(-1, ChronoUnit.DAYS));
		startDate.setLocale(LOCALE);

		// End time
		endDate = new DateTimeField(ApplicationResourceBundle.getInstance("vaadin-status-indicator").getString("statusIndicatorFilterPanel.endDate"));
		startDate.setResolution(DateTimeResolution.MINUTE);
		startDate.setLocale(LOCALE);

		// Search field
		searchTextField = new TextField(ApplicationResourceBundle.getInstance("vaadin-status-indicator").getString("statusIndicatorFilterPanel.search"));

		addComponents(statuses, startDate, endDate, searchTextField);
		layout.setComponentAlignment(startDate, Alignment.MIDDLE_CENTER);
		layout.setComponentAlignment(endDate, Alignment.MIDDLE_CENTER);
		layout.setComponentAlignment(searchTextField, Alignment.MIDDLE_CENTER);
		layout.setComponentAlignment(maxRowsTextField, Alignment.MIDDLE_CENTER);
		layout.setComponentAlignment(disabledCheckBox, Alignment.MIDDLE_CENTER);
		layout.setComponentAlignment(searchButton, Alignment.MIDDLE_CENTER);
		layout.setComponentAlignment(countLabel, Alignment.MIDDLE_CENTER);
	}

	public List<StateType> getStateTypes(){
		List<StateType> selectedTypes = new ArrayList<>();
		if (successCheckBox.getValue())
			selectedTypes.add(StateType.SUCCESS);
		if (warningCheckBox.getValue())
			selectedTypes.add(StateType.WARNING);
		if (failureCheckBox.getValue())
			selectedTypes.add(StateType.FAILURE);
		return selectedTypes;
	}

	public LocalDateTime getCreatedAfter(){
		return startDate.getValue();
	}

	public LocalDateTime getCreatedBefore(){
		return endDate.getValue();
	}

	@Override
	public StatusLogEntryFinder getFinder() {
		StatusLogEntryFinder finder = new StatusLogEntryFinder();

		if (searchTextField.getValue() != null && !searchTextField.getValue().isEmpty())
			finder.setMessage(searchTextField.getValue(), true);

		if (endDate.getValue() != null && startDate.getValue() != null) {
			finder.setBetween(startDate.getValue(), endDate.getValue());
		} else if (startDate.getValue() != null) {
			finder.setCreatedAfter(startDate.getValue());
		} else {
			finder.setCreatedBefore(endDate.getValue());
		}

		// State type statusFilter
		Set<StateType> stateTypes = new HashSet<>();
		if (successCheckBox.getValue())
			stateTypes.add(StateType.SUCCESS);

		if (warningCheckBox.getValue())
			stateTypes.add(StateType.WARNING);

		if (failureCheckBox.getValue())
			stateTypes.add(StateType.FAILURE);

		finder = finder.setStateTypes(stateTypes);
		return finder;
	}

}
