package no.goodtech.admin.tabs.report;

import no.goodtech.admin.report.ColumnType;
import no.goodtech.admin.report.TypeType;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class ExcelExporter {
	private static final Logger logger = LoggerFactory.getLogger(ExcelExporter.class);

	public byte[] create(final List<ColumnType> columns, final List<List<?>> rows) {

		Map<String, CellStyle> CELL_STYLE_MAP = new HashMap<String, CellStyle>();

		// Create an output stream
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try {
			// Create a work book
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet worksheet = workbook.createSheet("Worksheet");

			// Lag en CreationHelper for å lage et "yyyy-MM-dd HH:mm:ss" format til å formatere dato-cellen
			CreationHelper createHelper = workbook.getCreationHelper();

			// Create a default date cell style
			CellStyle defaultCellStyle = workbook.createCellStyle();
			defaultCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
			CELL_STYLE_MAP.put("yyyy-MM-dd HH:mm:ss", defaultCellStyle);

			// Create the header row
			XSSFRow headerRow = worksheet.createRow(0);
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
			int cellCounter = 0;
			for (ColumnType column : columns) {
				XSSFCell cell = headerRow.createCell(cellCounter++);
				cell.setCellValue(column.getName());
				cell.setCellStyle(headerCellStyle);

				// Add additional date formats if specified (max 4000 calls to createCellStyle() is allowed)
				if ((column.getType() != null) && column.getType().equals(TypeType.JAVA_UTIL_DATE) && (column.getFormat() != null)) {
					if (CELL_STYLE_MAP.get(column.getFormat()) == null) {
						CellStyle cellStyle = workbook.createCellStyle();
						cellStyle.setDataFormat(createHelper.createDataFormat().getFormat(column.getFormat()));
						CELL_STYLE_MAP.put(column.getFormat(), cellStyle);
					}
				}
			}

			// Create data rows
			int rowCount = 1;
			for (List<?> columnValues : rows) {
				// Create a new row
				XSSFRow excelRow = worksheet.createRow(rowCount++);

				cellCounter = 0;
				for (Object columnValue : columnValues) {
					ColumnType column = columns.get(cellCounter);

					// Create new cell in row
					if (columnValue == null) {
						excelRow.createCell(cellCounter++).setCellValue((String) columnValue);
					} else if (column.getType() != null) {
						if (column.getType().equals(TypeType.JAVA_LANG_BOOLEAN)) {
							excelRow.createCell(cellCounter++).setCellValue(Boolean.parseBoolean(columnValue.toString()));
						} else if (column.getType().equals(TypeType.JAVA_LANG_BYTE)) {
							excelRow.createCell(cellCounter++).setCellValue(Byte.parseByte(columnValue.toString()));
						} else if (column.getType().equals(TypeType.JAVA_LANG_SHORT)) {
							excelRow.createCell(cellCounter++).setCellValue(Short.parseShort(columnValue.toString()));
						} else if (column.getType().equals(TypeType.JAVA_LANG_INTEGER)) {
							excelRow.createCell(cellCounter++).setCellValue(Integer.parseInt(columnValue.toString()));
						} else if (column.getType().equals(TypeType.JAVA_LANG_LONG)) {
							excelRow.createCell(cellCounter++).setCellValue(Long.parseLong(columnValue.toString()));
						} else if (column.getType().equals(TypeType.JAVA_LANG_NUMBER)) {
							excelRow.createCell(cellCounter++).setCellValue(Integer.parseInt(columnValue.toString()));
						} else if (column.getType().equals(TypeType.JAVA_LANG_FLOAT)) {
							excelRow.createCell(cellCounter++).setCellValue(Float.parseFloat(columnValue.toString()));
						} else if (column.getType().equals(TypeType.JAVA_LANG_DOUBLE)) {
							excelRow.createCell(cellCounter++).setCellValue(Double.parseDouble(columnValue.toString()));
						} else if (column.getType().equals(TypeType.JAVA_LANG_STRING)) {
							excelRow.createCell(cellCounter++).setCellValue(columnValue.toString());
						} else if (column.getType().equals(TypeType.JAVA_UTIL_DATE)) {
							// Create and set cell value
							XSSFCell cell = excelRow.createCell(cellCounter++);

							// Set cell style
							cell.setCellStyle((column.getFormat() != null) ? CELL_STYLE_MAP.get(column.getFormat()) : defaultCellStyle);

							// Set cell value
							cell.setCellValue((Date) columnValue);
						}
					} else {
						excelRow.createCell(cellCounter++).setCellValue(columnValue.toString());
					}
				}
			}

			// Write buffer to woorkbook
			byteArrayOutputStream.flush();
			workbook.write(byteArrayOutputStream);
		} catch (IOException e) {
			logger.warn("ExcelExporter: message=" + e.getMessage() + "\n" + ", stackTrace=" + Arrays.toString(e.getStackTrace()));
		} catch (RuntimeException e) {
			logger.warn("RuntimeException: message=" + e.getMessage() + "\n" + ", stackTrace=" + Arrays.toString(e.getStackTrace()));
		}

		return byteArrayOutputStream.toByteArray();
	}
}
