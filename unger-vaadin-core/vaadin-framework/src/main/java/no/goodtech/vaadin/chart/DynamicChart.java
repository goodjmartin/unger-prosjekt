package no.goodtech.vaadin.chart;

import com.vaadin.ui.Component;

/**
 * En graf som kan oppfriskes og hvor man kan begrense y-aksene
 * @author oystein
 *
 */
public interface DynamicChart extends Component {
	
	/**
	 * Tegn grafen på nytt
	 */
	void refresh();
	
	/**
	 * Angi eller ta bort øvre og nedre grense på y-aksen 
	 * @param ranges område på y-aksen
	 */
	void setYaxisRanges(YaxisRanges ranges);

	/**
	 * @param showLegend true = show legend, false = do not show legend
	 */
	void showLegend(boolean showLegend);
	
	/**
	 * @param showTooltip true = show tooltip, false = do not show tooltip on each point
	 */
	void setShowTooltip(boolean showTooltip);
	
	/**
	 * Extend time period either in one or both directions. If you want to "pan", increase or decrease both parameters
	 * with the same value
	 * @param fromTimeDiff extend current time period with x ms. If > 0, increase fromTime, if < 0, decrease fromTime
	 * @param toTimeDiff extend current time period with x ms. If > 0, increase toTime, if < 0, decrease toTime
	 * @see #isChangeTimePeriodSupported()
	 */
	void changeTimePeriod(long fromTimeDiff, long toTimeDiff);
	
	/**
	 * @return true if you may use {@link #changeTimePeriod(long, long)}
	 */
	boolean isChangeTimePeriodSupported();
}


