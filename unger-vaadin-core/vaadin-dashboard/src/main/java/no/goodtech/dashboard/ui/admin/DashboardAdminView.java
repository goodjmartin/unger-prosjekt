package no.goodtech.dashboard.ui.admin;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import no.goodtech.dashboard.Globals;
import no.goodtech.dashboard.config.ui.DashboardConfig;
import no.goodtech.dashboard.config.ui.DashboardConfigFinder;
import no.goodtech.dashboard.config.ui.DashboardConfigStub;
import no.goodtech.dashboard.ui.DashboardPanel;
import no.goodtech.dashboard.ui.Texts;
import no.goodtech.vaadin.buttons.RefreshButton;
import no.goodtech.vaadin.lists.v7.ListSelectionAwareComponentDisabler;
import no.goodtech.vaadin.main.SimpleInputBox;
import no.goodtech.vaadin.security.AccessFunction;
import no.goodtech.vaadin.tabs.IMenuView;
import no.goodtech.vaadin.ui.SimpleCrudAdminPanel;

/**
 * Panel for controlling dashboards
 */
@UIScope
@SpringView(name = DashboardAdminView.VIEW_ID)
public class DashboardAdminView extends SimpleCrudAdminPanel<DashboardConfig, DashboardConfigStub, DashboardConfigFinder>
		implements IMenuView {

	public static final String VIEW_ID = "DashboardAdminView";
	public static final String VIEW_NAME = Texts.get("dashboardAdminView.viewName");
	private static final String ACCESS_VIEW = "dashboardPanel.view";
	private static final String ACCESS_EDIT = "dashboardPanel.edit";
	private static final String REFRESH_DASHBOARDS_CAPTION = Texts.get("dashboardAdminView.refreshDashboardsButton.caption");
	private static final String PREVIEW_CAPTION = Texts.get("dashboardAdminView.previewButton.caption");

	public DashboardAdminView() {
		super(new DashboardConfigFilterPanel(), new DashboardConfigGrid());
		createPreviewButton();
		createRefreshDashboardsButton();
	}
	
	public void enter(ViewChangeEvent event) {
		super.enter(event);
	}

	private void createRefreshDashboardsButton() {
		final RefreshButton refreshButton = new RefreshButton(new RefreshButton.IRefreshListener() {
			@Override
			public void refreshClicked() {
				Globals.getDashboardSeriesManager().refresh();
				for (Long dashboardPk : new DashboardConfigFinder().listPks()) {
					Globals.getDashboardChangedAdaptor().publish(String.valueOf(dashboardPk));
				}
			}
		});
		refreshButton.setCaption(REFRESH_DASHBOARDS_CAPTION);
		buttonLayout.addButton(refreshButton);
	}

	private void createPreviewButton() {
		final Button button = new Button(PREVIEW_CAPTION, VaadinIcons.LINE_CHART);
		button.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent clickEvent) {
				final DashboardConfigStub dashboardConfig = getSelectedObject();
				if (dashboardConfig != null) {
					final DashboardPanel content = new DashboardPanel(dashboardConfig.getPk(), dashboardConfig.getRefreshIntervalInSeconds() != null);
					content.refresh();
					content.setSizeFull();
					Window window = new Window(null, content);
					window.setModal(true);
					window.setWidth("90%");
					window.setHeight("90%");
					UI.getCurrent().addWindow(window);
				}
			}
		});
		ListSelectionAwareComponentDisabler.control(table, true, button);
		button.setEnabled(false);
		buttonLayout.addButton(button);
	}

	@Override
	protected SimpleInputBox.IinputBoxContent createDetailForm(DashboardConfigStub dashboardConfig) {
		return new DashboardConfigFormVaadin7(dashboardConfig);
	}

	@Override
	protected DashboardConfigStub createEntity() {
		return new DashboardConfig();
	}

	protected AccessFunction getAccessFunctionView() {
		return new AccessFunction(ACCESS_VIEW, Texts.get("accessFunction." + ACCESS_VIEW));
	}

	protected AccessFunction getAccessFunctionEdit() {
		return new AccessFunction(ACCESS_EDIT, Texts.get("accessFunction." + ACCESS_EDIT));
	}

	@Override
	public String getViewName() {
		return VIEW_NAME;
	}

	@Override
	protected Integer getDetailsPopupWidth() {
		return 50;
	}
}
