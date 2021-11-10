package no.goodtech.vaadin.chart;

import com.vaadin.server.Page;
import com.vaadin.ui.VerticalLayout;
import no.goodtech.vaadin.chart.YaxisRanges.MinMaxValue;
import org.jfree.chart.JFreeChart;
import org.vaadin.addon.JFreeChartWrapper;

/**
 * Diagram som kan vise flere y-akser.
 * Diagrammet fyller hele skjermen
 * Er basert på {@link JFreeChartWrapper}
 */
public class JfreeChartPanel extends VerticalLayout implements DynamicChart {
    private final JFreeChartBuilder jFreeChartBuilder = new JFreeChartBuilder();
    private final TimeSeriesChartData chartData;
    private final String title;
    private boolean showLegend;
	private JFreeChart chart;
    
    /**
     * For hver måleenhet, opprett en ny tidsserie, legg til alle måleverdier med måleenheten til denne tidsserien.
     * Legg etter dette denne tidsserien til chartet.
     *
     * @param chartData data som skal vises i grafen
     * @param title     overskrift
     */
    public JfreeChartPanel(final TimeSeriesChartData chartData, String title, boolean showLegend) {
        this.chartData = chartData;
        this.title = title;
        this.showLegend = showLegend;
        setSizeFull();
		setMargin(false);
		setSpacing(false);
		refresh();
	}

    public void refresh() {
        chart = jFreeChartBuilder.buildChart(chartData, title, showLegend);
        removeAllComponents();
        if (chart != null) {
            chart.setAntiAlias(false);
            JFreeChartWrapper wrapper = new JFreeChartWrapper(chart, JFreeChartWrapper.RenderingMode.PNG) {
            	
            	/**
            	 * Workaround to make this work in vaadin 7.3.9 and newer, seee
            	 * https://vaadin.com/forum#!/thread/8366526/9212257
            	 */
                @Override
                public void attach() {
                    super.attach();
                    setResource("src", getSource());
                }
            };
            
            //Hent høyde og bredde fra nettleseren
            int width = Page.getCurrent().getBrowserWindowWidth();
            int height = Page.getCurrent().getBrowserWindowHeight();
            wrapper.setGraphWidth(width);
            wrapper.setGraphHeight(height);
            addComponent(wrapper);
            wrapper.setSizeFull();
        }
    }

    public void setYaxisRanges(YaxisRanges ranges) {
        for (String axis : chartData.getYaxes().keySet()) {
            final MinMaxValue minMaxValue = ranges.getAlways(axis);
            chartData.setYaxisRange(axis, minMaxValue.getMinValue(), minMaxValue.getMaxValue());
        }
    }

	public void showLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}

	public void setShowTooltip(boolean showTooltip) {
		//n/a in picture
	}

	public void changeTimePeriod(long fromTimeDiff, long toTimeDiff) {
		//TODO: Not supported yet
	}

	public boolean isChangeTimePeriodSupported() {
		return false;
	}
}

