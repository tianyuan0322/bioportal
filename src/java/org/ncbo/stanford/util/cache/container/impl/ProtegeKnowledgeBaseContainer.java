package org.ncbo.stanford.util.cache.container.impl;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.util.cache.container.HashbeltContainer;

import edu.stanford.smi.protege.model.KnowledgeBase;

/**
 * Hashbelt container specific to Protege knowledge bases
 * 
 * @author Michael Dorf
 * 
 */
public class ProtegeKnowledgeBaseContainer implements
		HashbeltContainer<Integer, KnowledgeBase> {

	private static final Log log = LogFactory
			.getLog(ProtegeKnowledgeBaseContainer.class);
	private HashbeltContainer<Integer, KnowledgeBase> baseContainer;

	public ProtegeKnowledgeBaseContainer(
			HashbeltContainer<Integer, KnowledgeBase> baseContainer) {
		this.baseContainer = baseContainer;
	}

	public void clear() {
		baseContainer.clear();
	}

	public synchronized KnowledgeBase get(Integer key) {
		return baseContainer.get(key);
	}

	public Iterator<Integer> getKeys() {
		return baseContainer.getKeys();
	}

	public Iterator<KnowledgeBase> getValues() {
		return baseContainer.getValues();
	}

	public synchronized void put(Integer key, KnowledgeBase value) {
		baseContainer.put(key, value);
	}

	public synchronized KnowledgeBase remove(Integer key) {
		KnowledgeBase oldValue = baseContainer.remove(key);

		if (oldValue != null) {
			log.debug("Disposing of the knowledgebase: " + oldValue.getName());
			oldValue.getProject().dispose();
		}

		return oldValue;
	}

	public synchronized KnowledgeBase removeShallow(Integer key) {
		return baseContainer.removeShallow(key);
	}

	public int size() {
		return baseContainer.size();
	}
}