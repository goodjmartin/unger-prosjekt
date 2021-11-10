package no.goodtech.vaadin.chart;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;
import no.goodtech.vaadin.buttons.ResetButton;
import no.goodtech.vaadin.buttons.SaveButton;
import no.goodtech.vaadin.chart.YaxisRanges.MinMaxValue;
import no.goodtech.vaadin.chart.YaxisRanges.RangeRefresher;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Brukes for å angi min- og maks-grenser for aktuelle y-akser.
 * Legg til en ny akse med {@link #addAxis(String)}
 * Tegn panelet på nytt med {@link #refresh()}
 * Begrens hvilke akser du vil se med {@link #setVisibleAxes(Collection)}
 */
public class YaxisLimitPanel extends VerticalLayout {

	private final GridLayout fieldLayout;
	private final YaxisRanges yaxisRanges;
	private Set<String> visibleAxes = new HashSet<String>();
	private final TimeSeriesChartData data;
	private final RangeRefresher rangeRefresher;
	
	private static String getText(String key) {
		return ApplicationResourceBundle.getInstance("vaadin-core").getString(key);
	}

	/**
	 * Opprett og vis panel
	 * @param data punkter som skal vises i diagrammet
	 * @param yaxisRanges evt. min-maks-grenser som er bestemt på forhånd
	 * @param rangeRefresher gir beskjed via denne hvis bruker har endret grensene
	 */
	public YaxisLimitPanel(final TimeSeriesChartData data, YaxisRanges yaxisRanges, RangeRefresher rangeRefresher) {
		this.data = data; 
		this.yaxisRanges = new YaxisRanges();
		this.rangeRefresher = rangeRefresher;

		setSizeFull();

		//3 kolonner(Måleenhet, Min Maks) , 1 rad(øker etterhvert)
		fieldLayout = new GridLayout(3, 1);
		fieldLayout.setMargin(false);
		//fieldLayout.setImmediate(true);
		fieldLayout.setSizeFull();
		addComponent(fieldLayout);
	}

	/**
	 * Oppfrisk panel med en ny akse. Hvis aksen ikke finnes fra før, blir den lagt til
	 * @param axisName navn på aksen
	 */
	public void addAxis(String axisName) {
		yaxisRanges.add(axisName);
		if (!data.getYaxes().containsKey(axisName))
			data.setYaxisRange(axisName, null, null);
	}

	/**
	 * Tegner skjermbildet på nytt iht. data som ligger i yaxisRanges
	 */
	public void refresh() {
		fieldLayout.removeAllComponents();
		for (String axisName : visibleAxes)
			addRow(axisName, yaxisRanges.getAlways(axisName));
	}
	
	/**
	 * Vis panelet i en dialogboks
	 * @param chart vil oppdatere denne hvis man velger å lagre grenseverdiene
	 */
	public void popup(final DynamicChart chart) {
		refresh();
		Button saveButton = new SaveButton(new SaveButton.ISaveListener() {
			public void saveClicked() {
				for (String axis : visibleAxes) {
					//lagrer grenseverdiene slik at grafen kan oppdateres
					final MinMaxValue range = yaxisRanges.get(axis);
					data.getYaxes().get(axis).setMinLimit(range.getMinValue());
					data.getYaxes().get(axis).setMaxLimit(range.getMaxValue());
					rangeRefresher.pleaseRefresh(axis, range);
				}
				chart.setYaxisRanges(yaxisRanges);
				chart.refresh();
				
			}
		});
		Button resetButton = new ResetButton(new ResetButton.IResetListener() {
			public void resetClicked() {
				for (String axis : visibleAxes)
					yaxisRanges.set(axis, new MinMaxValue(null, null));
				refresh();			
			}
		});
		HorizontalLayout buttonLayout = new HorizontalLayout(resetButton, saveButton);
		buttonLayout.setMargin(false);
		VerticalLayout content = new VerticalLayout(fieldLayout, buttonLayout);
		content.setSizeUndefined();
		if (fieldLayout.getComponentCount() > 1) {
			final Component component = fieldLayout.getComponent(1,  0);
			((TextField) component).focus();
		}
			
		Window subWindow = new Window(getText("chart.yaxislimits.title"));
		subWindow.setContent(content);
        UI.getCurrent().addWindow(subWindow);
	}
			
	private void addRow(String axis, MinMaxValue minMaxValue) {
		BeanFieldGroup<MinMaxValue> binder = new BeanFieldGroup<MinMaxValue>(MinMaxValue.class);
		binder.setItemDataSource(minMaxValue);
		binder.setBuffered(false);

		String axisCaption = null;
		String minCaption = null;
		String maxCaption = null;
		if (fieldLayout.getComponentCount() == 0) {
			//vis kun overskrift i første rad
			axisCaption = getText("chart.axis");
			minCaption = getText("chart.min");
			maxCaption = getText("chart.max");
		}
		final TextField axisField = new TextField(axisCaption, axis);
		binder.buildAndBind(minCaption, "minValue");
		binder.buildAndBind(maxCaption, "maxValue");

		axisField.setReadOnly(true);		
		axisField.setNullRepresentation("Ukjent");
		axisField.setWidth("100%");
		fieldLayout.addComponent(axisField);
		
		for (Field<?> field : binder.getFields()) {
			//((TextField) field).setNullRepresentation("");
			field.setWidth("100%");
			fieldLayout.addComponent(field);
		}
	}

	/**
	 * @return hvilke akser som er synlige
	 */
	public Collection<String> getVisibleAxes() {
		return visibleAxes;
	}
	
	/**
	 * Angi hvilke akser du vil se
	 * @param visibleAxes navn på aksene du vil se
	 */
	public void setVisibleAxes(Collection<String> visibleAxes) {
		this.visibleAxes.clear();
		this.visibleAxes.addAll(visibleAxes);
		for (String axis : visibleAxes)
			addAxis(axis);
	}
}
