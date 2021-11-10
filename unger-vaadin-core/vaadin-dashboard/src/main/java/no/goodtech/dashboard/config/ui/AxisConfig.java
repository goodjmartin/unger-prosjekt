package no.goodtech.dashboard.config.ui;

import no.goodtech.persistence.entity.AbstractSimpleEntityImpl;
import no.goodtech.vaadin.lists.ICopyable;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
//TODO: @Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
public class AxisConfig extends AbstractSimpleEntityImpl implements ICopyable<AxisConfig> {

	@ManyToOne
	@NotNull
	protected ChartConfig panelConfig;

	@NotEmpty
	protected String name;

	protected Boolean opposite = false, hideAlternateGrid = false;

	protected Double minValue, maxValue, tickInterval, lineWidth, tickWidth;

	protected Integer gridLineWidth;


	public AxisConfig() {
		this(null, null);
	}

	public AxisConfig(ChartConfig chartConfig) {
		this(null, chartConfig);
	}

	public AxisConfig(String name) {
		this(name, null);
	}

	public AxisConfig(String name, ChartConfig chartConfig) {
		this.name = name;
		this.panelConfig = chartConfig;
	}

	public ChartConfig getPanelConfig() {
		return panelConfig;
	}

	public void setPanelConfig(ChartConfig panelConfig) {
		this.panelConfig = panelConfig;
	}

	public Boolean getOpposite() {
		return opposite;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isOpposite() {
		return opposite != null && opposite;
	}

	public void setOpposite(boolean opposite) {
		this.opposite = opposite;
	}

	public boolean isHideAlternateGrid() {
		return hideAlternateGrid != null && hideAlternateGrid;
	}

	public void setHideAlternateGrid(boolean hideAlternateGrid) {
		this.hideAlternateGrid = hideAlternateGrid;
	}

	public Double getMinValue() {
		return minValue;
	}

	public void setMinValue(Double minValue) {
		this.minValue = minValue;
	}

	public Double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}

	public Double getTickInterval() {
		return tickInterval;
	}

	public void setTickInterval(Double tickInterval) {
		this.tickInterval = tickInterval;
	}

	public Double getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(Double lineWidth) {
		this.lineWidth = lineWidth;
	}

	public Double getTickWidth() {
		return tickWidth;
	}

	public void setTickWidth(Double tickWidth) {
		this.tickWidth = tickWidth;
	}

	public Integer getGridLineWidth() {
		return gridLineWidth;
	}

	public void setGridLineWidth(Integer gridLineWidth) {
		this.gridLineWidth = gridLineWidth;
	}

	public AxisConfig copy() {
		AxisConfig copy = new AxisConfig();
		copy.panelConfig = panelConfig;
		copy.opposite = opposite;
		copy.name = name;
		copy.gridLineWidth = gridLineWidth;
		copy.lineWidth = lineWidth;
		copy.hideAlternateGrid = hideAlternateGrid;
		copy.minValue = minValue;
		copy.maxValue = maxValue;
		copy.tickInterval = tickInterval;
		copy.tickWidth = tickWidth;
		return copy;
	}

	public AxisConfig clone() {
		AxisConfig clone = copy();
		cloneFieldsTo(clone);
		return clone;
	}
}
