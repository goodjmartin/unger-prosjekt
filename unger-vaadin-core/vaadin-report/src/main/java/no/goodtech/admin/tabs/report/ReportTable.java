package no.goodtech.admin.tabs.report;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.Table;
import no.goodtech.admin.report.ColumnType;
import no.goodtech.admin.report.TypeType;

import java.math.RoundingMode;
import java.text.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportTable extends Table {

    private static final DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS = new DecimalFormatSymbols();
    private static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DecimalFormat DEFAULT_DECIMAL_FORMAT;
    private static final DecimalFormat DEFAULT_INTEGER_FORMAT;

    static {
        DECIMAL_FORMAT_SYMBOLS.setGroupingSeparator(' ');
        DEFAULT_INTEGER_FORMAT = new DecimalFormat("###,###,###,###", DECIMAL_FORMAT_SYMBOLS);
        DECIMAL_FORMAT_SYMBOLS.setDecimalSeparator(',');
        DEFAULT_DECIMAL_FORMAT = new DecimalFormat("###,###,###,###.00", DECIMAL_FORMAT_SYMBOLS);
        DEFAULT_DECIMAL_FORMAT.setRoundingMode(RoundingMode.HALF_UP);
    }

    private final Map<String, Format> formatMap = new HashMap<>();
    private final Map<String, Object> sums = new HashMap<>();


    public ReportTable(){

    }

    public ReportTable(final List<ColumnType> columnTypes) {
        setColumns(columnTypes);
    }

    public void setColumns(final List<ColumnType> columnTypes){
        if (columnTypes != null) {
            for (ColumnType column : columnTypes) {
                if (column.getType() != null) {
                    TypeType type = column.getType();

                    Format format = null;
                    if (type.equals(TypeType.JAVA_UTIL_DATE)) {
                        format = DEFAULT_DATE_FORMAT;
                        if (column.getFormat() != null) {
                            format = new SimpleDateFormat(column.getFormat());
                        }
                    } else if (type.equals(TypeType.JAVA_LANG_FLOAT) ||
                            type.equals(TypeType.JAVA_LANG_DOUBLE)) {
                        format = DEFAULT_DECIMAL_FORMAT;
                        if (column.getFormat() != null) {
                            format = new DecimalFormat(column.getFormat(), DECIMAL_FORMAT_SYMBOLS);
                            ((DecimalFormat)format).setRoundingMode(RoundingMode.HALF_UP);
                        }
                    } else if (type.equals(TypeType.JAVA_LANG_SHORT) ||
                            type.equals(TypeType.JAVA_LANG_INTEGER) ||
                            type.equals(TypeType.JAVA_LANG_LONG) ||
                            type.equals(TypeType.JAVA_LANG_NUMBER)) {
                        format = DEFAULT_INTEGER_FORMAT;
                        if (column.getFormat() != null) {
                            format = new DecimalFormat(column.getFormat(), DECIMAL_FORMAT_SYMBOLS);
                        }
                    }

                    if (format != null) {
                        formatMap.put(column.getName(), format);
                    }
                }
            }
        }
    }

    protected String formatPropertyValue(Object rowId, Object colId, Property property) {
        Format format = formatMap.get((String)colId);

        if ((format != null) && (property.getValue() != null)) {
            return format.format(property.getValue());
        }

        return super.formatPropertyValue(rowId, colId, property);
    }

    /**
     * Format a cell value according to format given in report template
     */
    public String format(Object value, String column) {
        Format format = formatMap.get(column);
        if (value == null)
        	return "";
        if (format == null)
        	return value.toString();

        return format.format(value);
    }
}
