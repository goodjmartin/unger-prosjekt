package no.goodtech.vaadin.chart;

import java.util.HashMap;
import java.util.Map;

/**
 * Inneholder grenseverdier for en eller flere y-akser
 * @author oystein
 */
public class YaxisRanges {
	
	public static class MinMaxValue {
		private Double minValue, maxValue;

		public MinMaxValue(Double min, Double max) {
			this.minValue = min;
			this.maxValue = max;
		}

		public Double getMinValue() {
			return minValue;
		}

		public Double getMaxValue() {
			return maxValue;
		}

		public void setMinValue(Double minValue) {
			this.minValue = minValue;
		}

		public void setMaxValue(Double maxValue) {
			this.maxValue = maxValue;
		}
		
		@Override
		public String toString() {
			return minValue + " - " + maxValue;
		}
	}

	private Map<String, MinMaxValue> ranges = new HashMap<String, MinMaxValue>();
	
	public void set(String axis, MinMaxValue minMaxValue) {
		ranges.put(axis, minMaxValue);
	}
	
	public void add(String axis) {
		if (ranges.get(axis) == null)
			set(axis, new MinMaxValue(null, null));
	}
	
	public MinMaxValue get(String axis) {
		return ranges.get(axis);
	}
	
	public MinMaxValue getAlways(String axis) {
		MinMaxValue range = ranges.get(axis);
		if (range == null)
			return new MinMaxValue(null, null);
		else
			return range;
	}
	
	/**
	 * Bruk denne hvis du ønsker å abonnere på endringer 
	 */
	public static interface RangeRefresher {
		
		/**
		 * Angitt akse er oppdatert
		 * @param axis navn på aksen
		 * @param range nye grenser
		 */
		void pleaseRefresh(String axis, MinMaxValue range);
	}
}
