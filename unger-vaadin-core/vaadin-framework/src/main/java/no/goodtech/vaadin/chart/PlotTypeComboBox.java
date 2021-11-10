package no.goodtech.vaadin.chart;

import com.vaadin.server.FontAwesome;
import com.vaadin.v7.ui.ComboBox;
import no.goodtech.vaadin.chart.TimeSeries.PlotType;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.utils.Utils;

/**
 * Provides single selection of plot types
 */
public class PlotTypeComboBox extends ComboBox
{
	public PlotTypeComboBox() {
		setNullSelectionAllowed(false);
		for (PlotType type : PlotType.values()) {
			addItem(type);
			final String name = type.name();
			setItemCaption(type, getText(name));
			final FontAwesome icon = Utils.findIcon(getText(name + ".icon"));
			if (icon != null)
				setItemIcon(type, icon);

		}
	}
	
	private String getText(String key) {
		return ApplicationResourceBundle.getInstance("vaadin-core").getString("chart.plotType." + key);
	}
}
