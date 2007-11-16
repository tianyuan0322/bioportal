package org.ncbo.stanford.view.session;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import org.acegisecurity.ui.savedrequest.Enumerator;

/**
 * Emulates the behavior of standard HttpSession (StandardSession
 * implementation) but removes all container specific functionality.
 * 
 * @author Michael Dorf
 */
public class RESTSession {

	// ----------------------------------------------------------- Constructors

	/**
	 * Construct a new Session
	 */
	public RESTSession() {
	}

	// ----------------------------------------------------- Instance Variables

	private static final String ILLEGAL_STATE_EXCEPTION_MSG = "Invalid Session";
	private static final String NULL_ATTRIBUTE_NAME_EXCEPTION_MSG = "Name cannot be null";

	/**
	 * The dummy attribute value serialized when a NotSerializableException is
	 * encountered in <code>writeObject()</code>.
	 */
	private static final String NOT_SERIALIZED = "___NOT_SERIALIZABLE_EXCEPTION___";

	/**
	 * The collection of user data attributes associated with this Session.
	 */
	private HashMap<String, Object> attributes = new HashMap<String, Object>();

	/**
	 * The time this session was created, in milliseconds since midnight,
	 * January 1, 1970 GMT.
	 */
	private long creationTime = 0L;

	/**
	 * The session identifier of this Session.
	 */
	private String id = null;

	/**
	 * The last accessed time for this Session.
	 */
	private long lastAccessedTime = creationTime;

	/**
	 * Flag indicating whether this session is new or not.
	 */
	private boolean isNew = false;

	/**
	 * Flag indicating whether this session is valid or not.
	 */
	private boolean isValid = false;

	/**
	 * The current accessed time for this session.
	 */
	private long lastUsedTime = creationTime;

	// ----------------------------------------------------- Session Properties

	/**
	 * Set the creation time for this session. This method is called by the
	 * Manager when an existing Session instance is reused.
	 * 
	 * @param time
	 *            The new creation time
	 */
	public void setCreationTime(long time) {
		this.creationTime = time;
		this.lastAccessedTime = time;
		this.lastUsedTime = time;
	}

	/**
	 * Return the session identifier for this session.
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Set the session identifier for this session.
	 * 
	 * @param id
	 *            The new session identifier
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Return the last time the client sent a request associated with this
	 * session, as the number of milliseconds since midnight, January 1, 1970
	 * GMT. Actions that your application takes, such as getting or setting a
	 * value associated with the session, do not affect the access time.
	 */
	public long getLastAccessedTime() {
		return this.lastAccessedTime;
	}

	/**
	 * Return the last time a request was recieved associated with this session,
	 * as the number of milliseconds since midnight, January 1, 1970 GMT.
	 * Actions that your application takes, such as getting or setting a value
	 * associated with the session, do not affect the access time.
	 */
	public long getLastUsedTime() {
		return this.lastUsedTime;
	}

	/**
	 * Set the <code>isNew</code> flag for this session.
	 * 
	 * @param isNew
	 *            The new value for the <code>isNew</code> flag
	 */
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	/**
	 * Return the <code>isValid</code> flag for this session.
	 */
	public boolean isValid() {
		return this.isValid;
	}

	/**
	 * Set the <code>isValid</code> flag for this session.
	 * 
	 * @param isValid
	 *            The new value for the <code>isValid</code> flag
	 */
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	// ------------------------------------------------- Session Public Methods

	/**
	 * Update the accessed time information for this session. This method should
	 * be called by the context when a request comes in for a particular
	 * session, even if the application does not reference it.
	 */
	public void access() {
		this.isNew = false;
		this.lastAccessedTime = this.lastUsedTime;
		this.lastUsedTime = System.currentTimeMillis();
	}

	/**
	 * Release all object references, and initialize instance variables, in
	 * preparation for reuse of this object.
	 */
	public void recycle() {
		// Reset the instance variables associated with this Session
		attributes.clear();
		creationTime = 0L;
		id = null;
		lastAccessedTime = 0L;
		lastUsedTime = 0L;
		isNew = false;
		isValid = false;
	}

	/**
	 * Return a string representation of this object.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("StandardSession[");
		sb.append(id);
		sb.append("]");
		
		return (sb.toString());
	}

	// ------------------------------------------------ Session Package Methods

	/**
	 * Read a serialized version of the contents of this session object from the
	 * specified object input stream, without requiring that the StandardSession
	 * itself have been serialized.
	 * 
	 * @param stream
	 *            The object input stream to read from
	 * 
	 * @exception ClassNotFoundException
	 *                if an unknown class is specified
	 * @exception IOException
	 *                if an input/output error occurs
	 */
	void readObjectData(ObjectInputStream stream)
			throws ClassNotFoundException, IOException {
		readObject(stream);
	}

