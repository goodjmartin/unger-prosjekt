package no.goodtech.dashboard.config.ui;

import no.goodtech.persistence.entity.AbstractEntityImpl;
import no.goodtech.vaadin.category.Category;
import no.goodtech.vaadin.lists.ICopyable;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Configuration for one dashboardConfig view / "page"
 */
@Entity
//TODO: @Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
public class DashboardConfig extends AbstractEntityImpl<DashboardConfig>
		implements DashboardConfigStub, ICopyable<DashboardConfig> {

	@NotEmpty
	private String id;

	private String title;

	@Min(1)
	private Integer refreshIntervalInSeconds;

	@NotNull
	@Min(1)
	private int numRows = 1;

	@NotNull
	@Min(1)
	private int numColumns = 1;

	@ManyToOne
	private Category area;

	@OneToMany(cascade= CascadeType.ALL, mappedBy="dashboardConfig", orphanRemoval=true)
	//TODO: @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<PanelConfig> panels = new HashSet<>();


	public DashboardConfig() {
		this(null);
	}

	public DashboardConfig(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getRefreshIntervalInSeconds() {
		return refreshIntervalInSeconds;
	}

	/**
	 * Provide refresh rate
	 * @param refreshIntervalInSeconds if null, switch off auto refresh
	 */
	public void setRefreshIntervalInSeconds(Integer refreshIntervalInSeconds) {
		this.refreshIntervalInSeconds = refreshIntervalInSeconds;
	}

	public int getNumRows() {
		return numRows;
	}

	public void setNumRows(int numRows) {
		this.numRows = numRows;
	}

	public int getNumColumns() {
		return numColumns;
	}

	public void setNumColumns(int numColumns) {
		this.numColumns = numColumns;
	}

	public Category getArea() {
		return area;
	}

	public void setArea(Category area) {
		this.area = area;
	}

	public Set<PanelConfig> getPanels() {
		return panels;
	}

	public void setPanels(Set<PanelConfig> panels) {
		this.panels.clear();
		for (PanelConfig panelConfig : panels) {
			addPanelConfig(panelConfig);
		}
	}

	public void setPanels(Collection<PanelConfig> panels) {
		setPanels(new HashSet<>(panels));
	}

	public void addPanelConfig(PanelConfig panelConfig) {
		panels.add(panelConfig);
		panelConfig.setDashboardConfig(this);
	}

	@Override
	public void lazyLoad() {
		for (PanelConfig panel : panels) {
			panel.lazyLoad();
		}
	}

	public List<SeriesConfig> getSeriesConfigs() {
		List<SeriesConfig> seriesConfigs = new ArrayList<>();
		for (PanelConfig panelConfig : panels) {
			if (panelConfig instanceof ChartConfig) {
				ChartConfig chartConfig = (ChartConfig) panelConfig;
				seriesConfigs.addAll(chartConfig.getSeriesConfigs());
			}
		}
		return seriesConfigs;
	}

	public String formatCoordinates() {
		return String.format("x=1-%d, y=1-%d", getNumColumns(), getNumRows());
	}

	public int getMinimumNumColumnsNeeded() {
		int minNumColumnsNeeded = 1;
		for (PanelConfig panelConfig : panels) {
			if (panelConfig.getEndColumn() > minNumColumnsNeeded) {
				minNumColumnsNeeded = panelConfig.getEndColumn();
			}
		}
		return minNumColumnsNeeded;
	}

	public int getMinimumNumRowsNeeded() {
		int minNumRowsNeeded = 1;
		for (PanelConfig panelConfig : panels) {
			if (panelConfig.getEndRow() > minNumRowsNeeded) {
				minNumRowsNeeded = panelConfig.getEndRow();
			}
		}
		return minNumRowsNeeded;
	}

	@Override
	public DashboardConfig copy() {
		DashboardConfig copy = new DashboardConfig(id);
		copy.title = title;
		copy.numColumns = numColumns;
		copy.numRows = numRows;
		copy.refreshIntervalInSeconds = refreshIntervalInSeconds;
		copy.area = area;
		for (PanelConfig panelConfig : panels) {
			copy.addPanelConfig(panelConfig.copy());
		}
		return copy;
	}
}
