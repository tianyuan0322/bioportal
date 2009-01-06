package org.ncbo.stanford.util.cache.expiration.system;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This thread wakes up, cleans up the cache, and goes back to sleep
 * 
 * @author Michael Dorf
 * 
 */
@SuppressWarnings("unchecked")
public class ExpirationThread extends Thread {

	private static final Log log = LogFactory.getLog(ExpirationThread.class);

	public static final long FIFTEEN_SECONDS = 15 * 1000;
	public static final long REAPER_THREAD_SLEEP_TIME = FIFTEEN_SECONDS;

	private boolean shouldKeepRunning = true;
	private long timeToSleep;
	private AbstractExpirationSystem owner;

	public ExpirationThread(AbstractExpirationSystem owner) {
		this(REAPER_THREAD_SLEEP_TIME, owner);
	}

	public ExpirationThread(long timeToSleepIn, AbstractExpirationSystem ownerIn) {
		owner = ownerIn;
		timeToSleep = timeToSleepIn;
		setPriority(Thread.MIN_PRIORITY);
	}

	public synchronized void halt() {
		shouldKeepRunning = false;
	}

	public void run() {
		while (shouldKeepRunning()) {
			try {
				Thread.sleep(timeToSleep);
			} catch (InterruptedException ignored) {
			}

			owner.expireObjects();
		}
	}

	private synchronized boolean shouldKeepRunning() {
		return shouldKeepRunning;
	}
}