	/**
	 * Write a serialized version of the contents of this session object to the
	 * specified object output stream, without requiring that the
	 * StandardSession itself have been serialized.
	 * 
	 * @param stream
	 *            The object output stream to write to
	 * 
	 * @exception IOException
	 *                if an input/output error occurs
	 */
	void writeObjectData(ObjectOutputStream stream) throws IOException {
		writeObject(stream);
	}

	// ------------------------------------------------- HttpSession Properties

	/**
	 * Return the time when this session was created, in milliseconds since
	 * midnight, January 1, 1970 GMT.
	 * 
	 * @exception IllegalStateException
	 *                if this method is called on an invalidated session
	 */
	public long getCreationTime() {
		if (!isValid) {
			throw new IllegalStateException(ILLEGAL_STATE_EXCEPTION_MSG);
		}

		return this.creationTime;
	}

	// ----------------------------------------------HttpSession Public Methods

	/**
	 * Return the object bound with the specified name in this session, or
	 * <code>null</code> if no object is bound with that name.
	 * 
	 * @param name
	 *            Name of the attribute to be returned
	 * 
	 * @exception IllegalStateException
	 *                if this method is called on an invalidated session
	 */
	public Object getAttribute(String name) {
		if (!isValid) {
			throw new IllegalStateException(ILLEGAL_STATE_EXCEPTION_MSG);
		}

		synchronized (attributes) {
			return attributes.get(name);
		}
	}

	/**
	 * Return an <code>Enumeration</code> of <code>String</code> objects
	 * containing the names of the objects bound to this session.
	 * 
	 * @exception IllegalStateException
	 *                if this method is called on an invalidated session
	 */
	public Enumeration<?> getAttributeNames() {
		if (!isValid) {
			throw new IllegalStateException(ILLEGAL_STATE_EXCEPTION_MSG);
		}

		synchronized (attributes) {
			return new Enumerator(attributes.keySet(), true);
		}

	}

	/**
	 * Return <code>true</code> if the client does not yet know about the
	 * session, or if the client chooses not to join the session. For example,
	 * if the server used only cookie-based sessions, and the client has
	 * disabled the use of cookies, then a session would be new on each request.
	 * 
	 * @exception IllegalStateException
	 *                if this method is called on an invalidated session
	 */
	public boolean isNew() {
		if (!isValid) {
			throw new IllegalStateException(ILLEGAL_STATE_EXCEPTION_MSG);
		}

		return this.isNew;
	}

	/**
	 * Remove the object bound with the specified name from this session. If the
	 * session does not have an object bound with this name, this method does
	 * nothing.
	 * <p>
	 * After this method executes, and if the object implements
	 * <code>HttpSessionBindingListener</code>, the container calls
	 * <code>valueUnbound()</code> on the object.
	 * 
	 * @param name
	 *            Name of the object to remove from this session.
	 * 
	 * @exception IllegalStateException
	 *                if this method is called on an invalidated session
	 */
	public void removeAttribute(String name) {
		removeAttribute(name, true);
	}

	/**
	 * Remove the object bound with the specified name from this session. If the
	 * session does not have an object bound with this name, this method does
	 * nothing.
	 * <p>
	 * After this method executes, and if the object implements
	 * <code>HttpSessionBindingListener</code>, the container calls
	 * <code>valueUnbound()</code> on the object.
	 * 
	 * @param name
	 *            Name of the object to remove from this session.
	 * @param notify
	 *            Should we notify interested listeners that this attribute is
	 *            being removed?
	 * 
	 * @exception IllegalStateException
	 *                if this method is called on an invalidated session
	 */
	public void removeAttribute(String name, boolean notify) {
		// Validate our current state
		if (!isValid) {
			throw new IllegalStateException(ILLEGAL_STATE_EXCEPTION_MSG);
		}

		// Remove this attribute from our collection
		boolean found = false;

		synchronized (attributes) {
			found = attributes.containsKey(name);

			if (found) {
				attributes.remove(name);
			}
		}
	}

	/**
	 * Bind an object to this session, using the specified name. If an object of
	 * the same name is already bound to this session, the object is replaced.
	 * <p>
	 * After this method executes, and if the object implements
	 * <code>HttpSessionBindingListener</code>, the container calls
	 * <code>valueBound()</code> on the object.
	 * 
	 * @param name
	 *            Name to which the object is bound, cannot be null
	 * @param value
	 *            Object to be bound, cannot be null
	 * 
	 * @exception IllegalArgumentException
	 *                if an attempt is made to add a non-serializable object in
	 *                an environment marked distributable.
	 * @exception IllegalStateException
	 *                if this method is called on an invalidated session
	 */
	public void setAttribute(String name, Object value) {
		// Name cannot be null
		if (name == null) {
			throw new IllegalArgumentException(
					NULL_ATTRIBUTE_NAME_EXCEPTION_MSG);
		}

		// Null value is the same as removeAttribute()
		if (value == null) {
			removeAttribute(name);
			return;
		}

		// Validate our current state
		if (!isValid) {
			throw new IllegalStateException(ILLEGAL_STATE_EXCEPTION_MSG);
		}

		synchronized (attributes) {
			attributes.put(name, value);
		}
	}

