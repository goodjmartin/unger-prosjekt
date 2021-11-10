package no.goodtech.dashboard.config.ui;

import no.goodtech.dashboard.config.fetcher.FetcherConfig;
import no.goodtech.persistence.entity.AbstractSimpleEntityImpl;
import no.goodtech.vaadin.lists.ICopyable;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
//@Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
//TODO: Digital (step curves), see signal logger chart
//TODO: Tooltip formatting, including short names
public class SeriesConfig extends AbstractSimpleEntityImpl implements ICopyable<SeriesConfig> {


	public enum LimitType {
		MIN, TARGET, MAX;
	}

	@ManyToOne
	@NotNull
	protected ChartConfig panelConfig;

	@ManyToOne
	protected AxisConfig axisConfig;

	@ManyToOne
	protected FetcherConfig fetcherConfig;

	@ManyToOne
	private SeriesConfig connectedSeriesConfig;

	private Integer sourceType;

	@NotNull
	protected String id;

	protected String name;

	protected Boolean showMarker;

	private Boolean plotband;

	private String color;

	@Min(1)
	@Max(255)
	private Integer lineWidth = 2;

	private Double fixedValue;

	public SeriesConfig() {
	}

	@Override
	public SeriesConfig copy() {
		SeriesConfig config = new SeriesConfig();
		config.panelConfig = panelConfig;
		config.axisConfig = axisConfig;
		config.fetcherConfig = fetcherConfig;
		config.id = id;
		config.name = name;
		config.showMarker = showMarker;
		config.plotband = plotband;
		config.color = color;
		config.lineWidth = lineWidth;
		config.sourceType = sourceType;
		return config;
	}

	public SeriesConfig clone() {
		SeriesConfig clone = copy();
		cloneFieldsTo(clone);
		return clone;
	}

	public SeriesConfig(ChartConfig chartConfig) {
		chartConfig.addSeriesConfig(this);
	}

	public SeriesConfig(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		if (name == null)
			return id;
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AxisConfig getAxisConfig() {
		return axisConfig;
	}

	public void setAxisConfig(AxisConfig axisConfig) {
		this.axisConfig = axisConfig;
	}

	public boolean isShowMarker() {
		return showMarker != null && showMarker;
	}

	public void setShowMarker(boolean showMarker) {
		this.showMarker = showMarker;
	}

	public ChartConfig getPanelConfig() {
		return panelConfig;
	}

	public void setPanelConfig(ChartConfig panelConfig) {
		this.panelConfig = panelConfig;
	}

	public FetcherConfig getFetcherConfig() {
		return fetcherConfig;
	}

	public void setFetcherConfig(FetcherConfig fetcherConfig) {
		this.fetcherConfig = fetcherConfig;
	}

	public boolean isPlotband() {
		return plotband != null && plotband;
	}

	/**
	 * If set to true, change in value will be marked with different background color
	 * Series will not be visible unless the value changes
	 */
	public void setPlotband(boolean plotband) {
		this.plotband = plotband;
	}

	public SeriesConfig getConnectedSeriesConfig() {
		return connectedSeriesConfig;
	}

	public void setConnectedSeriesConfig(SeriesConfig connectedSeriesConfig) {
		this.connectedSeriesConfig = connectedSeriesConfig;
	}

	/**
	 * @return RGB HEX value of color or name of one of the predefined CSS colors (https://www.w3schools.com/cssref/css_colors.asp)
	 * If null, color will be chosen automatically
	 */
	public String getColor() {
		return color;
	}

	/**
	 * Provide color of this series
	 * @param color RGB HEX value of color,
	 * or name of one of the predefined CSS colors (https://www.w3schools.com/cssref/css_colors.asp)
	 * or null if you want color to be chosen automatically
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * @return type of source data or null if not relevant
	 */
	public Integer getSourceType() {
		return sourceType;
	}

	/**
	 * Provide type of source data behind this series
	 * @param sourceType type of source data
	 */
	public void setSourceType(Integer sourceType) {
		this.sourceType = sourceType;
	}

	/**
	 * @return fixed value (if any). May be null
	 */
	public Double getFixedValue() {
		return fixedValue;
	}

	/**
	 * @param fixedValue a fixed value
	 */
	public void setFixedValue(Double fixedValue) {
		this.fixedValue = fixedValue;
	}

	public Integer getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(Integer lineWidth) {
		this.lineWidth = lineWidth;
	}

	@Override
	protected String toString(Object key) {
		if (name != null) {
			return String.format("%s (%s)", name, id);
		}
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!obj.getClass().equals(this.getClass())) {
			return false;
		}
		//check for equality based on ID (for auto-generated series without pk)
		SeriesConfig other = (SeriesConfig)obj;
		String thisId = this.getId();
		String otherId = other.getId();
		if (thisId == null || otherId == null || !thisId.equals(otherId)) {
			return false;
		}

		//check for equality based on panel for series with same ID
		if (this.panelConfig == null && other.panelConfig == null) {
			return true; //both are not assigned to any panel
		} else if (this.panelConfig == null || other.panelConfig == null) {
			return false;
		}
		return this.panelConfig.equals(other.panelConfig);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getPk()).append(id).toHashCode();
	}
}
