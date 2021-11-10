package no.goodtech.vaadin.buttons.export;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.eventusermodel.MissingRecordAwareHSSFListener;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.record.FormatRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NoteRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.RKRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * A XLS -> CSV processor, that uses the MissingRecordAware
 * EventModel code to ensure it outputs all columns and rows.
 *
 * This modifies the already existing XLS2CSVmra in com.vaadin.addon.tableexport to support special delimiters
 * TODO remove this when com.vaadin.addon.tableexport.CsvExport supports special delimiters! (remove dependency from pom as well)
 */
public class XLS2CSVmraCustom implements HSSFListener {
	private int minColumns;
	private POIFSFileSystem fs;
	private PrintStream output;

	private int lastRowNumber;
	private int lastColumnNumber;

	/**
	 * Should we output the formula, or the value it has?
	 */
	private boolean outputFormulaValues = true;

	// Records we pick up as we process
	private SSTRecord sstRecord;
	private Map customFormatRecords = new Hashtable();
	private List xfRecords = new ArrayList();

	private char separatorChar;
	private DateFormat dateFormat;
	private DecimalFormat decimalFormat;
//	private Character quoteChar;

	/**
	 * Creates a new XLS -> CSV converter
	 *
	 * @param fs         The POIFSFileSystem to process
	 * @param output     The PrintStream to output the CSV to
	 * @param minColumns The minimum number of columns to output, or -1 for no minimum
	 */
	public XLS2CSVmraCustom(POIFSFileSystem fs, PrintStream output, int minColumns, char separator) {
		this.fs = fs;
		this.output = output;
		this.minColumns = minColumns;
		this.separatorChar = separator;
//		this.quoteChar = quote;
	}

	/**
	 * Creates a new XLS -> CSV converter
	 *
	 * @param filename   The file to process
	 * @param minColumns The minimum number of columns to output, or -1 for no minimum
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public XLS2CSVmraCustom(String filename, int minColumns) throws IOException, FileNotFoundException {
		this(new POIFSFileSystem(new FileInputStream(filename)), System.out, minColumns, ';');
	}

	/**
	 * Initiates the processing of the XLS file to CSV
	 */
	public void process() throws IOException {
		MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
		HSSFEventFactory factory = new HSSFEventFactory();
		HSSFRequest request = new HSSFRequest();
		request.addListenerForAllRecords(listener);

		factory.processWorkbookEvents(request, fs);
	}

