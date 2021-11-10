package no.goodtech.dashboard.ui;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ItemCaptionGenerator;
import no.goodtech.dashboard.config.ui.DashboardConfigFinder;
import no.goodtech.vaadin.frontpage.model.FrontPageCardComponent;
import no.goodtech.vaadin.frontpage.ui.FrontPageCardInput;
import no.goodtech.vaadin.frontpage.model.IFrontPageCardComponent;
import no.goodtech.vaadin.login.User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Front-page component for dashboards
 */
@FrontPageCardComponent
public class DashboardPanelFrontPageComponent extends DashboardPanel implements IFrontPageCardComponent {


	public DashboardPanelFrontPageComponent() {
		super();
	}

	@Override
	public FrontPageCardInput getInputPanel(){
		FrontPageCardInput input = new FrontPageCardInput();
		ComboBox<String> configIds = new ComboBox<>(getCaption("id"));
		configIds.setId("id");
		configIds.setEmptySelectionAllowed(false);
		configIds.setItems(new DashboardConfigFinder().listIds());
		input.addComponent(configIds);

		ComboBox<Boolean> live = new ComboBox<>(getCaption("live"));
		live.setId("live");
		live.setEmptySelectionAllowed(false);
		live.setItems(Arrays.asList(true, false));
		live.setItemCaptionGenerator((ItemCaptionGenerator<Boolean>) item -> item ? getCaption("true") : getCaption("false"));
		input.addComponent(live);

		return input;
	}

	@Override
	public Map<String, String> getArguments() {
		Map<String, String> args = new HashMap<>();
		args.put("id", id);
		args.put("live", String.valueOf(live));
		return args;
	}

	@Override
	public void setArguments(Map<String, String> arg) {
		if (arg != null) {
			id = arg.get("id");
			live = Boolean.parseBoolean(arg.get("live"));
		}
	}

	@Override
	public String getViewName() {
		return getCaption("viewName");
	}

	@Override
	public String getParentViewId() {
		return DashboardView.VIEW_ID;
	}

	@Override
	public boolean isAuthorized(User user, String arg) {
		return true;
	}
}
