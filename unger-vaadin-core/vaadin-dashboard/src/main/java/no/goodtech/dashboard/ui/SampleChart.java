package no.goodtech.dashboard.ui;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.FontWeight;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.addon.charts.model.style.Style;
import com.vaadin.addon.charts.util.Util;
import no.goodtech.dashboard.Globals;
import no.goodtech.dashboard.config.ui.AxisConfig;
import no.goodtech.dashboard.config.ui.ChartConfig;
import no.goodtech.dashboard.config.ui.SeriesConfig;
import no.goodtech.dashboard.model.CurrentTimeProvider;
import no.goodtech.dashboard.model.SampleDTO;
import no.goodtech.dashboard.model.SeriesInfo;
import no.goodtech.push.ISubscriber;
import no.goodtech.vaadin.PrecisionUtils;
import no.goodtech.vaadin.chart.ColorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.timer.Timer;
import java.util.*;

public class SampleChart extends Chart implements IDashboardSubPanel {

	private static final Logger LOGGER = LoggerFactory.getLogger(SampleChart.class);
	private final Configuration configuration = new Configuration();
	private final ChartConfig chartConfig;
	private volatile Map<SeriesConfig, Date> lastHitTimeMap = new HashMap<>();
	private final Map<AxisConfig, YAxis> yAxes= new LinkedHashMap<>();
	private final ValueChangePlotBandHandler valueChangePlotBandHandler;
	private final boolean live;

	public SampleChart(final ChartConfig chartConfig, boolean live) {

		setSizeFull();

		this.chartConfig = chartConfig;
		valueChangePlotBandHandler = new ValueChangePlotBandHandler(this.configuration);

		// Set chart type
		configuration.getChart().setType(ChartType.LINE);

		// Set chart title
		configuration.getTitle().setText(chartConfig.getTitle());
		configuration.getTitle().setAlign(HorizontalAlign.LEFT);

		// Align tick marks on axes when we have multiple axes
		configuration.getChart().setAlignTicks(chartConfig.isAlignTicks());

		// Set type for x-axis
		configuration.getxAxis().setType(AxisType.DATETIME);

		// Set time label format
		configuration.getTooltip().setXDateFormat("%d.%m.%Y %H:%M:%S");

		// Turn chart legend on (if multiple series)
		Legend legend = configuration.getLegend();
		if (chartConfig.getSeriesConfigs().size() > 1) {
			legend.setEnabled(true);
			legend.setAlign(HorizontalAlign.RIGHT);
			legend.setVerticalAlign(VerticalAlign.TOP);
			legend.setFloating(true);
			legend.setX(0);
			legend.setY(0);
		} else {
			legend.setEnabled(false);
		}

		// Turn animation off for chart
		configuration.getChart().setAnimation(false);

		addYAxes(chartConfig.getyAxes()); // Add each y-axis

		for (SeriesConfig seriesConfig : chartConfig.getSeriesConfigs()) {
			if (!seriesConfig.isPlotband()) {
				findOrCreateSeries(seriesConfig);
			}
		}

		// Set default credit text
		configuration.disableCredits();

		Style style = new Style();
		style.setFontSize("16px");
		style.setFontWeight(FontWeight.BOLD);
		style.setColor(SolidColor.DARKBLUE);
		configuration.getTitle().setStyle(style);

		Style yAxisStyle = new Style();
		yAxisStyle.setFontSize("10px");
		yAxisStyle.setColor(SolidColor.GRAY);
		this.configuration.getyAxis().getLabels().setStyle(yAxisStyle);

		Style yAxisTitleStyle = new Style();
		yAxisTitleStyle.setFontSize("12px");
		yAxisTitleStyle.setColor(SolidColor.DARKBLUE);

		for (YAxis axis : this.configuration.getyAxes().getAxes()) {
			axis.getLabels().setStyle(yAxisStyle);
			axis.setLineWidth(1);
			axis.setLineColor(SolidColor.DARKGRAY);
			axis.getTitle().setStyle(yAxisTitleStyle);
		}

		Style xAxisStyle = new Style();
		xAxisStyle.setFontSize("12px");
		xAxisStyle.setColor(SolidColor.GRAY);

		this.configuration.getxAxis().setTickColor(SolidColor.DARKGRAY);
		this.configuration.getxAxis().setTickWidth(1);
		this.configuration.getxAxis().setLineWidth(1);
		this.configuration.getxAxis().setLineColor(SolidColor.DARKGRAY);
		this.configuration.getxAxis().getLabels().setStyle(xAxisStyle);

		this.live = live;


		// Refresh chart data
		refresh();

		drawChart(configuration);

		createAxisChangedSubscriber();
	}

