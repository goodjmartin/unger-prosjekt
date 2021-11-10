package no.goodtech.vaadin.tabs.status;

import java.util.Date;

import javax.management.timer.Timer;
import javax.sql.DataSource;

import no.cronus.common.scheduler.IScheduledTask;
import no.goodtech.vaadin.tabs.status.common.IStatusLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Delete old rows from a database table
 * @author oystein
 */
public class DatabaseRowPurger implements IScheduledTask {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseRowPurger.class);
	private final String tableName;
	private final String timestampColumnName;
	private final Integer numDaysToKeep;
	private final JdbcTemplate jdbcTemplate;
	private final String statusLoggerId;

	/**
	 * Configure what to delete.
	 * Will delete rows from tableName where timestampColumnName is older than numDaysToKeep every time you call {@link #execute()}
	 * @param tableName name of the table to delete rows from 
	 * @param timestampColumnName name of the timestamp column
	 * @param numDaysToKeep minimum age of the rows you like to delete
	 * @param dataSource the database the table lives in
	 * @param statusLoggerId provide this if you want to log to a statuslogger
	 */
	public DatabaseRowPurger(String tableName, String timestampColumnName, Integer numDaysToKeep, DataSource dataSource, String statusLoggerId) {

		this.tableName = tableName;
		this.timestampColumnName = timestampColumnName;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.statusLoggerId = statusLoggerId;
		this.numDaysToKeep = numDaysToKeep;
		if (numDaysToKeep == null)
			LOGGER.warn("You need to provide how many days you like to keep in order to purge old rows in {}", tableName);
		else
			LOGGER.info("Will purge rows older than {} days from {} ", numDaysToKeep, tableName);
	}

	public void execute() {
		if (numDaysToKeep != null) {
			Date now = new Date();
			Date expiryDate = new Date(now.getTime() - numDaysToKeep * Timer.ONE_DAY);
			int rowCount = jdbcTemplate.update("delete from " + tableName + " where " + timestampColumnName + " < ?", expiryDate);
			
			final String message = "Wiped " + rowCount + " row(s) older than " + expiryDate + " from " + tableName;
			LOGGER.debug(message);
			if (getStatusLogger() != null)
				getStatusLogger().success(message);
		}
	}

	public void unhandledException(Throwable throwable) {
		final String message = "Unexpected error: " + throwable.getMessage();
		if (statusLoggerId != null) {
			getStatusLogger().failure(message, throwable);
		} else
			LOGGER.error(message, throwable);
	}

	public void timeout() {
		final String message = "Timeout when deleting rows from " + tableName;
		if (statusLoggerId != null)
			 getStatusLogger().warning(message);
		else
			LOGGER.warn(message);
	}
	
	private IStatusLogger getStatusLogger() {
		return Globals.getStatusLoggerRepository().lookupStatusLogger(statusLoggerId);
	}
}
