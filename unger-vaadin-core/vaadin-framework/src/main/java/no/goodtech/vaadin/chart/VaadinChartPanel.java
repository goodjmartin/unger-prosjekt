package no.goodtech.vaadin.chart;

import com.vaadin.addon.charts.ChartClickEvent;
import com.vaadin.addon.charts.ChartClickListener;
import com.vaadin.addon.charts.ChartSelectionEvent;
import com.vaadin.addon.charts.ChartSelectionListener;
import com.vaadin.addon.charts.util.Util;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import no.goodtech.vaadin.chart.TimeSeries.PlotType;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

/**
 * Viser et interaktivt tidsserie-diagram basert på Vaadin Charts
 */
public class VaadinChartPanel extends VerticalLayout implements DynamicChart {

	private static Logger LOGGER = LoggerFactory.getLogger(VaadinChartPanel.class);

	private TimeSeriesChartData data;
	private final String title;
	private boolean showLegend;
	private boolean showTooltip;
	private Integer reductionLimit;
	private Integer reductionFactor;
	private ChartSelectionListener selectionListener;
	private Date originalFromTime, originalToTime;
	private boolean zoom = false;
	private ChartClickListener clickListener;
	private final ZoomListener zoomListener;
	final YaxisRanges yaxisRanges;
	final Map<String, PlotType> plotTypes;

	public interface ZoomListener {
		TimeSeriesChartData loadNewData(Date fromTome, Date toTime);
	}

	public interface ZoomTriggeredListener {
		void zoomStarted();
	}

	/**
	 * Opprett og vis interakintiv graf i samme vindu
	 * @param data data som skal vises i diagrammet
	 * @param yaxisRanges grenseverdier for y-akse
	 * @param plotTypes how to plot each series. Key = series name
	 * @param title overskrift
	 * @param previousComponent forrige komponent
	 * @param zoomListener if you want to fetch data again when the user zooms, provide this. Set to null if you don't
	 */
	public VaadinChartPanel(final TimeSeriesChartData data, final YaxisRanges yaxisRanges, final Map<String, PlotType> plotTypes,
			final String title,	boolean showLegend, boolean showTooltip,
			Integer reductionLimit,	Integer reductionFactor, final ZoomListener zoomListener) {

		this.data = data;
		this.title = title;
		this.showLegend = showLegend;
		this.showTooltip = showTooltip;
		this.reductionLimit = reductionLimit;
		this.reductionFactor = reductionFactor;
		this.originalFromTime = data.getMinDate();
		this.originalToTime = data.getMaxDate();
		this.zoomListener = zoomListener;
		this.yaxisRanges = yaxisRanges;
		this.plotTypes = plotTypes;
		setPlotTypes(plotTypes);
		setYaxisRanges(yaxisRanges);
		
		if (zoomListener != null) {
			selectionListener = new ChartSelectionListener() {
				public void onSelection(ChartSelectionEvent event) {
					if (!zoom)
						Notification.show(getText("zoom.reset"), Type.TRAY_NOTIFICATION);
					Date fromTime = Util.toServerDate(event.getSelectionStart());
					Date toTime = Util.toServerDate(event.getSelectionEnd());
					LOGGER.debug("Zoom: fromTime = {}, toTime = {}", fromTime, toTime);
					VaadinChartPanel.this.data = zoomListener.loadNewData(fromTime, toTime);
					setYaxisRanges(yaxisRanges);
					setPlotTypes(plotTypes);
					zoom = true;
					refresh();
				}
			};
			clickListener = new ChartClickListener() {
				public void onClick(ChartClickEvent event) {
					if (zoom) {
						VaadinChartPanel.this.data = zoomListener.loadNewData(originalFromTime, originalToTime);
						setYaxisRanges(yaxisRanges);
						setPlotTypes(plotTypes);
						zoom = false;
						refresh();
					}
				}
			};
		}
		setSizeFull();
		setMargin(false);
		setSpacing(false);
		refresh();
	}

	private MultipleYaxisTimeSeriesChart createChart() {
		MultipleYaxisTimeSeriesChart chart = new MultipleYaxisTimeSeriesChart(data, title, true, showLegend, showTooltip,
				reductionLimit, reductionFactor);
		chart.drawChart();
		if (selectionListener != null) {
			chart.addChartSelectionListener(selectionListener);
			chart.addChartClickListener(clickListener);
		}
		return chart;
	}

	/**
	 * Oppfrisker bildet med evt. nye grenser for y-akser
	 */
	public void refresh() {
        removeAllComponents();
        addComponent(createChart());
	}
	
	public void setYaxisRanges(YaxisRanges ranges) {
		data.setYaxisRanges(ranges);
	}

	public void setPlotTypes(Map<String, PlotType> plotTypes) {
		data.setPlotTypes(plotTypes);
	}

	public void showLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}

	public void setShowTooltip(boolean showTooltip) {
		this.showTooltip = showTooltip;
	}

	/**
	 * @return true if chart is zoomed
	 */
	public boolean isZoom() {
		return zoom;
	}
	
	private static String getText(String key) {
		return ApplicationResourceBundle.getInstance("vaadin-core").getString("chart." + key);
	}

	public void changeTimePeriod(long fromTimeDiff, long toTimeDiff) {
		if (zoomListener != null) {
			long fromDate = this.data.getMinDate().getTime();
			long toDate = this.data.getMaxDate().getTime();
			fromDate += fromTimeDiff;
			toDate += toTimeDiff;
			VaadinChartPanel.this.data = zoomListener.loadNewData(new Date(fromDate), new Date(toDate));
			setYaxisRanges(yaxisRanges);
			setPlotTypes(plotTypes);
			zoom = true;
			refresh();
		}
	}

	public boolean isChangeTimePeriodSupported() {
		return zoomListener != null;
	}
}