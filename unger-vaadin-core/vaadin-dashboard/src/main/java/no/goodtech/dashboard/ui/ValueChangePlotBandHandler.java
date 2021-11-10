package no.goodtech.dashboard.ui;

import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.Label;
import com.vaadin.addon.charts.model.PlotBand;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.addon.charts.util.Util;
import no.goodtech.dashboard.config.ui.SeriesConfig;
import no.goodtech.dashboard.model.SampleDTO;
import no.goodtech.vaadin.chart.ColorUtils;

import java.util.*;

/**
 * Creates plot bands for a chart.
 * A plot band series is a series where we draw a plot vertical band whenever the value changes
 */
public class ValueChangePlotBandHandler {

	private final Configuration chart;
	private Map<SeriesConfig, SampleDTO> lastPlotBandValue = new HashMap<>();

	public ValueChangePlotBandHandler(Configuration chart) {
		this.chart = chart;
	}

	/**
	 * Render given series as plot bands. Draws a vertical band for each value change
	 */
	public void createBands(SeriesConfig seriesConfig, List<SampleDTO> dtos) {
		for (SampleDTO dto : dtos) {
			final SampleDTO lastPoint = lastPlotBandValue.get(seriesConfig);
			if (lastPoint != null && dto.getValue() != null && lastPoint.getValue() != null && lastPoint.getCreated().before(dto.getCreated())) {
				//if value changes
				if (Math.abs(dto.getValue() - lastPoint.getValue()) > 0.000001) {
					final PlotBand band = new PlotBand();
					band.setFrom(lastPoint.getCreated());
					band.setTo(dto.getCreated());
					band.setColor(ColorUtils.createColor(seriesConfig.getColor()));
					band.setId(seriesConfig.getId());
					//band.setLabel(new Label(String.format("%d > %d", lastPoint.getValue().intValue(), dto.getValue().intValue())));
					chart.getxAxis().addPlotBand(band);
				}

			}
			lastPlotBandValue.put(seriesConfig, dto);
		}
	}

	/**
	 * Remove plot bands for given series
	 * @param seriesConfig the series that we want to remove plot bands for
	 * @param startTime only remove bands older than startTime. If null, remove all bands
	 */
	public void removeBands(SeriesConfig seriesConfig, Date startTime) {
		final XAxis xAxis = chart.getxAxis();
		final PlotBand[] plotBands = xAxis.getPlotBands();
		for (PlotBand band : plotBands) {
			if (band.getId().equals(seriesConfig.getId())) {
				if (startTime != null) {
					if (Util.toServerInstant(band.getTo().doubleValue()).isBefore(startTime.toInstant())) {
						xAxis.removePlotBand(band); //whole band outside axis, remove this
					}
				} else {
					xAxis.removePlotBand(band); //remove all plot bands for this series
					lastPlotBandValue.remove(seriesConfig);
				}
			}
		}
	}
}
