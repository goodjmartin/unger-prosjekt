package no.goodtech.dashboard.model;

import no.goodtech.vaadin.utils.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;

/**
 * You may use this to test live dashboards with pre-configured start time
 */
@Service
public class CurrentTimeProvider {


	private static Long configuredStartTime = null;
	private static long actualStartTime;


	public CurrentTimeProvider(@Value("${dashboard.startTime}") final String startTime) {
		actualStartTime = System.currentTimeMillis();
		if (startTime != null && !startTime.isEmpty()) {
			try {
				configuredStartTime = Utils.DATETIME_FORMATTER.parse(startTime).getTime();
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * @return now or a configured time
	 */
	public static long getTime() {
		long now = System.currentTimeMillis();
		if (configuredStartTime == null) {
			return now;
		} else {
			long timeSinceStart = now - actualStartTime;
			return configuredStartTime + timeSinceStart;
		}
	}
}
