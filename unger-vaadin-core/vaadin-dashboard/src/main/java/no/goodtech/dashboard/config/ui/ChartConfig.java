package no.goodtech.dashboard.config.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
//TODO: Aggregation functions shown in upper right corner for a specific series:
//TODO:  1) % of value count inside limits, green/yellow/red text color
//TODO: @Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
public class ChartConfig extends PanelConfig {

	public enum XaxisType {
		TIME, XY, CATEGORY;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ChartConfig.class);

	protected Boolean alignTicks = false;

	@OneToMany(cascade= CascadeType.ALL, mappedBy="panelConfig", orphanRemoval=true)
	private Set<AxisConfig> yAxes = new LinkedHashSet<>();

	@OneToMany(cascade= CascadeType.ALL, mappedBy="panelConfig", orphanRemoval=true)
	private Set<SeriesConfig> seriesConfigs = new HashSet<>();

	@ManyToOne
	private SeriesConfig runningAverageSeries;

	@Min(2)
	private Integer runningAverageMaxValueCount;

	@Min(0)
	private Integer runningAveragePrecision;


	public ChartConfig() {
		this(null);
	}

	public ChartConfig(DashboardConfigStub dashboardConfig) {
		this.dashboardConfig = (DashboardConfig) dashboardConfig;
	}

	public Set<AxisConfig> getyAxes() {
		return yAxes;
	}

	public void setyAxes(Set<AxisConfig> yAxes) {
		this.yAxes = yAxes;
	}

	public void addyAxis(AxisConfig yaxis) {
		yaxis.setPanelConfig(this);
		yAxes.add(yaxis);
	}

	public Set<SeriesConfig> getSeriesConfigs() {
		return seriesConfigs;
	}

	public void setSeriesConfigs(Set<SeriesConfig> seriesConfigs) {
		this.seriesConfigs.clear();
		for (SeriesConfig seriesConfig : seriesConfigs) {
			addSeriesConfig(seriesConfig);
		}
	}

	/**
	 * @param id ID of the series config you want
	 * @return series config with given ID or null if not found
	 */
	public SeriesConfig findSeriesConfig(String id) {
		for (SeriesConfig seriesConfig : seriesConfigs) {
			if (seriesConfig.getId().equals(id)) {
				return seriesConfig;
			}
		}
		return null;
	}

	public void addSeriesConfig(SeriesConfig seriesConfig) {
		seriesConfigs.add(seriesConfig);
		seriesConfig.setPanelConfig(this);
	}

	public Boolean getAlignTicks() {
		return alignTicks;
	}

	public boolean isAlignTicks() {
		return alignTicks != null && alignTicks;
	}

	public void setAlignTicks(Boolean alignTicks) {
		this.alignTicks = alignTicks;
	}

	@Override
	public void lazyLoad() {
		seriesConfigs.size();
		yAxes.size();
	}

	/**
	 * @return the series we calculate running average for, if any. Null if no calculation of running average
	 * @see #getRunningAverageMaxValueCount()
	 */
	public SeriesConfig getRunningAverageSeries() {
		return runningAverageSeries;
	}

	/**
	 * Provide which series we want to calculate running average for
	 * @param runningAverageSeries the series or null if we don't need calculation of running average
	 * @see #setRunningAverageMaxValueCount(Integer)
	 */
	public void setRunningAverageSeries(SeriesConfig runningAverageSeries) {
		this.runningAverageSeries = runningAverageSeries;
	}

	/**
	 * @return how many values we want to average, if we want to calculate running average
	 */
	public Integer getRunningAverageMaxValueCount() {
		return runningAverageMaxValueCount;
	}

	public void setRunningAverageMaxValueCount(Integer runningAverageMaxValueCount) {
		this.runningAverageMaxValueCount = runningAverageMaxValueCount;
	}

	/**
	 * @return num decimals of running average
	 */
	public Integer getRunningAveragePrecision() {
		return runningAveragePrecision;
	}

	public void setRunningAveragePrecision(Integer runningAveragePrecision) {
		this.runningAveragePrecision = runningAveragePrecision;
	}

	public PanelConfig copy() {
		ChartConfig copy = new ChartConfig(dashboardConfig);
		copy.title = title;
		copy.alignTicks = alignTicks;
		copy.runningAverageSeries = runningAverageSeries;
		copy.runningAverageMaxValueCount = runningAverageMaxValueCount;
		copy.runningAveragePrecision = runningAveragePrecision;
		copy.setStartRow(getStartRow());
		copy.setEndRow(getEndRow());
		copy.setStartColumn(getStartColumn());
		copy.setEndColumn(getEndColumn());
		copy.setPeriodLengthInMinutes(getPeriodLengthInMinutes());
		copy.setTimeShift(isTimeShift());
		for (SeriesConfig seriesConfig : seriesConfigs) {
			copy.addSeriesConfig(seriesConfig.copy());
		}
		for (AxisConfig axisConfig : yAxes) {
			copy.addyAxis(axisConfig.copy());
		}
		return copy;
	}
}