	private void createAxisChangedSubscriber() {
		ISubscriber<AxisConfig> axisChangedSubscriber = axisConfig -> getUI().access(new Runnable() {
			@Override
			public void run() {
				YAxis yAxis = yAxes.get(axisConfig);
				if (yAxis != null) {
					yAxis.setMin(axisConfig.getMinValue());
					yAxis.setMax(axisConfig.getMaxValue());
					LOGGER.debug("axis changed on chart {}, pk = {} on dashboard {}, min={}, max={}", chartConfig.getTitle(),
							chartConfig.getPk(), chartConfig.getDashboardConfig().getId(), yAxis.getMin(), yAxis.getMax());
				}
			}
		});
		addAttachListener((AttachListener) (AttachEvent event) -> {
			Globals.getAxisChangedAdaptor().register(axisChangedSubscriber);
		});
		addDetachListener((DetachListener) event -> {
			Globals.getAxisChangedAdaptor().unregister(axisChangedSubscriber);
		});
	}

	private DataSeries findOrCreateSeries(SeriesConfig  seriesConfig) {
		DataSeries chartSeries = findDataSeries(seriesConfig);
		if (chartSeries == null) {
			if (seriesConfig.getFixedValue() == null) {
				chartSeries = new DataSeries();
				// Create sample series
				chartSeries.setName(seriesConfig.getName());
				chartSeries.setId(seriesConfig.getId());

				// Turn animation off for series
				PlotOptionsLine plotOptions = new PlotOptionsLine();
				plotOptions.setAnimation(false);

				// Turn marker symbol on / off (depending on configuration)
				plotOptions.setMarker(new Marker(seriesConfig.isShowMarker()));

				plotOptions.setShadow(false);
				if (seriesConfig.getLineWidth() != null) {
					plotOptions.setLineWidth(seriesConfig.getLineWidth());
				} else {
					plotOptions.setLineWidth(2);
				}

				final Color color = ColorUtils.createColor(seriesConfig.getColor());
				if (color != null) {
					plotOptions.setColor(color);
				}
				chartSeries.setPlotOptions(plotOptions);

				configuration.addSeries(chartSeries); // Add series to chart
				applyToAxis(seriesConfig, chartSeries);

			} else {
				//this is a series with fixed value, so draw fixed plot line
				PlotLine plotLine = new PlotLine();
				plotLine.setValue(seriesConfig.getFixedValue());
				plotLine.setColor(ColorUtils.createColor(seriesConfig.getColor()));
				plotLine.setId(seriesConfig.getId());
				plotLine.setWidth(2);
				plotLine.setLabel(new Label(seriesConfig.getName()));
				applyToAxis(seriesConfig, plotLine);
			}
		}
		return chartSeries;
	}

	private void applyToAxis(SeriesConfig seriesConfig, PlotLine plotLine) {
		YAxis axis = findYAxis(seriesConfig);
		if (axis == null) {
			final AxisList<YAxis> yAxisAxisList = getConfiguration().getyAxes();
			if (yAxisAxisList != null && yAxisAxisList.getNumberOfAxes() > 0) {
				axis = yAxisAxisList.getAxis(0);
			} else {
				axis = new YAxis();
				getConfiguration().addyAxis(axis);
			}
		}
		axis.addPlotLine(plotLine);
	}

	private YAxis findYAxis(SeriesConfig seriesConfig) {
		final AxisConfig axisConfig = seriesConfig.getAxisConfig();
		if (axisConfig != null) {
			final YAxis axis = yAxes.get(axisConfig);
			if (axis != null) {
				return axis;
			}
			LOGGER.warn("Axis of SeriesConfig {} does not belong to this chart, ignoring axis", seriesConfig.getId());
		}
		return null;
	}