	/**
	 * Main HSSFListener method, processes events, and outputs the
	 * CSV as the file is processed.
	 */
	public void processRecord(Record record) {
		int thisRow = -1;
		int thisColumn = -1;
		String thisStr = null;

		switch (record.getSid()) {
			case SSTRecord.sid:
				sstRecord = (SSTRecord) record;
				break;
			case FormatRecord.sid:
				FormatRecord fr = (FormatRecord) record;
				customFormatRecords.put(fr.getIndexCode(), fr);
				break;
			case ExtendedFormatRecord.sid:
				ExtendedFormatRecord xr = (ExtendedFormatRecord) record;
				xfRecords.add(xr);
				break;

			case BlankRecord.sid:
				BlankRecord brec = (BlankRecord) record;

				thisRow = brec.getRow();
				thisColumn = brec.getColumn();
				thisStr = "";
				break;
			case BoolErrRecord.sid:
				BoolErrRecord berec = (BoolErrRecord) record;

				thisRow = berec.getRow();
				thisColumn = berec.getColumn();
				thisStr = "";
				break;
			case FormulaRecord.sid:
				FormulaRecord frec = (FormulaRecord) record;

				thisRow = frec.getRow();
				thisColumn = frec.getColumn();

				if (outputFormulaValues) {
					thisStr = formatNumberDateCell(frec, frec.getValue());
				} else {
					thisStr = frec.toString();
				}
				break;
			case LabelRecord.sid:
				LabelRecord lrec = (LabelRecord) record;

				thisRow = lrec.getRow();
				thisColumn = lrec.getColumn();
				thisStr = lrec.getValue();
				break;
			case LabelSSTRecord.sid:
				LabelSSTRecord lsrec = (LabelSSTRecord) record;

				thisRow = lsrec.getRow();
				thisColumn = lsrec.getColumn();
				if (sstRecord == null) {
					thisStr = "(No SST Record, can't identify string)";
				} else {
					thisStr = sstRecord.getString(lsrec.getSSTIndex()).toString();
				}
				break;
			case NoteRecord.sid:
				NoteRecord nrec = (NoteRecord) record;

				thisRow = nrec.getRow();
				thisColumn = nrec.getColumn();
				thisStr = "(TODO)";
				break;
			case NumberRecord.sid:
				NumberRecord numrec = (NumberRecord) record;

				thisRow = numrec.getRow();
				thisColumn = numrec.getColumn();

				// Format
				thisStr = formatNumberDateCell(numrec, numrec.getValue());
				break;
			case RKRecord.sid:
				RKRecord rkrec = (RKRecord) record;

				thisRow = rkrec.getRow();
				thisColumn = rkrec.getColumn();
				thisStr = "(TODO)";
				break;
			default:
				break;
		}

		// Handle new row
		if (thisRow != -1 && thisRow != lastRowNumber) {
			lastColumnNumber = -1;
		}

		// Handle missing column
		if (record instanceof MissingCellDummyRecord) {
			MissingCellDummyRecord mc = (MissingCellDummyRecord) record;
			thisRow = mc.getRow();
			thisColumn = mc.getColumn();
			thisStr = "";
		}

		// If we got something to print out, do so
		if (thisStr != null) {
			if (thisColumn > 0) {
				output.print(separatorChar);
			}
			output.print(thisStr);
		}

		// Update column and row count
		if (thisRow > -1) {
			lastRowNumber = thisRow;
		}
		if (thisColumn > -1) {
			lastColumnNumber = thisColumn;
		}

		// Handle end of row
		if (record instanceof LastCellOfRowDummyRecord) {
			// Print out any missing commas if needed
			if (minColumns > 0) {
				// Columns are 0 based
				if (lastColumnNumber == -1) {
					lastColumnNumber = 0;
				}
				for (int i = lastColumnNumber; i < (minColumns); i++) {
					output.print(separatorChar);
				}
			}

			// We're onto a new row
			lastColumnNumber = -1;

			// End the row
			output.println();
		}
	}

	/**
	 * Formats a number or date cell, be that a real number, or the
	 * answer to a formula
	 */
	private String formatNumberDateCell(CellValueRecordInterface cell, double value) {
		// Get the built in format, if there is one
		ExtendedFormatRecord xfr = (ExtendedFormatRecord) xfRecords.get(cell.getXFIndex());
		if (xfr == null) {
			System.err.println("Cell " + cell.getRow() + "," + cell.getColumn() + " uses XF with index " + cell.getXFIndex() + ", but we don't have that");
			return Double.toString(value);
		} else {
			int formatIndex = xfr.getFormatIndex();
			String format;
			if (formatIndex >= HSSFDataFormat.getNumberOfBuiltinBuiltinFormats()) {
				FormatRecord tfr = (FormatRecord) customFormatRecords.get(formatIndex);
				format = tfr.getFormatString();
			} else {
				format = HSSFDataFormat.getBuiltinFormat(xfr.getFormatIndex());
			}

			// Is it a date?
			if (HSSFDateUtil.isADateFormat(formatIndex, format) && HSSFDateUtil.isValidExcelDate(value)) {
				// Format as a date
				Date d = HSSFDateUtil.getJavaDate(value, false);
				if(dateFormat != null)
					return dateFormat.format(d);
				// Default:
				format = "dd.MM.yyyy";
				DateFormat df = new SimpleDateFormat(format);
				return df.format(d);
			} else {
				if(decimalFormat != null)
					return decimalFormat.format(value);
				if (Objects.equals(format, "General")) {
					// Some sort of wierd default
					DecimalFormat df = new DecimalFormat("#");
					return df.format(value);
				}

				// Format as a number
				DecimalFormat df = new DecimalFormat(format);
				return df.format(value);
			}
		}
	}

	public DecimalFormat getDecimalFormat() {
		return decimalFormat;
	}

	public void setDecimalFormat(DecimalFormat decimalFormat) {
		this.decimalFormat = decimalFormat;
	}

	public DateFormat getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public char getSeparatorChar() {
		return separatorChar;
	}

	public void setSeparatorChar(char separatorChar) {
		this.separatorChar = separatorChar;
	}
}