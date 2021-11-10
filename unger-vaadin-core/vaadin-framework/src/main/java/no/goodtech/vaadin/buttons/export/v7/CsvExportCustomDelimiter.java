package no.goodtech.vaadin.buttons.export.v7;


import com.vaadin.addon.tableexport.v7.CsvExport;
import com.vaadin.v7.ui.Table;
import no.goodtech.vaadin.buttons.export.XLS2CSVmraCustom;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

@Deprecated
public class CsvExportCustomDelimiter extends CsvExport {
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CsvExportCustomDelimiter.class);

	private char delimiter = ';';

	public CsvExportCustomDelimiter(Table table) {
		super(table);
	}

	public CsvExportCustomDelimiter(Table table, String sheetName) {
		super(table, sheetName);
	}

	public CsvExportCustomDelimiter(Table table, String sheetName, String reportTitle) {
		super(table, sheetName, reportTitle);
	}

	public CsvExportCustomDelimiter(Table table, String sheetName, String reportTitle, String exportFileName) {
		super(table, sheetName, reportTitle, exportFileName);
	}

	public CsvExportCustomDelimiter(Table table, String sheetName, String reportTitle, String exportFileName, boolean hasTotalsRow) {
		super(table, sheetName, reportTitle, exportFileName, hasTotalsRow);
	}

//	public CsvExportCustomDelimiter(TableHolder tableHolder) {
//		super(tableHolder);
//	}
//
//	public CsvExportCustomDelimiter(TableHolder tableHolder, String sheetName) {
//		super(tableHolder, sheetName);
//	}
//
//	public CsvExportCustomDelimiter(TableHolder tableHolder, String sheetName, String reportTitle) {
//		super(tableHolder, sheetName, reportTitle);
//	}
//
//	public CsvExportCustomDelimiter(TableHolder tableHolder, String sheetName, String reportTitle, String exportFileName) {
//		super(tableHolder, sheetName, reportTitle, exportFileName);
//	}
//
//	public CsvExportCustomDelimiter(TableHolder tableHolder, String sheetName, String reportTitle, String exportFileName, boolean hasTotalsRow) {
//		super(tableHolder, sheetName, reportTitle, exportFileName, hasTotalsRow);
//	}

	@Override
	/**
	 * Convert Excel to CSV and send to user.
	 * This override uses a special delimiter. Default is ';'
	 */
	public boolean sendConverted() {
		File tempXlsFile, tempCsvFile;
		try {
			tempXlsFile = File.createTempFile("tmp", ".xls");
			final FileOutputStream fileOut = new FileOutputStream(tempXlsFile);
			workbook.write(fileOut);
			final FileInputStream fis = new FileInputStream(tempXlsFile);
			final POIFSFileSystem fs = new POIFSFileSystem(fis);
			tempCsvFile = File.createTempFile("tmp", ".csv");
			final PrintStream p =
					new PrintStream(new BufferedOutputStream(
							new FileOutputStream(tempCsvFile, true)));

			final XLS2CSVmraCustom xls2csv = new XLS2CSVmraCustom(fs, p, -1, delimiter);
			xls2csv.setDateFormat(new SimpleDateFormat(getDateDataStyle().getDataFormatString()));
			xls2csv.setDecimalFormat(new DecimalFormat(getDoubleDataStyle().getDataFormatString()));
			xls2csv.process();
			p.close();
			if (null == mimeType) {
				setMimeType(CSV_MIME_TYPE);
			}
			return super.sendConvertedFileToUser(getTableHolder().getUI(), tempCsvFile,
					exportFileName);
		} catch (final IOException e) {
			LOGGER.warn("Converting to CSV failed with IOException " + e);
			return false;
		}
	}

	public char getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(char delimiter) {
		this.delimiter = delimiter;
	}
}