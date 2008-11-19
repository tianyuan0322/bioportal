package lucene.bean;

import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;

public class LuceneSearchField {
	private String label;
	private Store store;
	private Index index;
	private String contents;

	/**
	 * @param label
	 * @param store
	 * @param index
	 */
	public LuceneSearchField(String label, Store store, Index index) {
		super();
		this.label = label;
		this.store = store;
		this.index = index;
	}

	public String toString() {
		return "Label: " + label + ", Store: " + store + ", Index: " + index
				+ ", Contents: " + contents;
	}

	/**
	 * @param label
	 * @param store
	 * @param index
	 * @param contents
	 */
	public LuceneSearchField(String label, Store store, Index index,
			String contents) {
		this(label, store, index);
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
