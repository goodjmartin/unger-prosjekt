package no.goodtech.vaadin.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
@EnableScheduling
@Profile("DeadLockTester")
class DeadLockTester {

	private final Logger logger = LoggerFactory.getLogger(DeadLockTester.class);

	private final Lock lock1 = new Lock("1");
	private final Lock lock2 = new Lock("2");

	@Scheduled(initialDelayString = "${deadLockTester.initialDelay}", fixedDelayString = "${deadLockTester.fixedDelay}")
	public void deadLockThread1() {
		tryToLock("1", lock1, lock2);
	}

	@Scheduled(initialDelayString = "${deadLockTester.initialDelay}", fixedDelayString = "${deadLockTester.fixedDelay}")
	public void deadLockThread2() {
		tryToLock("2", lock2, lock1);
	}

	@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
	private void tryToLock(final String instanceId, final Lock lock1, final Lock lock2) {
		logger.debug(String.format("DeadLockTester %s locking %s", instanceId, lock1.getInstanceId()));
		synchronized (lock1) {
			lock1.foo(instanceId);

			try {Thread.sleep(5000);} catch (InterruptedException ignored) {}

			logger.debug(String.format("DeadLockTester %s locking %s", instanceId, lock2.getInstanceId()));
			synchronized (lock2) {
				lock2.foo(instanceId);
			}
		}
	}

	private class Lock {
		private final String instanceId;

		Lock(final String instanceId) {
			this.instanceId = instanceId;
		}

		void foo(final String deadLockTesterId) {
			logger.debug(String.format("DeadLockTester %s calling lock%s.foo()", deadLockTesterId, instanceId));
		}

		String getInstanceId() {
			return instanceId;
		}
	}

}
