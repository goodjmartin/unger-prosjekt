package no.goodtech.vaadin.chart;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;

/**
 * Algoritme for å redusere antall punkter i en dataserie i forhold til hvaslags oppløsning man har på skjermen 
 * TODO: Hvor har vi funnet koden?
 * http://en.wikipedia.org/wiki/Ramer–Douglas–Peucker_algorithm
 */
public class RamerDouglasPeucker {

	/**
	 * Reduce data series with bit more sophisticated method.
	 *
	 * @param series with x y data pairs
	 * @param pixels number of pixels for series
	 */
	public static DataSeries reduce(DataSeries series, int pixels) {
		// Calculate rough estimate for visual ratio on x-y, might be bad guess
		// if axis have been set manually
		DataSeriesItem dataSeriesItem = series.get(0);
		double xMax = dataSeriesItem.getX().doubleValue();
		double xMin = xMax;
		double yMax = dataSeriesItem.getY().doubleValue();
		double yMin = yMax;
		for (int i = 1; i < series.size(); i++) {
			DataSeriesItem item = series.get(i);
			double x = item.getX().doubleValue();
			if (xMax < x) {
				xMax = x;
			}
			if (xMin > x) {
				xMin = x;
			}
			double y = item.getY().doubleValue();
			if (yMax < y) {
				yMax = y;
			}
			if (yMin > y) {
				yMin = y;
			}
		}
		double xyRatio = (xMax - xMin) / (yMax - yMin);

		// rough estimate for sane epsilon (1px)
		double epsilon = (xMax - xMin) / pixels;

		List<DataSeriesItem> rawData = series.getData();
		List<DataSeriesItem> reduced = ramerDouglasPeucker(rawData, epsilon, xyRatio);
		series.setData(reduced);
		return series;
	}

	/**
	 * @param points  points
	 * @param epsilon epsilon
	 * @param xyRatio y values are multiplied with this to make distance calculation
	 *                in algorithm sane
	 * @return list of items
	 */
	private static List<DataSeriesItem> ramerDouglasPeucker(
			List<DataSeriesItem> points, double epsilon, final double xyRatio) {
		// Find the point with the maximum distance
		double dmax = 0;
		int index = 0;
		DataSeriesItem start = points.get(0);
		DataSeriesItem end = points.get(points.size() - 1);

		for (int i = 1; i < points.size() - 1; i++) {
			DataSeriesItem point = points.get(i);
			double d = pointToLineDistance(start, end, point, xyRatio);
			if (d > dmax) {
				index = i;
				dmax = d;
			}
		}

		ArrayList<DataSeriesItem> reduced = new ArrayList<DataSeriesItem>();
		if (points.size() > 1 && dmax >= epsilon) {
			// max distance is greater than epsilon, keep the most relevant
			// point and recursively simplify
			List<DataSeriesItem> startToRelevant = ramerDouglasPeucker(
					points.subList(0, index + 1), epsilon, xyRatio);
			reduced.addAll(startToRelevant);
			List<DataSeriesItem> relevantToEnd = ramerDouglasPeucker(
					points.subList(index, points.size() - 1), epsilon, xyRatio);
			reduced.addAll(relevantToEnd.subList(1, relevantToEnd.size()));
		} else {
			// no relevant points, drop all but ends
			reduced.add(start);
			reduced.add(end);
		}

		return reduced;
	}

	private static double pointToLineDistance(DataSeriesItem A, DataSeriesItem B, DataSeriesItem P, final double xyRatio) {
		double bY = B.getY().doubleValue() * xyRatio;
		double aY = A.getY().doubleValue() * xyRatio;
		double pY = P.getY().doubleValue() * xyRatio;
		double normalLength = Math.hypot(B.getX().doubleValue()
				- A.getX().doubleValue(), bY - aY);
		return Math.abs((P.getX().doubleValue() - A.getX().doubleValue())
				* (bY - aY) - (pY - aY)
				* (B.getX().doubleValue() - A.getX().doubleValue()))
				/ normalLength;
	}

}
