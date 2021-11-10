package no.goodtech.admin.tabs.report;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.ChartOptions;
import com.vaadin.addon.charts.PointClickListener;
import com.vaadin.addon.charts.model.*;
import com.vaadin.ui.VerticalLayout;
import no.goodtech.vaadin.chart.MultipleYaxisTimeSeriesChart;
import no.goodtech.vaadin.chart.TimeSeriesChartData;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

import java.util.*;

public class GraphTab extends VerticalLayout {

	private Chart chart;
	private String chartTitle;
	private final String xAxistitle;
	private final String yAaxsisTitle;
	private final String chartType;
	private final String axisType;
	private final Boolean showTooltip;
	private final boolean exporting;
	private final Stacking stacking;

	/**
	 * Hack for å kunne angi diagram-type vha. vaadins interne navn på typene
	 * @author oystein
	 */
	public class NiceChartType extends ChartType {
		public NiceChartType(String type) {
			super(type);
		}
	}

	/**
	 * @param chartType see {@link ChartType}, defaults to {@link ChartType#BAR}
	 * @param stacking provide this if you want stacking, defaults to {@link Stacking#NONE}
	 */
	public GraphTab(String chartTitle, String xAxisTitle, String yAxisTitle, String chartType, String axisType,
					Boolean showTooltip, String stacking, boolean exporting) {

		this.chartTitle = chartTitle;
		this.xAxistitle = xAxisTitle;
		this.yAaxsisTitle = yAxisTitle;
		this.axisType = axisType;
		this.showTooltip = showTooltip;
		this.exporting = exporting;

		if (stacking == null)
			this.stacking = Stacking.NONE;
		else
			this.stacking = Stacking.valueOf(stacking.toUpperCase());

		if (chartType == null) {
			this.chartType = ChartType.BAR.toString();
		} else {
			this.chartType = chartType;
		}

		setMargin(false);
		setSpacing(false);
	}

	private static List<DataSeriesItem> getPoints(Series series, List<Double> values) {
		List<DataSeriesItem> points = new ArrayList<>();
		for (double value : values) {
			DataSeriesItem dataSeriesItem = new DataSeriesItem();
			dataSeriesItem.setX(value);
			points.add(new DataSeriesItem(series.getName(), value));
		}
		return points;
	}

	/**
	 * Bygger opp grafen med nye punkter, samler kategorier i en liste av String variable, og
	 * setter disse kategoriene i clearchart metoden.
	 * typeValuesPointsMap inneholder en string peker til en arraylist av Double variable,
	 * pekeren er navnet på XY-serien, listen er hvilke verdier den inneholder.
	 * <p/>
	 * Eksempel på resultatet av spørring brukt for å generere grafen
	 * navn	                        Type                                antall
	 * Tekstilruller KIM - HB	        Desinfisering                   	21
	 * Tekstilruller KIM - HB	        Digitalisering                  	4
	 * Tekstilruller KIM - HB	        Magasinering	                    9
	 * Tekstilruller KIM - HB	        Plassering	                        77
	 * @param rows resultatet av SQL spørringen
	 */
	public void buildGraph(List<List<?>> rows) {
		if (chart != null)
			removeComponent(chart);

		final List<String> seriesNameList = findSeries(rows);

		if (axisType != null && axisType.equalsIgnoreCase(AxisType.DATETIME.toString())) {
			TimeSeriesChartData chartData = new TimeSeriesChartData();
			for (List<?> columnValues : rows) {
				final Object x = columnValues.get(0);
				final Object value = columnValues.get(1);
				if (value != null && value instanceof Number && x != null && x instanceof Date)
					chartData.addValue((Number) value, (Date) x, yAaxsisTitle, yAaxsisTitle, false);
			}

			chart = new MultipleYaxisTimeSeriesChart(chartData, chartTitle, false, true, showTooltip(), null, null);
		} else {
			createCategoryChart(rows, seriesNameList);
		}
		addComponent(chart);
		chart.getConfiguration().setExporting(exporting);
		chart.getConfiguration().setTitle(chartTitle);
	}

	/**
	 * Builds a clicker listener for the chart to navigate to other views in the application.
	 * To generate clicker listener, add a list of <parameters></parameters> to the <chart></chart>.
	 * See clickableUrlParameters in chartType for more information
	 */
	public void addPointClickListener(PointClickListener clickListener){
		if (chart != null) {
			chart.addPointClickListener(clickListener);
		}
	}

	/**
	 * @return configuration explisitly ask for tooltip to be shown
	 */
	private boolean showTooltip() {
		return showTooltip != null && showTooltip;
	}

	/**
	 * @return configuration explisitly asks to hide tooltip
	 */
	private boolean hideTooltip() {
		return showTooltip != null && !showTooltip;
	}

