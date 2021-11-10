package no.goodtech.dashboard.ui.admin;

import no.goodtech.dashboard.config.ui.DashboardConfig;
import no.goodtech.dashboard.config.ui.DashboardConfigFinder;
import no.goodtech.dashboard.ui.Texts;
import no.goodtech.vaadin.UrlUtils;
import no.goodtech.vaadin.category.ui.CategoryComboBox;
import no.goodtech.vaadin.search.FilterPanel;

public class DashboardConfigFilterPanel extends FilterPanel<DashboardConfigFinder> {

	private final CategoryComboBox areaComboBox;

	public DashboardConfigFilterPanel() {
		areaComboBox = new CategoryComboBox(Texts.get("dashboardConfig.filter.area.caption"), DashboardConfig.class);
		areaComboBox.setNullSelectionAllowed(true);
		addComponents(areaComboBox);
	}

	@Override
	public DashboardConfigFinder getFinder() {
		final DashboardConfigFinder finder = new DashboardConfigFinder().orderById();
		if (areaComboBox.getCurrentCategory() != null) {
			finder.setArea(areaComboBox.getCurrentCategory());
		}
		return finder;
	}

	public void refresh(String url) {
		super.refresh(url);
		areaComboBox.refresh();
		if (url != null) {
			final UrlUtils urlUtils = new UrlUtils(url);

			final String area = urlUtils.getParameter("area");
			if (area != null)
				areaComboBox.select(area);

		}
	}
}
