package lucene.bean;

import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;

public class LuceneSearchField {
	private String label;
	private String contents;
	private Store store;
	private Index index;

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

	/**
	 * @param label
	 * @param contents
	 * @param store
	 * @param index
	 */
	public LuceneSearchField(String label, String contents, Store store,
			Index index) {
		super();
		this.label = label;
		this.contents = contents;
		this.store = store;
		this.index = index;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the contents
	 */
	public String getContents() {
		return contents;
	}

	/**
	 * @param contents the contents to set
	 */
	public void setContents(String contents) {
		this.contents = contents;
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
}
