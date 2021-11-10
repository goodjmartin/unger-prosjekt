package no.goodtech.dashboard.config.ui;

import no.goodtech.persistence.jpa.AbstractFinder;
import no.goodtech.vaadin.category.Category;
import no.goodtech.vaadin.search.FilterPanel;

import java.util.List;

public class DashboardConfigFinder extends AbstractFinder<DashboardConfig, DashboardConfigStub, DashboardConfigFinder>
implements FilterPanel.IMaxRowsAware {

	public DashboardConfigFinder() {
		super("select c from DashboardConfig c", "c");
	}

	@SuppressWarnings("unchecked")
	public List<SeriesConfig> listSeriesConfigs() {
		setSelectFromClause("select sc from SeriesConfig sc", "sc");
		addJoin("sc.panelConfig.dashboardConfig c");
		addSortOrder("sc.id", true);
		return getRepository().list(this, SeriesConfig.class);
	}

	public DashboardConfigFinder setPanelConfig(PanelConfig panelConfig) {
		if (panelConfig != null) {
			addEqualFilter(prefixWithAlias("panelConfig.pk"), panelConfig.getPk());
		}
		return this;
	}

	public DashboardConfigFinder setSeriesConfigId(String seriesConfigId) {
		addEqualFilter(prefixWithAlias("panelConfig.seriesConfig.id"), seriesConfigId);
		return this;
	}

	public DashboardConfigFinder setArea(Category area) {
		if (area != null) {
			addEqualFilter(prefixWithAlias("area.pk"), area.getPk());
		}
		return this;
	}

	public DashboardConfigFinder orderById() {
		addSortOrder(prefixWithAlias("id"), true);
		return this;
	}

	public List<String> listIds(){
		setSelectFromClause("select distinct new java.lang.String(c.id) from DashboardConfig c", "c");
		return super.list(String.class);
	}
}
