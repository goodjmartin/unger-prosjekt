package no.goodtech.vaadin.chart;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.AbstractPlotOptions;
import com.vaadin.addon.charts.model.AxisTitle;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.Crosshair;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.DateTimeLabelFormats;
import com.vaadin.addon.charts.model.Legend;
import com.vaadin.addon.charts.model.Marker;
import com.vaadin.addon.charts.model.MarkerSymbolUrl;
import com.vaadin.addon.charts.model.PlotOptionsArea;
import com.vaadin.addon.charts.model.PlotOptionsLine;
import com.vaadin.addon.charts.model.PlotOptionsScatter;
import com.vaadin.addon.charts.model.PlotOptionsSpline;
import com.vaadin.addon.charts.model.Tooltip;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.charts.model.ZoomType;
import com.vaadin.addon.charts.model.style.FontWeight;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.addon.charts.model.style.Style;
import com.vaadin.ui.Notification;
import no.goodtech.vaadin.chart.TimeSeries.PlotType;
import no.goodtech.vaadin.global.VaadinSpringContextHelper;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

import java.util.Map;

//TODO import com.vaadin.addon.charts.model.AbstractLinePlotOptions;

/**
 * Tidsline-diagram som kan vise flere serier, hvor seriene kan være knytta til forskjellige y-akser
 */
public class MultipleYaxisTimeSeriesChart extends Chart {

	/**
	 * Opprett diagram og fyll det med data
	 *
	 * @param chartData       data som skal vises i diagrammet
	 * @param title           overskrift
	 * @param disableMarker   true = ikke vis markering for hvert punkt på grafen
	 * @param reductionLimit  simplify series with more than x points. If null, reduce over 1000 points
	 * @param reductionFactor simplyfy factor. If null, factor is 200. Higher value gives more reduction
	 * @see #drawChart() bruk denne for å vise diagrammet
	 */
	public MultipleYaxisTimeSeriesChart(TimeSeriesChartData chartData, String title, boolean disableMarker,
										boolean showLegend, boolean showTooltip, Integer reductionLimit, Integer reductionFactor) {
		super(ChartType.LINE);
		setSizeFull();

		//Chart settings
		Configuration configuration = getConfiguration();
		configuration.setTooltip(createTooltip());

		if (title != null)
			configuration.setTitle(title);
		else
			configuration.setTitle("");

		configuration.setExporting(true);
		configuration.getxAxis().setType(AxisType.DATETIME);
		configuration.getxAxis().setCrosshair(new Crosshair());
		configuration.getCredits().setEnabled(false);
		configuration.getChart().setZoomType(ZoomType.X);

		Legend legend = configuration.getLegend();
		legend.setEnabled(showLegend);

		//to make active series in legend more visible 
		legend.setItemStyle(createStyle(FontWeight.BOLD, SolidColor.BLACK));
		legend.setItemHiddenStyle(createStyle(FontWeight.NORMAL, SolidColor.GRAY));

		//Custom label format
		configuration.getxAxis().setDateTimeLabelFormats(new DateTimeLabelFormats("%e. %b", "%b"));

		if (reductionLimit == null)
			reductionLimit = 1000;
		if (reductionFactor == null)
			reductionFactor = 200;
		addData(chartData, disableMarker, showTooltip, reductionLimit, reductionFactor);
	}

	private Style createStyle(FontWeight fontWeight, SolidColor fontColor) {
		Style style = new Style();
		style.setFontWeight(fontWeight);
		style.setColor(fontColor);
		return style;
	}

	private void addData(TimeSeriesChartData chartData, boolean disableMarker, boolean showTooltip, int reductionLimit,
						 int reductionFactor) {
		Configuration configuration = getConfiguration();

		String reducedSeriesList = "";
		for (Map.Entry<String, Yaxis> axisEntry : chartData.getYaxes().entrySet()) {

			final YAxis yAxis = createYaxis(axisEntry);
			configuration.addyAxis(yAxis);

//			final Tooltip tooltip = createTooltip();

			//add all series connected to this yAxis
			for (Map.Entry<String, TimeSeries> seriesEntry : axisEntry.getValue().getSeries().entrySet()) {

				final TimeSeries series = seriesEntry.getValue();

				if (series.getValues().size() > 0) {
					DataSeries dataSeries = new DataSeries(series.getName());
					AbstractPlotOptions plotOptions = getPlotOptions(series, showTooltip);
					configuration.getTooltip().setEnabled(showTooltip);

					dataSeries.setPlotOptions(plotOptions);
					Marker marker = createMarker(series);

					// For hver måleverdi, legg et punkt til tidsserien med tidspunktet verdien bli målt, og målt verdi
					Long lastDigitalValue = null;
					for (SampleValue sampleValue : series.getValues()) {
						final Double value = sampleValue.getValue();
						final DataSeriesItem point = new DataSeriesItem(sampleValue.getTime(), value);
						if (series.isDigital() && PlotType.ICON.equals(series.getPlotType())) {
							final long longValue = value.longValue();
							if (longValue > 0L) {
								if (lastDigitalValue == null || longValue != lastDigitalValue) {
									//draw icon if digital value is true
									point.setMarker(marker);
								}
							} else {
								point.setMarker(new Marker(false));
							}
							lastDigitalValue = longValue;
						}
						dataSeries.add(point);
					}
					if (!series.isDigital() && series.getValues().size() > reductionLimit) {
						reducedSeriesList = reducedSeriesList + seriesEntry.getKey() + ", \n";
						LargeDataSet.ramerDouglasPeuckerReduce(dataSeries, reductionFactor);
					}
					configuration.addSeries(dataSeries);
					dataSeries.setyAxis(yAxis);
				}
			}
		}
		if (!reducedSeriesList.isEmpty()) {
			Notification.show(getText("reduction.warning.prefix") + reducedSeriesList, Notification.Type.TRAY_NOTIFICATION);
		}
	}

