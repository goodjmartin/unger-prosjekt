 package no.goodtech.vaadin.tabs.status.ui;

import com.vaadin.event.UIEvents.PollEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalLayout;
import no.goodtech.vaadin.Poller.IPollablePanel;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.security.AccessFunction;
import no.goodtech.vaadin.security.AccessFunctionManager;
import no.goodtech.vaadin.tabs.IMenuView;
import no.goodtech.vaadin.tabs.status.model.StatusIndicatorLogEntryDTO;
import no.goodtech.vaadin.tabs.status.model.StatusIndicatorStub;

import java.util.List;

 @UIScope
 @SpringView(name = StatusTab.VIEW_ID)
public class StatusTab extends VerticalLayout implements IMenuView, IPollablePanel {

	public static final String VIEW_ID = "StatusTab";
	public static final String VIEW_NAME = ApplicationResourceBundle.getInstance("vaadin-status-indicator").getString("statusTab.name");

	private StatusIndicatorFilterPanel filterPanel;
	private StatusIndicatorLogEntryDTOMessyGrid messyGrid;
	private StatusIndicatorDetails statusIndicatorDetails;

    static {
        AccessFunctionManager.registerAccessFunction(new AccessFunction(VIEW_ID, ApplicationResourceBundle.getInstance("vaadin-status-indicator").getString("accessFunction.status.statusView")));
    }

    public StatusTab() {
		setSizeFull();

		messyGrid = new StatusIndicatorLogEntryDTOMessyGrid();
		messyGrid.addSelectionListener((SelectionListener<StatusIndicatorLogEntryDTO>) event -> {
			if (event.getFirstSelectedItem().isPresent())
				statusSelected(event.getFirstSelectedItem().get().getStatusIndicator());
		});

		statusIndicatorDetails = new StatusIndicatorDetails();

		filterPanel = new StatusIndicatorFilterPanel(finder -> {
			List<StatusIndicatorLogEntryDTO> result = finder.listStatusIndicatorWithLogEntry();
			messyGrid.refresh(result);
			statusIndicatorDetails.refresh(messyGrid.getSelectedStatusIndicator(), filterPanel);
			return result.size();
		});
		addComponent(filterPanel);

        final HorizontalSplitPanel horizontal = new HorizontalSplitPanel();
        horizontal.setSplitPosition(60);
        addComponent(horizontal);
        final VerticalLayout leftPanel = new VerticalLayout();

		leftPanel.setSizeFull();
		leftPanel.addComponent(messyGrid);
		leftPanel.setExpandRatio(messyGrid, 1);

        horizontal.addComponent(leftPanel);
        horizontal.addComponent(statusIndicatorDetails);

		setExpandRatio(horizontal, 1.0f);

		setSpacing(false);
		setMargin(false);
	}

    public void statusSelected(final StatusIndicatorStub statusLogger) {
		if (statusLogger != null) {
			statusIndicatorDetails.refresh(statusLogger, filterPanel);
		}
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public boolean isAuthorized(User user, final String value) {
        return AccessFunctionManager.isAuthorized(user, VIEW_ID);
    }

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
    	filterPanel.search();
	}

	@Override
	public void poll(PollEvent event) {
		filterPanel.search();
	}

	 public String getViewName() {
		return VIEW_NAME;
	}
}
