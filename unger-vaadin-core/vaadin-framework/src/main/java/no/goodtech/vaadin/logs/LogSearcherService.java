package no.goodtech.vaadin.logs;

import no.goodtech.vaadin.ui.Texts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


/**
 * Service for fetching the last log file and searching it.
 * For now, only date search is available
 */
public class LogSearcherService {

	private static final Logger LOGGER = LoggerFactory.getLogger(LogSearcherService.class);

	private String logPath;
	private int maxLines = 15000; // ~1MB
	private String dateFormat = "yyyy-MM-dd HH:mm:ss";
	private SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);

	public LogSearcherService(String logPath) {
		this.logPath = logPath;
	}

	public LogSearcherService(String logPath, int maxLines, String dateFormat) {
		this.logPath = logPath;
		this.maxLines = maxLines;
		this.dateFormat = dateFormat;
		this.dateFormatter = new SimpleDateFormat(dateFormat);
	}

	public LogInfo fetchLogging() {
		return fetchLogging((Date) null, (Date) null);
	}

	public LogInfo fetchLogging(LocalDateTime start, LocalDateTime end) {
		Date startDate = (start != null) ? Date.from(start.atZone(ZoneId.systemDefault()).toInstant()) : null;
		Date endDate = (end != null) ? Date.from(end.atZone(ZoneId.systemDefault()).toInstant()) : null;
		return fetchLogging(startDate, endDate);
	}

	public LogInfo fetchLogging(Date startDate, Date endDate) {
		if (logPath == null || logPath.isEmpty()) {
			LOGGER.debug("Log path is empty..");
			return new LogInfo(Texts.get("logSearcherService.noLogPath"), 0, 0);
		}

		long startTime = System.currentTimeMillis();

		boolean startSet = false;
		if (startDate == null)
			startSet = true;

		try {
			// Read all lines and save in a ring buffer with size maxLines
			BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(logPath)));
			String[] lines = new String[maxLines];
			int totalLinesCount = 0;
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				if (!startSet) {
					Date logDate = null;
					try {
						logDate = dateFormatter.parse(line.substring(0, dateFormat.length()));
					} catch (ParseException ignored) {
					}
					if (logDate != null && logDate.after(startDate)) {
						startSet = true;
						lines[totalLinesCount % lines.length] = line;
						totalLinesCount++;
					}
				} else {
					// Break out if logDate is after endDate
					if (endDate != null) {
						Date logDate = null;
						try {
							logDate = dateFormatter.parse(line.substring(0, dateFormat.length()));
						} catch (ParseException ignored) {
						}
						if (logDate != null && logDate.after(endDate)) {
							break;
						}
					}

					// Add the new line
					lines[totalLinesCount % lines.length] = line;
					totalLinesCount++;
				}
			}

			StringBuilder builder = new StringBuilder();
			int numLinesShowing = 0;
			for (String l : lines) {
				if (l != null) {
					builder.append(l).append("\n");
					numLinesShowing++;
				} else {
					break;
				}
			}
			LOGGER.debug("Used: " + (System.currentTimeMillis() - startTime));
			return new LogInfo(builder.toString(), totalLinesCount, numLinesShowing);
		} catch (IOException e) {
			LOGGER.debug("Could not find any log files...");
			return new LogInfo(Texts.get("logSearcherService.noLogFile"), 0, 0);
		}
	}

	public int getMaxLines() {
		return maxLines;
	}

	public void setMaxLines(int maxLines) {
		this.maxLines = maxLines;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
		this.dateFormatter = new SimpleDateFormat(dateFormat);
	}

	public String getLogPath() {
		return logPath;
	}

	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}
}
