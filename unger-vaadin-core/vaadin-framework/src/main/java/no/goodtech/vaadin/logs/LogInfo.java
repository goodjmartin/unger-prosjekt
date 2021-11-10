package no.goodtech.vaadin.logs;

/**
 * Simple class for keeping results from search in system logs
 */
public class LogInfo {

	private String log;
	private int totalLines;
	private int foundLines;


	public LogInfo(String log, int totalLines, int foundLines) {
		this.log = log;
		this.totalLines = totalLines;
		this.foundLines = foundLines;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public int getTotalLines() {
		return totalLines;
	}

	public void setTotalLines(int totalLines) {
		this.totalLines = totalLines;
	}

	public int getFoundLines() {
		return foundLines;
	}

	public void setFoundLines(int foundLines) {
		this.foundLines = foundLines;
	}
}
