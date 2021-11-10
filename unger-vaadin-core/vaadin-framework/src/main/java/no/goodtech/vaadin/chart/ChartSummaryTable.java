package no.goodtech.vaadin.chart;

import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Table;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

import java.util.List;

/**
 * Shows min, max and average values for each series in a chart
 */
public class ChartSummaryTable extends Table implements IChartControl {

	public static final String NUM_VALUES = "numValues";
	public static final String MAX_VALUE = "maxValue";
	public static final String AVG_VALUE = "avgValue";
	public static final String MIN_VALUE = "minValue";
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String COLOR = "color";
	public static final String PLOT_TYPE = "plotType";

	
	public ChartSummaryTable(List<? extends TimeSeries> series) {
		if (series != null) {
			final BeanItemContainer<? extends TimeSeries> container = new BeanItemContainer<TimeSeries>(TimeSeries.class, series);
			setContainerDataSource(container);
			setPageLength(series.size());
			
			final ColumnGenerator generatedColumn = new ColorColumnGenerator(COLOR);
			addGeneratedColumn(COLOR, generatedColumn);
	
	//		for (Object propertyId : container.getContainerPropertyIds()) {
	//			final Class<?> type = container.getType(propertyId);
	//			if (Number.class.isAssignableFrom(type))
	//        		setColumnAlignment(propertyId, Align.RIGHT);
	//		}
			
			setVisibleColumns(COLOR, MIN_VALUE, AVG_VALUE, MAX_VALUE, NUM_VALUES, NAME, ID, PLOT_TYPE);
			for (Object column : getVisibleColumns())
				setColumnHeader(column, getText("column." + (String) column));
	
			setColumnExpandRatio(NAME, 1);
			setSelectable(false);
			setWidth("100%");
		}
	}
	
	private static String getText(String key) {
		return ApplicationResourceBundle.getInstance("vaadin-core").getString("chart.summaryTable." + key);
	}

	public void setChart(DynamicChart chart) {
		// TODO Auto-generated method stub
		
	}
}