	private void applyToAxis(SeriesConfig seriesConfig, DataSeries chartSeries) {
		final YAxis axis = findYAxis(seriesConfig);
		if (axis != null) {
			chartSeries.setyAxis(axis); //use specific y axisConfig for this series
		} else if (seriesConfig.getFixedValue() != null) {
			//auto-create axisConfig for this series because else series will not be visible
			final YAxis dummyAxis = new YAxis();
			final String axisId = "autocreated-axisConfig-for-series-" + seriesConfig.getId();
			dummyAxis.setId(axisId);
			configuration.addyAxis(dummyAxis);
			final AxisConfig dummyAxisConfig = new AxisConfig();
			dummyAxisConfig.setName(axisId);
			yAxes.put(dummyAxisConfig, dummyAxis);
			chartSeries.setyAxis(dummyAxis);
		}
	}

	/**
	 * Create Y-axis and add it to the configuration
	 * @param sampleAxisConfigs the y-axis from file
	 */
	private void addYAxes(Collection<AxisConfig> sampleAxisConfigs) {
		int axisIndex = 0;
		for (AxisConfig axisConfig : sampleAxisConfigs) {
			YAxis axis = new YAxis();
			configuration.addyAxis(axis);
			yAxes.put(axisConfig, axis);

			axis.setTitle(new AxisTitle(axisConfig.getName()));
			axis.getTitle().setAlign(VerticalAlign.HIGH);
			axis.getTitle().setAlign(VerticalAlign.HIGH);
			axis.setOpposite(axisConfig.isOpposite());

			// Set the tick interval
			if (axisConfig.getTickInterval() != null)
				axis.setTickInterval(axisConfig.getTickInterval());

			// Set y-axis min / max value
			axis.setMin(axisConfig.getMinValue());
			axis.setMax(axisConfig.getMaxValue());

			//to take absolute control over min/max when using multiple axes, you must do this:
			//setAlignTicks on chart = false and hide the grid
			//use line with = 0 and alternate grid color = white to hide the grid
			axis.setGridLineWidth(axisConfig.getGridLineWidth());
			axis.setLineWidth(axisConfig.getLineWidth());
			axis.setTickWidth(axisConfig.getTickWidth());
			if (axisConfig.isHideAlternateGrid())
				axis.setAlternateGridColor(SolidColor.WHITE);

			axisIndex++;
		}
	}

	public void refresh() {

		final long now = CurrentTimeProvider.getTime();
		Date startTime = new Date(now - chartConfig.getPeriodLengthInMinutes() * Timer.ONE_MINUTE);
		int valueCount = 0;
		int seriesCount = 0;

		// Fetch new sample points:
		for (SeriesConfig seriesConfig : Globals.getDashboardSeriesManager().getSeriesConfigs(chartConfig)) {
			Date fromTime = lastHitTimeMap.get(seriesConfig);
			if (fromTime == null) {
				fromTime = startTime;
			}
			final SeriesInfo points = Globals.getDashboardSeriesManager().getSamplePoints(seriesConfig, fromTime);
			refreshSeries(startTime, seriesConfig, points);
			seriesCount ++;
			valueCount += points.count();
		}
		if (live) {
			// Set start and end time for chart
			configuration.getxAxis().setMin(startTime);
			configuration.getxAxis().setMax(new Date(now));
		}

		// TODO: Remove this call when Vaadin Chart ticket 'http://dev.vaadin.com/ticket/11701' is solved
		// TODO: - this should reduce the data being transferred to the browser dramatically
		drawChart();
		LOGGER.debug("refresh(): chart {}, pk = {} on dashboard {} got {} values in {} series", chartConfig.getTitle(),
				chartConfig.getPk(), chartConfig.getDashboardConfig().getId(), valueCount, seriesCount);
	}

