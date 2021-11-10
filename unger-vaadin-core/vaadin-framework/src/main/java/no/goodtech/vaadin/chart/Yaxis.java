package no.goodtech.vaadin.chart;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * En y-akse
 */
public class Yaxis {
	private Double minLimit, maxLimit;
	Map<String, TimeSeries> series = new LinkedHashMap<String, TimeSeries>();

	/**
	 * @return serier som bruker denne aksen, gruppert på navn
	 */
	public Map<String, TimeSeries> getSeries() {
		return series;
	}
	
//	/**
//	 * Legg til en serie om den ikke finnes fra før
//	 * @param name navn på serien
//	 * @param digital om den inneholder digitale data eller ikke (digital gir "firkant-graf")
//	 */
//	public void addSeries(String name, boolean digital) {
//		if (!getSeries().containsKey(name)) {
//			final Series series = new Series(name, colorProvider.getNextColor());
//			series.setDigital(digital);
//			getSeries().put(name, series);
//		}
//	}

	/**
	 * @return nedre grense for denne aksen
	 */
	public Double getMinLimit() {
		return minLimit;
	}

	/**
	 * @param min angi nedre grense for denne aksen
	 */
	public void setMinLimit(Double min) {
		this.minLimit = min;
	}

	/**
	 * @return øvre grense for denne aksen
	 */
	public Double getMaxLimit() {
		return maxLimit;
	}

	/**
	 * @param max angi øvre grense for denne aksen
	 */
	public void setMaxLimit(Double max) {
		this.maxLimit = max;
	}
	
	/**
	 * @return laveste verdi på grafen for denne aksen. Null om ingen verdier finnes for denne aksen
	 */
	public Double getMinValue() {
		Double minValue = null;
		for (TimeSeries series : getSeries().values())
			if (minValue == null || series.getMinValue() < minValue)
				minValue = series.getMinValue();
		return minValue;
	}
	
	/**
	 * @return høyeste verdi på grafen for denne aksen. Null om ingen verdier finnes for denne aksen
	 */
	public Double getMaxValue() {
		Double maxValue = null;
		for (TimeSeries series : getSeries().values())
			if (maxValue == null || series.getMaxValue() > maxValue)
				maxValue = series.getMaxValue();
		return maxValue;
	}
}
