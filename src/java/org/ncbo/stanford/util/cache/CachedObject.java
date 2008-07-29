package org.ncbo.stanford.util.cache;

/**
 * An object with a timeout.
 * 
 * <p>
 * A CachedObject has a timeout period - during that period the <code>get</code>
 * method will return the stored object, afterwards <code>get</code> will
 * return <code>null</code>
 * </p>
 * 
 */
public class CachedObject<V> {

	/**
	 * Default timeout period in milliseconds. Value is 60 seconds.
	 */
	public final static long DEFAULT_TIMEOUT = 60 * 1000;

	// Timeout in milliseconds for this cached object
	private long timeout;

	// Creation time of this cached object.
	private long creationTime;

	// Actual object stored in cache
	private V val;

	/**
	 * Equivalent to <code>CachedObject(null)</code>.
	 * 
	 * @see CacheMap
	 */
	public CachedObject() {
		this(null);
	}

	/**
	 * Equivalent to
	 * <code>CachedObject(object, CachedObject.DEFAULT_TIMEOUT)</code>
	 * 
	 * @see #DEFAULT_TIMEOUT
	 */
	public CachedObject(V val) {
		this(val, DEFAULT_TIMEOUT);
	}

	/**
	 * Create a cached object from an object and a specified timeout.
	 * 
	 * @param object
	 *            Object to cache
	 * @param timeout
	 *            period in milliseconds.
	 * @see #DEFAULT_TIMEOUT
	 */
	public CachedObject(V val, long timeout) {
		this.timeout = timeout;
		set(val);
	}

	/**
	 * Set the cache's object and restore its creation time.
	 * 
	 * @param object
	 *            Object to cache
	 */
	public void set(V val) {
		this.val = val;
		creationTime = System.currentTimeMillis();
	}

	/**
	 * Get the cached object.
	 * 
	 * @return The cached object before its timeout period, <code>null</code>
	 *         afterwards.
	 */
	public V get() {
		long now = System.currentTimeMillis();

		if (now - creationTime > timeout) {
			flush();
		}

		return val;
	}

	/**
	 * Clear the stored object. After this call, <code>get</code> will
	 * 
	 */
	public void flush() {
		val = null;
	}

	/**
	 * Print the cached object as "&lt;object's string value&gt;:&lt;remaining
	 * time in milliseconds&gt;"
	 */
	public String toString() {
		return val.toString() + ":"
				+ (System.currentTimeMillis() - creationTime);
	}
}

