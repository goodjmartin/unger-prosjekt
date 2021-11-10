package no.goodtech.admin.tabs.report;

//import com.vaadin.addon.charts.model.style.SolidColor;
//import com.vaadin.addon.timeline.Timeline;
//import com.vaadin.data.*;
//import com.vaadin.v7.data.Container;
//import com.vaadin.v7.data.util.IndexedContainer;
//import no.goodtech.admin.report.ReportType;
//import no.goodtech.admin.report.UnitType;
//import no.goodtech.admin.report.ZoomFactorType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.awt.*;
//import java.util.*;
//import java.util.List;
@Deprecated
public class TimeLinePanel { //} extends Timeline {

//    private static final Logger logger = LoggerFactory.getLogger(TimeLinePanel.class);
//    private static final Map<UnitType, Long> UNIT_FACTOR_MAP = new HashMap<UnitType, Long>();
//
//    static {
//        UNIT_FACTOR_MAP.put(UnitType.MILLISECOND, 1L);
//        UNIT_FACTOR_MAP.put(UnitType.SECOND, 1000L);
//        UNIT_FACTOR_MAP.put(UnitType.MINUTE, 60 * 1000L);
//        UNIT_FACTOR_MAP.put(UnitType.HOUR, 60 * 60 * 1000L);
//        UNIT_FACTOR_MAP.put(UnitType.DAY, 24 * 60 * 60 * 1000L);
//        UNIT_FACTOR_MAP.put(UnitType.WEEK, 7 * 24 * 60 * 60 * 1000L);
//        UNIT_FACTOR_MAP.put(UnitType.MONTH, 30 * 24 * 60 * 60 * 1000L);
//        UNIT_FACTOR_MAP.put(UnitType.YEAR, 365 * 60 * 60 * 1000L);
//    }
//
//    public TimeLinePanel(final ReportType report, final List rows) {
//        setSizeFull();
//        setChartMode(ChartMode.LINE);
//        setCaption(report.getName());
//
//        // Add the zoom levels
//        for (ZoomFactorType zoomFactor : report.getTimeLineExporter().getZoomFactor()) {
//            addZoomLevel(zoomFactor.getLegend(), zoomFactor.getTimeInterval() * UNIT_FACTOR_MAP.get(zoomFactor.getUnit()));
//        }
//        setZoomLevelsVisible(true);
//        setGraphGridColor(SolidColor.BLACK);
//
//        Object[] columns = (Object[])rows.get(0);
//        for (int column = 1; column < columns.length; column++) {
//            Container.Indexed indexedContainer = createContainer(rows, column);
//            addGraphDataSource(indexedContainer);
//            setGraphLegend(indexedContainer, report.getColumns().getColumn().get(column).getName());
//            setGraphFillColor(indexedContainer, null);
//            Color col = Color.decode(report.getTimeLineExporter().getSeriesList().getSeries().get(column - 1).getColor());
//			SolidColor color = new SolidColor(col.getRed(),col.getGreen(),col.getBlue());
//            setGraphOutlineColor(indexedContainer, color);
//            setBrowserFillColor(indexedContainer, null);
//            setBrowserOutlineColor(indexedContainer, color);
//            setVerticalAxisLegendUnit(indexedContainer, report.getTimeLineExporter().getSeriesList().getSeries().get(column - 1).getUnit());
//        }
//
//        setGraphCapsVisible(true);
//        selectFullRange();
//    }
//
//    private Container.Indexed createContainer(final List rows, int column) {
//        // Create the container
//        IndexedContainer container = new IndexedContainer();
//        container.addContainerProperty(PropertyId.TIMESTAMP, Date.class, null);
//        container.addContainerProperty(PropertyId.VALUE, Float.class, 0);
//
//        // Set the column values
//        for (Object row : rows) {
//            Object[] columnValues = (Object[]) row;
//            Date date = (Date)columnValues[0];
//            Item item = container.addItem(date);
//
//            if (item != null) {
//                item.getItemProperty(PropertyId.TIMESTAMP).setValue(date);
//                item.getItemProperty(PropertyId.VALUE).setValue(columnValues[column]);
//            } else {
//                logger.error("Unable to add item");
//            }
//        }
//
//        return container;
//    }

}
