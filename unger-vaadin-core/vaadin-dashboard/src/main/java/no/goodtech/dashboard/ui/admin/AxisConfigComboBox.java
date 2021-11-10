package no.goodtech.dashboard.ui.admin;

import no.goodtech.dashboard.config.ui.AxisConfig;
import no.goodtech.vaadin.lists.v7.MessyComboBox;

import java.util.List;

public class AxisConfigComboBox extends MessyComboBox<AxisConfig> {

	private final List<AxisConfig> axisConfigs;

	public AxisConfigComboBox(String caption, List<AxisConfig> axisConfigs) {
		super(caption);
		this.axisConfigs = axisConfigs;
	}

	@Override
	protected String getId(AxisConfig item) {
		return String.valueOf(item.getPk());
	}

	@Override
	protected String getName(AxisConfig item) {
		return item.getName();
	}

	public void refresh() {
		super.refresh(axisConfigs);
	}
}