	private Tooltip createTooltip() {
		final Tooltip tooltip = new Tooltip();
		tooltip.setShared(true);
		tooltip.setEnabled(true);
		tooltip.setXDateFormat("%Y.%m.%d %H:%M");
		tooltip.setHeaderFormat("<span style='font-size: 10px'>{point.key}</span>");
		tooltip.setPointFormat("<br/><span style='color:{series.color}'>{series.name}</span>: <b>{point.y}</b>");
		tooltip.setUseHTML(true);
		return tooltip;
	}

	private Marker createMarker(TimeSeries series) {
		Marker marker = new Marker();
		ISeriesIconProvider iconProvider = VaadinSpringContextHelper.getBean(ISeriesIconProvider.class);
		String url = iconProvider.getIconUrl(series);
		marker.setSymbol(new MarkerSymbolUrl(url));
		return marker;
	}

	private AbstractPlotOptions getPlotOptions(TimeSeries series, boolean showTooltip) {
		AbstractPlotOptions options;
		PlotType plotType = series.getPlotType();
		if (plotType == null) {
			options = new PlotOptionsLine();
			PlotOptionsLine plotOptions = (PlotOptionsLine) options;
			plotOptions.setAnimation(false);
			plotOptions.setColor(series.getColor());
			plotOptions.setEnableMouseTracking(showTooltip);
		} else {
			switch (plotType) {
				case SPLINE: {
					options = new PlotOptionsSpline();
					PlotOptionsSpline plotOptions = (PlotOptionsSpline) options;
					plotOptions.setAnimation(false);
					plotOptions.setColor(series.getColor());
					plotOptions.setEnableMouseTracking(showTooltip);
					break;
				}
				case SCATTER:
				case ICON: {
					options = new PlotOptionsScatter();
					PlotOptionsScatter plotOptions = (PlotOptionsScatter) options;
					plotOptions.setAnimation(false);
					plotOptions.setColor(series.getColor());
					plotOptions.setEnableMouseTracking(showTooltip);
					break;
				}
				case AREA: {
					options = new PlotOptionsArea();
					PlotOptionsArea plotOptions = (PlotOptionsArea) options;
					plotOptions.setAnimation(false);
					plotOptions.setColor(series.getColor());
					plotOptions.setEnableMouseTracking(showTooltip);
					break;
				}
				default: {
					options = new PlotOptionsLine();
					PlotOptionsLine plotOptions = (PlotOptionsLine) options;
					plotOptions.setAnimation(false);
					plotOptions.setColor(series.getColor());
					plotOptions.setEnableMouseTracking(showTooltip);
				}
			}
		}

		if (options instanceof PlotOptionsLine && !(options instanceof PlotOptionsScatter)) {
			PlotOptionsLine linePlotOptions = (PlotOptionsLine) options;
			linePlotOptions.setMarker(new Marker(false)); //deaktiverer markering av hvert punkt
			linePlotOptions.setLineWidth(1);
		}

		return options;
	}

	private YAxis createYaxis(Map.Entry<String, Yaxis> axisEntry) {
		YAxis yAxis = new YAxis();
		final String axisName = axisEntry.getKey();
		yAxis.setTitle(new AxisTitle(axisName));
		yAxis.setCrosshair(new Crosshair());
		final Double minY = axisEntry.getValue().getMinLimit();
		if (minY != null)
			yAxis.setMin(minY);
		final Double maxY = axisEntry.getValue().getMaxLimit();
		if (maxY != null)
			yAxis.setMax(maxY);
		return yAxis;
	}

	private static String getText(String key) {
		return ApplicationResourceBundle.getInstance("vaadin-core").getString("chart." + key);
	}
}
