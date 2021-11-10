package no.goodtech.vaadin.chart;

import java.awt.Color;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class JFreeChartBuilder {

	public JFreeChart buildChart(final TimeSeriesChartData chartData, String title, boolean showLegend) {
        JFreeChart chart = null;
        int axisNumber = 0; //For å telle y-akser
        for (Map.Entry<String, Yaxis> axisEntry : chartData.getYaxes().entrySet()) {
    		int seriesNumber = 0;
            String unitOfMeasure = axisEntry.getKey();
            TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();

            //for alle egenskaper med gitt måleenhet opprett en ny tidsserie med egenskapens id
            final Yaxis yAxis = axisEntry.getValue();
            XYItemRenderer renderer = new StandardXYItemRenderer();
            for (Map.Entry<String, no.goodtech.vaadin.chart.TimeSeries> seriesEntry : yAxis.getSeries().entrySet()) {
                final String seriesName = seriesEntry.getKey();
				TimeSeries timeSeries = new TimeSeries(seriesName);
				timeSeries.setKey(seriesName);
                for (SampleValue sampleValue : seriesEntry.getValue().getValues())
                    timeSeries.addOrUpdate(new Millisecond(sampleValue.getTime()), sampleValue.getValue());
                timeSeriesCollection.addSeries(timeSeries);
				final com.vaadin.addon.charts.model.style.Color color = seriesEntry.getValue().getColor();
				if (color != null) {
					final java.awt.Color awtColor = Color.decode(color.toString());
					renderer.setSeriesPaint(seriesNumber, awtColor);
				}
				seriesNumber++;
            }
            if (axisNumber == 0) {
                //Hvis det er første akse, opprett diagrammet
                chart = createChart(title, unitOfMeasure, timeSeriesCollection, yAxis, showLegend);
                chart.getXYPlot().setBackgroundPaint(Color.WHITE);
                chart.getXYPlot().setRangeGridlinePaint(Color.DARK_GRAY);
                chart.getXYPlot().setDomainGridlinePaint(Color.DARK_GRAY);
                chart.getXYPlot().setRenderer(renderer);
           
            } else {
                // Hvis et ikke er første akse, opprett en ny akse og knytt denne tidsserien til aksen.
                NumberAxis newXaxis = new NumberAxis(unitOfMeasure);
                newXaxis.setAutoRangeIncludesZero(false);
                XYPlot plot = chart.getXYPlot();
                plot.setRangeAxis(axisNumber, newXaxis);
                plot.setRangeAxisLocation(axisNumber, AxisLocation.BOTTOM_OR_LEFT);
                plot.setDataset(axisNumber, timeSeriesCollection);
                plot.mapDatasetToRangeAxis(axisNumber, axisNumber);
                plot.setRenderer(axisNumber, renderer);
                setRange(yAxis, axisNumber, chart);
            }
            axisNumber++; //Legg 1 for unitOfMeasureCounter for å indikere at det skal legges til en ny akse for neste måleenhet
        }
        return chart;
    }

    private JFreeChart createChart(String title, String unitOfMeasure, TimeSeriesCollection timeSeriesCollection, Yaxis yAxis, boolean showLegend) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(null, // Title
                "Tidspunkt", // x-akse Label
                unitOfMeasure, // y-akse Label
                timeSeriesCollection, // Dataset
                showLegend, // Vis fargekodet linje beskriuvelse
                false, // Tooltips
                false // URL
        );
        chart.setTitle(title);
        setRange(yAxis, 0, chart);
        return chart;
    }

    private void setRange(final Yaxis yAxis, int axisNumber, JFreeChart chart) {
        XYPlot plot = chart.getXYPlot();
        ValueAxis domainAxis = plot.getRangeAxis(axisNumber);

        Double minY = yAxis.getMinLimit();
        Double maxY = yAxis.getMaxLimit();
        if (minY == null && maxY == null) {
            domainAxis.setAutoRange(true);
        } else {
            //hvis bare den ene grensen er satt må vi beregne den andre sjøl, fordi JFreeChart må ha både øvre og nedre grense satt
            if (minY == null)
                minY = yAxis.getMinValue();
            if (maxY == null)
                maxY = yAxis.getMaxValue();
            if (minY == null || maxY == null)
                domainAxis.setAutoRange(true);
            else
                domainAxis.setRange(minY, maxY);
        }
    }
}