	// -------------------------------------------- HttpSession Private Methods

	/**
	 * Read a serialized version of this session object from the specified
	 * object input stream.
	 * <p>
	 * <b>IMPLEMENTATION NOTE</b>: The reference to the owning Manager is not
	 * restored by this method, and must be set explicitly.
	 * 
	 * @param stream
	 *            The input stream to read from
	 * 
	 * @exception ClassNotFoundException
	 *                if an unknown class is specified
	 * @exception IOException
	 *                if an input/output error occurs
	 */
	private void readObject(ObjectInputStream stream)
			throws ClassNotFoundException, IOException {

		// Deserialize the scalar instance variables (except Manager)
		creationTime = ((Long) stream.readObject()).longValue();
		lastAccessedTime = ((Long) stream.readObject()).longValue();
		isNew = ((Boolean) stream.readObject()).booleanValue();
		isValid = ((Boolean) stream.readObject()).booleanValue();
		lastUsedTime = ((Long) stream.readObject()).longValue();
		id = (String) stream.readObject();

		// Deserialize the attribute count and attribute values
		if (attributes == null) {
			attributes = new HashMap<String, Object>();
		}

		int n = ((Integer) stream.readObject()).intValue();
		boolean isValidSave = isValid;
		isValid = true;

		for (int i = 0; i < n; i++) {
			String name = (String) stream.readObject();
			Object value = (Object) stream.readObject();

			if ((value instanceof String) && (value.equals(NOT_SERIALIZED))) {
				continue;
			}

			synchronized (attributes) {
				attributes.put(name, value);
			}
		}

		isValid = isValidSave;
	}

	/**
	 * Write a serialized version of this session object to the specified object
	 * output stream.
	 * <p>
	 * <b>IMPLEMENTATION NOTE</b>: The owning Manager will not be stored in the
	 * serialized representation of this Session. After calling
	 * <code>readObject()</code>, you must set the associated Manager
	 * explicitly.
	 * <p>
	 * <b>IMPLEMENTATION NOTE</b>: Any attribute that is not Serializable will
	 * be unbound from the session, with appropriate actions if it implements
	 * HttpSessionBindingListener. If you do not want any such attributes, be
	 * sure the <code>distributable</code> property of the associated Manager
	 * is set to <code>true</code>.
	 * 
	 * @param stream
	 *            The output stream to write to
	 * 
	 * @exception IOException
	 *                if an input/output error occurs
	 */
	private void writeObject(ObjectOutputStream stream) throws IOException {

		// Write the scalar instance variables (except Manager)
		stream.writeObject(new Long(creationTime));
		stream.writeObject(new Long(lastAccessedTime));
		stream.writeObject(new Boolean(isNew));
		stream.writeObject(new Boolean(isValid));
		stream.writeObject(new Long(lastUsedTime));
		stream.writeObject(id);

		// Accumulate the names of serializable and non-serializable attributes
		String keys[] = keys();
		ArrayList<String> saveNames = new ArrayList<String>();
		ArrayList<Object> saveValues = new ArrayList<Object>();

		for (int i = 0; i < keys.length; i++) {
			Object value = null;

			synchronized (attributes) {
				value = attributes.get(keys[i]);
			}

			if (value == null) {
				continue;
			} else if (value instanceof Serializable) {
				saveNames.add(keys[i]);
				saveValues.add(value);
			} else {
				removeAttribute(keys[i]);
			}
		}

		// Serialize the attribute count and the Serializable attributes
		int n = saveNames.size();
		stream.writeObject(new Integer(n));

		for (int i = 0; i < n; i++) {
			stream.writeObject((String) saveNames.get(i));

			try {
				stream.writeObject(saveValues.get(i));
			} catch (NotSerializableException e) {
				stream.writeObject(NOT_SERIALIZED);
			}
		}
	}

	// -------------------------------------------------------- Private Methods

	/**
	 * Return the names of all currently defined session attributes as an array
	 * of Strings. If there are no defined attributes, a zero-length array is
	 * returned.
	 */
	private String[] keys() {
		String results[] = new String[0];

		synchronized (attributes) {
			return ((String[]) attributes.keySet().toArray(results));
		}
	}
}