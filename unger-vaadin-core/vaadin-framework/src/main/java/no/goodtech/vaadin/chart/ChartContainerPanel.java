package no.goodtech.vaadin.chart;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.Label;
import no.goodtech.vaadin.chart.YaxisRanges.RangeRefresher;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.utils.Utils;

import java.util.Date;

/**
 * Viser angitt tidslinje-diagram i samme vindu som applikasjonen
 * Har knapper for å justere grenser for y-akser
 */
public class ChartContainerPanel extends VerticalLayout {

	public static final String VIEW_ID = "hardeningChartPanel";
	private static final String LIMIT_BUTTON_CAPTION = getText("yaxislimits.button");
	private final Label fromTimeLabel = new Label(), toTimeLabel = new Label();
	
	/**
	 * Vis panelet med angitt grafisk innhold
	 */
	public ChartContainerPanel(){
		setSizeFull();
		setMargin(false);
	}

	/**
	 * Adds a chart to the panel
	 * @param data data som skal vises i grafen
	 * @param chart panelet som inneholder selve grafen
	 * @param settings user-specific settings
	 * @param rangeRefresher får beskjed via denne om grenser for y-akser blir endret av bruker
	 * @param tableLegend provide your own table if you like. If null, a standard {@link ChartSummaryTable} is used
	 */
	public void addChart(final TimeSeriesChartData data, final DynamicChart chart,
						 final ChartSettings settings, final RangeRefresher rangeRefresher, IChartControl tableLegend){

		IChartControl legend = tableLegend;
		if(legend == null)
			legend = new ChartSummaryTable(data.getSeries());

		legend.setChart(chart);
		HorizontalLayout tableLegendPane = new HorizontalLayout(legend);
		tableLegendPane.setWidth("100%");
		tableLegendPane.setVisible(settings.isShowTableLegend());

		chart.setSizeFull();
		addComponent(chart);
		addComponent(tableLegendPane);
		addComponent(createButtonPanel(tableLegendPane, data, chart, settings, rangeRefresher));
		setExpandRatio(chart, 1.0f);
		refreshTimePeriod(data.getMinDate(), data.getMaxDate());
	}
	
	private Layout createButtonPanel(final HorizontalLayout tableLegendPane, final TimeSeriesChartData data,
			final DynamicChart chart, final ChartSettings settings, final RangeRefresher rangeRefresher) {

		Button showAxisLimitsButton = new Button(LIMIT_BUTTON_CAPTION);
		showAxisLimitsButton.addClickListener(event -> {
			final YaxisLimitPanel limitPanel = new YaxisLimitPanel(data, settings.getYaxisRanges(), rangeRefresher);
			limitPanel.setVisibleAxes(data.getYaxes().keySet()); //vis bare grenser for aksene som brukes i grafen
			limitPanel.popup(chart);
		});

		CheckBox showStandardLegend = new CheckBox(getText("showStandardLegend"));
		showStandardLegend.setValue(settings.isShowStandardLegend());
		showStandardLegend.addValueChangeListener(event -> {
			final boolean show = (boolean) event.getProperty().getValue();
			settings.setShowStandardLegend(show);
			chart.showLegend(show);
			chart.refresh();
		});
		
		CheckBox showTableLegend = new CheckBox(getText("showTableLegend"));
		showTableLegend.setValue(settings.isShowTableLegend());
		showTableLegend.addValueChangeListener(event -> {
			final boolean show = (boolean) event.getProperty().getValue();
			settings.setShowTableLegend(show);
			tableLegendPane.setVisible(show);
		});
		
		CheckBox showTooltip = new CheckBox(getText("showTooltip"));
		showTooltip.setValue(settings.isShowTooltip());
		showTooltip.addValueChangeListener(event -> {
			final boolean show = (boolean) event.getProperty().getValue();
			settings.setShowTooltip(show);
			chart.setShowTooltip(show);
			chart.refresh();
		});

		final long shortStep = settings.getTimePeriodShortStep();
		final long longStep = settings.getTimePeriodLongStep();
		final HorizontalLayout backButtons = createBackButtons(shortStep, longStep, chart);
		final HorizontalLayout forwardButtons = createForwardButtons(shortStep, longStep, chart);
		final HorizontalLayout miscButtons = createButtonBar(showAxisLimitsButton, showStandardLegend, showTableLegend, showTooltip);
		
		final HorizontalLayout buttonPanel = createButtonBar(backButtons, miscButtons, forwardButtons);
		buttonPanel.setComponentAlignment(backButtons, Alignment.BOTTOM_LEFT);
		buttonPanel.setComponentAlignment(forwardButtons, Alignment.BOTTOM_RIGHT);
		buttonPanel.setComponentAlignment(miscButtons, Alignment.BOTTOM_CENTER);
		buttonPanel.setWidth("100%");
		return buttonPanel;
	}
	
	private HorizontalLayout createButtonBar(Component... buttons) {
		final HorizontalLayout bar = new HorizontalLayout(buttons);
		bar.setMargin(false);
		return bar;
	}
	
	private HorizontalLayout createBackButtons(long shortStep, long longStep, DynamicChart chart) {
		Button extendShortStep = createTimeTravelButton("+", "go.backward", -shortStep, 0, chart);
		Button extendLongStep = createTimeTravelButton("+", "go.backward", -longStep, 0, chart);
		Button pan = createTimeTravelButton("<<", "pan.backward", -shortStep, -shortStep, chart);		
		return createButtonBar(extendLongStep, extendShortStep, pan, fromTimeLabel);
	}
	
	private HorizontalLayout createForwardButtons(long shortStep, long longStep, DynamicChart chart) {
		Button extendShortStep = createTimeTravelButton("+", "go.forward", 0, shortStep, chart);
		Button extendLongStep = createTimeTravelButton("+", "go.forward", 0, longStep, chart);
		Button pan = createTimeTravelButton(">>", "pan.forward", shortStep, shortStep, chart);		
		return createButtonBar(toTimeLabel, pan, extendShortStep, extendLongStep);
	}
	
	private Button createTimeTravelButton(String caption, String tooltipKey, long fromTimeDiff, long toTimeDiff, 
			final DynamicChart chart) {

		long diff = fromTimeDiff;
		if (diff == 0)
			diff = toTimeDiff;

		final String duration = Utils.formatDurationAsText(Math.abs(diff));
		final Button button = new Button(caption + " " + duration);
		button.setDescription(getText(tooltipKey + ".tooltip") + " " + duration);
		if (chart.isChangeTimePeriodSupported())
			button.addClickListener(event -> chart.changeTimePeriod(fromTimeDiff, toTimeDiff));
		else
			button.setEnabled(false);
		return button;
	}

	private static String getText(String key) {
		return ApplicationResourceBundle.getInstance("vaadin-core").getString("chart." + key);
	}
	
	public void refreshTimePeriod(Date fromTime, Date toTime) {
		fromTimeLabel.setValue(Utils.DATETIME_FORMATTER.format(fromTime));
		toTimeLabel.setValue(Utils.DATETIME_FORMATTER.format(toTime));
	}
}
