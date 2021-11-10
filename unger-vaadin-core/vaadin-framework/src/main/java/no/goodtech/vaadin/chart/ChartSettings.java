package no.goodtech.vaadin.chart;

import java.util.HashMap;
import java.util.Map;

import javax.management.timer.Timer;

import no.goodtech.vaadin.chart.TimeSeries.PlotType;


/**
 * User-specific settings for UI
 */
public class ChartSettings {

	private YaxisRanges yaxisRanges = new YaxisRanges();
	protected boolean showStandardLegend = true;
	protected boolean showTableLegend = false;
	protected boolean showTooltip = false;
	protected Integer reductionLimit = 1000;
	protected Integer reductionFactor = 200;
	protected PlotType defaultPlotType = PlotType.LINE;
	protected PlotType defaultPlotTypeForDigitalSeries = PlotType.AREA;
	protected Map<String, PlotType> seriesPlotTypes = new HashMap<String, TimeSeries.PlotType>();
	protected long timePeriodShortStep = Timer.ONE_HOUR;
	protected long timePeriodLongStep = Timer.ONE_DAY;
	
	public YaxisRanges getYaxisRanges() {
		return yaxisRanges;
	}
	
	public void setYaxisRanges(YaxisRanges yaxisRanges) {
		this.yaxisRanges = yaxisRanges;
	}
	
	public boolean isShowStandardLegend() {
		return showStandardLegend;
	}
	
	public void setShowStandardLegend(boolean showStandardLegend) {
		this.showStandardLegend = showStandardLegend;
	}
	
	public boolean isShowTableLegend() {
		return showTableLegend;
	}
	
	public void setShowTableLegend(boolean showTableLegend) {
		this.showTableLegend = showTableLegend;
	}

	public Integer getReductionLimit() {
		return reductionLimit;
	}

	public void setReductionLimit(Integer reductionLimit) {
		this.reductionLimit = reductionLimit;
	}

	public Integer getReductionFactor() {
		return reductionFactor;
	}

	public void setReductionFactor(Integer reductionFactor) {
		this.reductionFactor = reductionFactor;
	}

	public Map<String, PlotType> getSeriesPlotTypes() {
		return seriesPlotTypes;
	}

	public void setPlotType(String series, PlotType plotType) {
		seriesPlotTypes.put(series, plotType);
	}
	
	public PlotType getPlotType(String series) {
		return seriesPlotTypes.get(series);
	}

	public PlotType getDefaultPlotType() {
		return defaultPlotType;
	}

	public void setDefaultPlotType(PlotType defaultPlotType) {
		this.defaultPlotType = defaultPlotType;
	}
	
	public PlotType getDefaultPlotTypeForDigitalSeries() {
		return defaultPlotTypeForDigitalSeries;
	}

	public void setDefaultPlotTypeForDigitalSeries(PlotType defaultPlotTypeForDigitalSeries) {
		this.defaultPlotTypeForDigitalSeries = defaultPlotTypeForDigitalSeries;
	}

	public boolean isShowTooltip() {
		return showTooltip;
	}

	public void setShowTooltip(boolean showTooltip) {
		this.showTooltip = showTooltip;
	}

	public void applyToChartData(TimeSeriesChartData chartData) {
		chartData.setPlotType(defaultPlotType);
		for (TimeSeries series : chartData.getSeries()) {
			if (series.isDigital())
				series.setPlotType(defaultPlotTypeForDigitalSeries);
			else 
				series.setPlotType(defaultPlotType);
		}
		chartData.setPlotTypes(seriesPlotTypes);
		chartData.setYaxisRanges(yaxisRanges);
	}

	public long getTimePeriodShortStep() {
		return timePeriodShortStep;
	}

	public void setTimePeriodShortStep(long timePeriodShortStep) {
		this.timePeriodShortStep = timePeriodShortStep;
	}

	public long getTimePeriodLongStep() {
		return timePeriodLongStep;
	}

	public void setTimePeriodLongStep(long timePeriodLongStep) {
		this.timePeriodLongStep = timePeriodLongStep;
	}	
}
