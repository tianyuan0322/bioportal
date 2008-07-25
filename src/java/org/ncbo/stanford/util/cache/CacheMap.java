package org.ncbo.stanford.util.cache;

	import java.util.HashMap;
	import java.util.Iterator;
	import java.util.Map;

	/**
	 * Timeout version of a <code>HashMap</code>.
	 * 
	 * <p>
	 * Entries in the map have a limited lifelength, and the <code>get</code>
	 * method will only return the stored object during a limited time period.
	 * </p>
	 * 
	 * <p>
	 * After the timeout period, thr object will be removed.
	 * </p>
	 * 
	 * @see CachedObject
	 */
	public class CacheMap extends HashMap {

	    private static final long serialVersionUID = 1L;

	    // Timeout period in milliseconds for stored objects
	    protected long timeout;

	    /**
	     * Create a map with default timeout period.
	     * 
	     * <p>
	     * Equivalent to <code>CacheMap(CachedObject.DEFAULT_TIMEOUT)</code>
	     * </p>
	     * 
	     * @see CachedObject#DEFAULT_TIMEOUT
	     * @see CachedObject
	     */
	    public CacheMap() {
	        this(CachedObject.DEFAULT_TIMEOUT);
	    }

	    /**
	     * Create a map with a specified timeout period.
	     * 
	     * <p>
	     * After an object has been stored in the map using <code>put</code>, it
	     * will only be available for <code>timeout</code> milliseconds. After
	     * that period, <code>get</code> will return <code>null</code>
	     * </p>
	     * 
	     * @param timeout
	     *            timeout period in milliseconds for cached objects.
	     */
	    public CacheMap(long timeout) {
	        this.timeout = timeout;
	    }

	    /**
	     * Get an object from the map. If the object's timeout period has expired,
	     * remove it from the map and return <code>null</code>.
	     * 
	     * <p>
	     * <b>Note</b>: Since the timeout check is done for each cached object, be
	     * careful when looping over a map using iterators or other sequences of
	     * <code>get</code>. The result from a new <code>get</code> can become
	     * <code>null</code> faster than you might think. Thus <b>always</b>
	     * check for <code>null</code> return values from <code>get</code>.
	     * </p>
	     */
	    public Object get(Object key) {

	        CachedObject cache = (CachedObject) super.get(key);

	        if (cache == null) {
	            return null;
	        }
	        Object obj = cache.get();
	        if (obj == null) {
	            remove(key);
	        }
	        return obj;
	    }

	    /**
	     * Check if an object exists in the map. If non-existent or expired, return
	     * <code>false</code>.
	     */
	    public boolean containsKey(Object key) {
	        return null != get(key);
	    }

	    /**
	     * Store an object in the map. The object's timeout period will start from
	     * the current system time.
	     */
	    public Object put(Object key, Object value) {

	        CachedObject cache = (CachedObject) super.get(key);

	        if (cache == null) {
	            super.put(key, new CachedObject(value, timeout));
	            return null;
	        }
	        Object old = cache.get();
	        cache.set(value);
	        return old;
	    }

	    /**
	     * Force timeout check on all items in map and remove all expired keys and
	     * objects.
	     * 
	     * <p>
	     * Normally, the timeout check is only done at <code>get</code> calls, but
	     * if necessary (for example at periodic complete printouts or before loops)
	     * you can (and probably want to) force a timeout check on all cached
	     * objects.
	     * </p>
	     */
	    public void update() {
	        for (Iterator it = entrySet().iterator(); it.hasNext();) {
	            Map.Entry entry = (Map.Entry) it.next();
	            CachedObject cache = (CachedObject) entry.getValue();
	            if (cache.get() == null) {
	                remove(entry.getKey());
	            }
	        }
	    }

} // CacheMap