	private void refreshSeries(Date startTime, SeriesConfig seriesConfig, SeriesInfo seriesInfo) {
		if (seriesInfo != null) {
			final List<SampleDTO> values = seriesInfo.getSampleDTOs();
			if (seriesConfig.isPlotband()) {
				if (seriesInfo.isFullFetch()) {
					valueChangePlotBandHandler.removeBands(seriesConfig, null);
				} else {
					valueChangePlotBandHandler.removeBands(seriesConfig, startTime); //remove obsolete points
				}
				valueChangePlotBandHandler.createBands(seriesConfig, values); // Add new points
			} else {
				DataSeries chartSeries = findOrCreateSeries(seriesConfig);
				if (chartSeries != null) {
					if (seriesInfo.isFullFetch()) {
						chartSeries.clear(); // Clear all existing points if it is a full fetch
					} else {
						removeOldPoints(startTime, chartSeries); //remove obsolete points
					}
					// Add new points
					for (SampleDTO sampleDTO : values) {
						chartSeries.add(new DataSeriesItem(sampleDTO.getCreated(), sampleDTO.getValue()), true, false);
					}
					refreshRunningAverage(chartSeries, seriesConfig);

					LOGGER.debug(String.format("Refreshed series: id=%20s, new=%3d, points=%3d", seriesConfig.getId(), values.size(), chartSeries.getData().size()));
				} else if (seriesConfig.getFixedValue() == null){
					LOGGER.warn("Fant ikke serie {} på dashboardet, så data fra denne serien blir ikke vist", seriesConfig.getId());
				}
			}
			if (values.size() > 0) {
				lastHitTimeMap.put(seriesConfig, values.get(values.size() - 1).getCreated());
			}
		}
	}

	private void refreshRunningAverage(DataSeries chartSeries, SeriesConfig seriesConfig) {
		final ChartConfig panelConfig = seriesConfig.getPanelConfig();
		final SeriesConfig runningAverageSeries = panelConfig.getRunningAverageSeries();

		if (runningAverageSeries != null && runningAverageSeries.equals(seriesConfig)) {
			final List<DataSeriesItem> dataSeriesItems = chartSeries.getData();
			Integer maxValueCount = panelConfig.getRunningAverageMaxValueCount();
			if (maxValueCount == null || maxValueCount > dataSeriesItems.size())
				maxValueCount = dataSeriesItems.size();
			int maxIndex = dataSeriesItems.size() - 1;
			double sum = 0;
			for (int i = maxIndex; i > maxIndex - maxValueCount; i--) {
				DataSeriesItem item = dataSeriesItems.get(i);
				sum += item.getY().doubleValue();
			}
			double average = sum/maxValueCount;

			int precision = 0;
			if (panelConfig.getRunningAveragePrecision() != null)
				precision = panelConfig.getRunningAveragePrecision();

			PrecisionUtils precisionUtils = new PrecisionUtils(precision);
			final String text = precisionUtils.formatNumber(average);
			final Credits credits = configuration.getCredits();
			credits.setEnabled(true);
			credits.setText(text);
			final Position position = credits.getPosition();
			position.setY(20);
			position.setX(-10);
			position.setVerticalAlign(VerticalAlign.TOP);
			configuration.setCredits(credits);
		}
	}

	// Remove sample points older than startTime
	private void removeOldPoints(Date startTime, DataSeries chartSeries) {
		while (true) {
			DataSeriesItem dataSeriesItem = (chartSeries.size() > 0) ? chartSeries.get(0) : null;

			if ((dataSeriesItem != null) && (Util.toServerDate(dataSeriesItem.getX().longValue()).getTime() < startTime.getTime())) {
				chartSeries.remove(dataSeriesItem);
			} else {
				break;
			}
		}
	}

	/**
	 * Refresh all series in chart with given data
	 * @param dataPerSeries data
	 */
	public void refresh(Map<SeriesConfig, SeriesInfo> dataPerSeries) {
		for (Map.Entry<SeriesConfig, SeriesInfo> entry : dataPerSeries.entrySet()) {
			final SeriesInfo seriesInfo = entry.getValue();
			refreshSeries(new Date(0), entry.getKey(), seriesInfo);
		}
	}

	private DataSeries findDataSeries(SeriesConfig seriesConfig) {
		for (Series dataSeries : configuration.getSeries()) {
			if (seriesConfig.getId().equals(dataSeries.getId())) {
				return (DataSeries) dataSeries;
			}
		}
		return null;
	}
}
