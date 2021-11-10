package no.goodtech.vaadin.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.management.*;
import com.sun.management.OperatingSystemMXBean;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
@EnableScheduling
@Profile("JavaVMMonitor")
public class JavaVMMonitor {

	private final Logger logger = LoggerFactory.getLogger(JavaVMMonitor.class);

	private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
	private final OperatingSystemMXBean operatingSystemMXBean = (com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
	private final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
	private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

	@SuppressWarnings("FieldCanBeLocal")
	@Value("${javaVMMonitor.checkForDeadLock.exitOnDeadLock}")
	private final Boolean exitOnDeadLock = false;

	@SuppressWarnings("FieldCanBeLocal")
	@Value("${javaVMMonitor.threadPoolSize}")
	private final Integer threadPoolSize = 10;

	@SuppressWarnings("unused")
	@Bean(destroyMethod = "shutdown")
	public Executor taskScheduler() {
		return Executors.newScheduledThreadPool(threadPoolSize);
	}

	public JavaVMMonitor() {
		// Log Java VM version used
		String vmName = runtimeMXBean.getVmName();
		String vmVersion = runtimeMXBean.getVmVersion();
		logger.info(String.format("%s (%s)", vmName, vmVersion));
	}

	@Scheduled(initialDelayString = "${javaVMMonitor.checkForDeadLock.initialDelay}", fixedDelayString = "${javaVMMonitor.checkForDeadLock.fixedDelay}")
	private void checkForDeadLock() {
		long now = System.currentTimeMillis();

		long[] ids = threadMXBean.findDeadlockedThreads();
		if (ids != null) {
			logger.error(String.format("*** Found %d deadlocked threads ***", ids.length));
			for (long id : ids) {
				ThreadInfo threadInfo = threadMXBean.getThreadInfo(id, Integer.MAX_VALUE);
				logger.error(String.format("\tDeadlock candidate: thread=%s, foo=%s, lockOwner=%s, state=%s", threadInfo.getThreadName(), threadInfo.getLockName(), threadInfo.getLockOwnerName(), threadInfo.getThreadState()));
				logger.error(formatStackTrace(threadInfo.getThreadName(), threadInfo.getStackTrace()));
			}

			if (exitOnDeadLock) {
				logger.error("Exiting application");

				System.exit(127);
			}
		}

		logger.debug(String.format("Checked for deadlock in %d millisecond", System.currentTimeMillis() - now));
	}

	@Scheduled(initialDelayString = "${javaVMMonitor.exportMetrics.initialDelay}", fixedDelayString = "${javaVMMonitor.exportMetrics.fixedDelay}")
	private void exportMetrics() {
		long now = System.currentTimeMillis();

		double systemCpuLoad = operatingSystemMXBean.getSystemCpuLoad();
		double processCpuLoad = operatingSystemMXBean.getProcessCpuLoad();

		// Obtain up-time information
		String upTime = formatUpTime(runtimeMXBean.getUptime());

		// Obtain heap information
		long heapMax = memoryMXBean.getHeapMemoryUsage().getMax() / 1000000;
		long heapUsed = memoryMXBean.getHeapMemoryUsage().getUsed() / 1000000;

		// Obtain thread information
		long threadCount = threadMXBean.getThreadCount();
		long threadCountPeak = threadMXBean.getPeakThreadCount();
		long threadCountTotalStarted = threadMXBean.getTotalStartedThreadCount();

		// Log obtained information
		logger.info(String.format("uptime=%s, systemCpuLoad=%.2f%%, processCpuLoad=%.2f%%, heapMax=%d MB, heapUsed=%d MB (%s %%), threadCount=%s, threadCountPeak=%s, threadCountTotalStarted=%s ", upTime, 100 * systemCpuLoad, 100 * processCpuLoad, heapMax, heapUsed, (100 * heapUsed / heapMax), threadCount, threadCountPeak, threadCountTotalStarted));
		logger.debug(String.format("Exported metrics in %d millisecond", System.currentTimeMillis() - now));
	}

	private String formatStackTrace(final String threadName, final StackTraceElement[] stackTraceElements) {
		StringBuilder stringBuilder = new StringBuilder(String.format("\tStack trace for thread %s\n", threadName));
		for (StackTraceElement stackTraceElement : stackTraceElements) {
			stringBuilder.append(String.format("\tat %s.%s(%s:%d)\n", stackTraceElement.getClassName(), stackTraceElement.getMethodName(), stackTraceElement.getFileName(), stackTraceElement.getLineNumber()));
		}
		return stringBuilder.toString();
	}

	private String formatUpTime(long uptime) {
		long upTimeInSeconds = uptime / 1000;

		// Calculate hours, minutes and seconds
		long hours = upTimeInSeconds / 3600;
		long minutes = (upTimeInSeconds % 3600) / 60;
		long seconds = upTimeInSeconds % 60;

		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

}
