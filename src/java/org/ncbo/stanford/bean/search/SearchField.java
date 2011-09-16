package org.ncbo.stanford.bean.search;

import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;

/**
 * Defines a structure for a single search field in the index
 * 
 * @author Michael Dorf
 * 
 */
public class SearchField {
	private String label;
	private Store store;
	private Index index;
	private String contents;
	private Class<?> type;

	/**
	 * @param label
	 * @param store
	 * @param index
	 * @param type
	 */
	public SearchField(String label, Store store, Index index, Class<?> type) {
		super();
		this.label = label;
		this.store = store;
		this.index = index;
		this.type = type;
	}

	public String toString() {
		return "Label: " + label + ", Store: " + store + ", Index: " + index
				+ ", Contents: " + contents + ", Type: " + type;
	}

	/**
	 * @param label
	 * @param store
	 * @param index
	 * @param type
	 * @param contents
	 */
	public SearchField(String label, Store store, Index index, Class<?> type,
			String contents) {
		this(label, store, index, type);
		this.contents = contents;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the store
	 */
	public Store getStore() {
		return store;
	}

	/**
	 * @return the index
	 */
	public Index getIndex() {
		return index;
	}

	/**
	 * @return the type
	 */
	public Class<?> getType() {
		return type;
	}

	/**
	 * @return the contents
	 */
	public String getContents() {
		return contents;
	}

	/**
	 * @param contents
	 *            the contents to set
	 */
	public void setContents(String contents) {
		this.contents = contents;
	}
}