	private Chart createCategoryChart(List<List<?>> rows, final List<String> seriesNameList) {

		Map<String, List<Double>> typeValuesPointsMap = new LinkedHashMap<String, List<Double>>();

		for (List<?> columnValues : rows) {
			final Object x = columnValues.get(0);
			if (columnValues.size() == 3 && x != null && columnValues.get(2) != null) {
				String category = columnValues.get(1).toString();
				List<Double> categoryValues = typeValuesPointsMap.get(category);
				if (categoryValues == null) {
					categoryValues = new ArrayList<>();
					for (int i = 0; i < seriesNameList.size(); i++) {
						categoryValues.add(0.0);
					}
					typeValuesPointsMap.put(category, categoryValues);
				}

				// TODO easier way?
				int indexOfSeries = seriesNameList.indexOf(x.toString());
				categoryValues.add(indexOfSeries, Double.parseDouble(columnValues.get(2).toString()));
				categoryValues.remove(indexOfSeries + 1);
			}
		}
		// Adding points to the specific category
		clearChart(seriesNameList);
		for (Map.Entry<String, List<Double>> entry : typeValuesPointsMap.entrySet()) {
			String series = entry.getKey();
			List<Double> points = entry.getValue();

			DataSeries xySeries = new DataSeries();
			xySeries.setName(series);
			xySeries.setData(getPoints(xySeries, points));
			chart.getConfiguration().addSeries(xySeries);
		}
		return chart;
	}

	private void applyStacking() {
		if (!Stacking.NONE.equals(stacking)) {
			final Configuration configuration = chart.getConfiguration();

			if ("column".equalsIgnoreCase(chartType)) {
				PlotOptionsColumn options = new PlotOptionsColumn();
				options.setStacking(stacking);
				configuration.setPlotOptions(options);
			} else if ("bar".equalsIgnoreCase(chartType)) {
				PlotOptionsBar options = new PlotOptionsBar();
				options.setStacking(stacking);
				configuration.setPlotOptions(options);
			} else if ("area".equalsIgnoreCase(chartType)) {
				PlotOptionsArea options = new PlotOptionsArea();
				options.setStacking(stacking);
				configuration.setPlotOptions(options);
			}
		}
	}

	/**
	 * Adds unique series to a list
	 * @param rows data from sql
	 * @return list of unique series
	 */
	private List<String> findSeries(List<List<?>> rows) {
		List<String> seriesNameList = new ArrayList<>();
		for (List<?> columnValues : rows) {
			final Object firstColumnValue = columnValues.get(0);
			if (firstColumnValue != null && !seriesNameList.contains(firstColumnValue.toString())) {
				seriesNameList.add(firstColumnValue.toString());
			}
		}
		return seriesNameList;
	}

	/**
	 * Fjerner grafen og bygger opp på nytt med ny data
	 * @param seriesNameList Kategorier, feks måneder, productionrequests, eller prosjekter
	 */
	private void clearChart(final List<String> seriesNameList) {

		Configuration chartConfig = new Configuration();
		chartConfig.getChart().setType(new NiceChartType(chartType));
		chartConfig.getCredits().setEnabled(false);
		chartConfig.getTitle().setText(chartTitle);

		chartConfig.getTooltip().setEnabled(!hideTooltip());
		if (!hideTooltip()) {
			chartConfig.getTooltip().setShared(false);
			chartConfig.getTooltip().setHeaderFormat("");
		}

		chartConfig.addxAxis(createXaxis(seriesNameList));
		chartConfig.addyAxis(createYaxis());

		Legend legend = new Legend();
		legend.setShadow(true);
		chartConfig.setLegend(legend);

		chart = new Chart();
		Lang lang = new Lang();
		lang.setNoData(getText("graphTab.noData"));
		ChartOptions.get().setLang(lang);
		chart.setConfiguration(chartConfig);
		applyStacking();
		chart.setSizeFull();
		addComponent(chart);
		chart.drawChart();
	}

	private YAxis createYaxis() {
		YAxis yAxis = new YAxis();
		yAxis.setMin(0.0);
		yAxis.setTitle(yAaxsisTitle);
		return yAxis;
	}

	private XAxis createXaxis(final List<String> seriesNames) {
		XAxis xAxis = new XAxis();
		xAxis.setTitle(xAxistitle);
		if (axisType != null)
			xAxis.setType(AxisType.valueOf(axisType.toUpperCase()));

		if (seriesNames.size() > 0)
			xAxis.setCategories(seriesNames.toArray(new String[1]));
		else
			xAxis.setCategories(" ");
		return xAxis;
	}

	public String getChartTitle() {
		return chartTitle;
	}

	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}

	private String getText(String key){
		return ApplicationResourceBundle.getInstance("vaadin-report").getString(key);
	}
}