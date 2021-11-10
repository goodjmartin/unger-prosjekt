package no.goodtech.admin.tabs.report;

import no.goodtech.admin.report.ColumnType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

public class CSVFile {

	private static final Logger logger = LoggerFactory.getLogger(CSVFile.class);

	public static byte[] create(final List<ColumnType> columns, final List<List<?>> rows) {
		if (logger.isDebugEnabled()) {
			logger.debug("enter - rows=" + rows);
		}

		StringBuilder stringBuilder = new StringBuilder();

		// Set the column names
		Iterator<ColumnType> columnIterator = columns.iterator();
		while (columnIterator.hasNext()) {
			stringBuilder.append("'").append(((ColumnType)columnIterator.next()).getName()).append("'");
			if (columnIterator.hasNext()) {
				stringBuilder.append(",");
			} else {
				stringBuilder.append("\n");
			}
		}

		// Set the column values
		for (List<?> columnValues : rows) {
			int index = 0;
			for (Object columnValue : columnValues) {
				stringBuilder.append("'").append(columnValue).append("'");
				if (++index < columnValues.size()) {
					stringBuilder.append(",");
				} else {
					stringBuilder.append("\n");
				}
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("leave");
		}

		return stringBuilder.toString().getBytes();
	}
}
