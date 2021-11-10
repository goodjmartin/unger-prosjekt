package no.goodtech.vaadin.chart;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import no.goodtech.vaadin.chart.TimeSeries.PlotType;
import no.goodtech.vaadin.chart.YaxisRanges.MinMaxValue;

/**
 * Rådata til et diagram for å vise tidsserie-data
 * Kan inneholde data for flere serier og flere y-akser 
 * @author oystein
 */
public class TimeSeriesChartData {

	long minDate = Long.MAX_VALUE;
	long maxDate = Long.MIN_VALUE;
	
	private Map<String, Yaxis> yaxes  = new LinkedHashMap<String, Yaxis>();
	private final VaadinColorProvider colorProvider = new VaadinColorProvider();
	
	
	/**
	 * Legg til en verdi som skal vises i en diagram
	 * @param value verdien
	 * @param timestamp tidspunktet for når verdien ble registrert
	 * @param seriesId ID til serien (grafen/linja) verdien hører til
	 * @param seriesName visningsnavn til serien (grafen/linja) verdien hører til
	 * @param yAxisName navn på y-aksen verdien hører til
	 */
	public void addValue(Number value, Date timestamp, String seriesId, String seriesName, String yAxisName, boolean digital) {
		if (value != null) {
			Yaxis axis = getYaxis(yAxisName);
			
			TimeSeries currentSeries = axis.series.get(seriesId);
			if (currentSeries == null) {
				//opprett serie hvis den ikke finnes fra før
				currentSeries = new TimeSeries(seriesId, seriesName, colorProvider.getNextColor());
				currentSeries.setDigital(digital);
				axis.series.put(seriesId, currentSeries);
			}

			currentSeries.add(timestamp, value.doubleValue());
	
			long sampleDate = timestamp.getTime();
			if (minDate > sampleDate)
				minDate = sampleDate;
			if (maxDate < sampleDate)
				maxDate = sampleDate;			
		}
		
	}

	/**
	 * Legg til en verdi som skal vises i en diagram
	 * @param value verdien
	 * @param timestamp tidspunktet for når verdien ble registrert
	 * @param seriesId ID til serien (grafen/linja) verdien hører til
	 * @param yAxisName navn på y-aksen verdien hører til
	 */
	public void addValue(Number value, Date timestamp, String seriesId, String yAxisName, boolean digital) {
		addValue(value, timestamp, seriesId, seriesId, yAxisName, digital);
	}
	
	protected Yaxis getYaxis(String yAxisName) {
		Yaxis axis = yaxes.get(yAxisName);
		if (axis == null) {
			//opprett y-akse hvis den ikke finnes fra før
			axis = new Yaxis();
			yaxes.put(yAxisName, axis);
		}
		return axis;
	}

	/**
	 * @return tidligste tidspunkt i diagrammet
	 */
	public Date getMinDate() {
		return new Date(minDate);
	}
	
	/**
	 * @return seneste tidspunkt i diagrammet
	 */
	public Date getMaxDate() {
		return new Date(maxDate);
	}

	/**
	 * @return alle y-akser gruppert på navn
	 */
	public Map<String, Yaxis> getYaxes() {
		return yaxes;
	}
	
	/**
	 * Angi grenser for en spesifikk y-akse
	 * @param axisName navn på aksen
	 * @param min lav grense
	 * @param max høy grense
	 */
	public void setYaxisRange(String axisName, Double min, Double max) {
		final Yaxis yaxis = getYaxis(axisName);
		yaxis.setMinLimit(min);
		yaxis.setMaxLimit(max);
	}
	
	/**
	 * Angi grenser for en eller flere y-akser
	 * @param yaxisRanges grenser
	 */
	public void setYaxisRanges(YaxisRanges yaxisRanges) {
		for (String yAxis : getYaxes().keySet()) {
			MinMaxValue userSpecificValueRange = yaxisRanges.get(yAxis);
			if (userSpecificValueRange != null)
				setYaxisRange(yAxis, userSpecificValueRange.getMinValue(), userSpecificValueRange.getMaxValue());
		}
	}
	
	/**
	 * Set how to plot specific series
	 * @param plotTypes plot type for each series that should be drawn with a different plot type
	 */
	public void setPlotTypes(Map<String, PlotType> plotTypes) {
		if (plotTypes != null) {
			for (Entry<String, PlotType> entry : plotTypes.entrySet()) {
				final Map<String, TimeSeries> seriesByName = getSeriesById();
				final TimeSeries timeSeries = seriesByName.get(entry.getKey());
				if (timeSeries != null)
					timeSeries.setPlotType(entry.getValue());
			}
		}
	}
	
	/**
	 * Use same plot type for all series
	 * @param plotType plot type to user
	 */
	public void setPlotType(PlotType plotType) {
		for (TimeSeries series : getSeries())
			series.setPlotType(plotType);
	}

	public List<TimeSeries> getSeries() {
		List<TimeSeries> result = new ArrayList<TimeSeries>();
		for (Yaxis axis : yaxes.values())
			result.addAll(axis.getSeries().values());
		return result;
	}
	
	/**
	 * @return alle serier som bruker denne aksen, gruppert på ID
	 */
	public Map<String, TimeSeries> getSeriesById() {
		Map<String, TimeSeries> result = new HashMap<String, TimeSeries>();
		for (Yaxis axis : yaxes.values())
			result.putAll(axis.getSeries());
		return result;
	}
	
}
