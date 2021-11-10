package no.goodtech.admin.tabs.report;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.IndexedContainer;
import no.goodtech.admin.report.ColumnType;
import no.goodtech.admin.report.TypeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ReportList {

	private static final Logger logger = LoggerFactory.getLogger(ReportList.class);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static IndexedContainer getContainer(final List<ColumnType> columns, final List<List<?>> rows) {

		IndexedContainer container = new IndexedContainer();

        try {
            // Set the column names
            for (ColumnType column : columns) {
                String columnType = (column.getType() != null) ? column.getType().value() : "java.lang.String";
                Class<?> clazz = Class.forName(columnType);
                container.addContainerProperty(column.getName(), clazz, null);
            }

            // Set the column values
            Integer itemId = 0;
            for (List<?> row : rows) {
                int columnCount = 0;
                Item item = null;
                for (ColumnType column : columns) {
                    if (item == null) {
                        item = container.addItem(itemId++);
                    }
					final Object columnValue = row.get(columnCount);

					// Create new cell in row
					final Property itemProperty = item.getItemProperty(column.getName());
					if (columnValue == null) {
						itemProperty.setValue(null);
					} else if (column.getType() != null) {
						if (column.getType().equals(TypeType.JAVA_LANG_BOOLEAN)) {
							itemProperty.setValue(Boolean.parseBoolean(columnValue.toString()));
						} else if (column.getType().equals(TypeType.JAVA_LANG_BYTE)) {
							itemProperty.setValue(Byte.parseByte(columnValue.toString()));
						} else if (column.getType().equals(TypeType.JAVA_LANG_SHORT)) {
							itemProperty.setValue(Short.parseShort(columnValue.toString()));
						} else if (column.getType().equals(TypeType.JAVA_LANG_INTEGER)) {
							itemProperty.setValue(Integer.parseInt(columnValue.toString()));
						} else if (column.getType().equals(TypeType.JAVA_LANG_LONG)) {
							itemProperty.setValue(Long.parseLong(columnValue.toString()));
						} else if (column.getType().equals(TypeType.JAVA_LANG_NUMBER)) {
							itemProperty.setValue(Integer.parseInt(columnValue.toString()));
						} else if (column.getType().equals(TypeType.JAVA_LANG_FLOAT)) {
							itemProperty.setValue(Float.parseFloat(columnValue.toString()));
						} else if (column.getType().equals(TypeType.JAVA_LANG_DOUBLE)) {
							itemProperty.setValue(Double.parseDouble(columnValue.toString()));
						} else if (column.getType().equals(TypeType.JAVA_LANG_STRING)) {
							itemProperty.setValue(columnValue.toString());
						} else if (column.getType().equals(TypeType.JAVA_UTIL_DATE)) {
							itemProperty.setValue(columnValue);
						}
					} else {
						itemProperty.setValue(columnValue.toString());
					}
                    columnCount++;
                }
            }
        } catch (ClassNotFoundException e) {
            logger.error("ClassNotFoundException - " + e.getMessage());
        }

        return container;
	}

}
