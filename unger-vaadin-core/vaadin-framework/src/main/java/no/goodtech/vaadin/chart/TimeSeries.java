package no.goodtech.vaadin.chart;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vaadin.addon.charts.model.style.Color;

/**
 * Rådata for en serie, dvs. alle punkter som vises i en graf
 */
public class TimeSeries implements IColorable {
	
	public enum PlotType {
		LINE, SPLINE, SCATTER, AREA, ICON
	}
	
	private final String id;
	private final String name;
	private List<SampleValue> values = new ArrayList<SampleValue>();
	private boolean digital;
	private Double minValue = null; 
	private Double maxValue = null;
	private Double avgValue = null;
	private Color color;
	private long longestGap = 0;
	private Date lastSampleTime = null, longestGapEnd = null;
	private PlotType plotType;

	public TimeSeries(String id, Color color) {
		this(id, id, color);
	}

	public TimeSeries(String id, String name, Color color) {
		this.id = id;
		this.name = name;
		this.color = color;
	}

	/**
	 * @return name that is shown in legends and tooltips
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return unique ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return alle verdier i serien, sortert på stigende tidspunkt
	 */
	public List<SampleValue> getValues() {
		return values;
	}

	/**
	 * @return number of points
	 */
	public int getNumValues() {
		return values.size();
	}

	/**
	 * @see #isDigital()
	 */
	public boolean isDigital() {
		return digital;
	}
	
	/**
	 * @return verdien er digital, dvs. at sist registrerte verdi gjelder helt til verdien endrer seg.
	 * Brukes til brytere f.eks. motor av/på eller for settpunkter
	 */
	public boolean getDigital() {
		return digital;
	}
	
	public void setDigital(boolean digital) {
		this.digital = digital;
	}

	/**
	 * @return lowest value. Null if no values
	 */
	public Double getMinValue() {
		return minValue;
	}
	
	/**
	 * @return highest value. Null if no values
	 */
	public Double getMaxValue() {
		return maxValue;
	}

	/**
	 * @return average value. Null if no values
	 */
	public Double getAvgValue() {
		return avgValue;
	}
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @return longest time gap in millis between sample values
	 */
	public long getLongestGap() {
		return longestGap;
	}
	
	
	/**
	 * @return time for first value after longest gap
	 */
	public Date getLongestGapEnd() {
		return longestGapEnd;
	}

	public void add(Date timestamp, double value) {
		values.add(new SampleValue(timestamp, value));

		if (minValue == null || value < minValue)
			minValue = value;
		
		if (maxValue == null || value > maxValue)
			maxValue = value;
		
		//Copied from http://stackoverflow.com/questions/1930454/what-is-a-good-solution-for-calculating-an-average-where-the-sum-of-all-values-e
		if (avgValue == null)
			avgValue = value;
		else
			avgValue += (value - avgValue) / values.size();
		
		if (lastSampleTime != null) {
			final long time = timestamp.getTime();
			long gap = time - lastSampleTime.getTime();
			if (gap > longestGap) {
				longestGap = gap;
				longestGapEnd = timestamp;
			}
		}
		lastSampleTime = timestamp;
	}

	/**
	 * @return how to plot this series
	 */
	public PlotType getPlotType() {
		return plotType;
	}

	/**
	 * @see #getPlotType()
	 */
	public void setPlotType(PlotType plotType) {
		this.plotType = plotType;
	}
}
