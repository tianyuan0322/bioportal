package org.ncbo.stanford.util.collections;

/**
 * A very simple node class for a linked list. The only nice thing here is that
 * it's strongly typed (it wraps an instance of type V).
 * 
 * Note also that none of these methods are synchronized. Synchronization, if
 * necessary, should be done elsewhere.
 * 
 * @author Michael Dorf
 * 
 */
public class Node<V> {
	
	private V value;
	private Node<V> predecessor;
	private Node<V> successor;

	public Node(V val) {
		value = val;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V newVal) {
		value = newVal;
	}
	
	public void insertAfter(Node<V> newPredecessor, Node<V> head) {
		if (newPredecessor == null) {
			successor = head;
		} else {
			successor = newPredecessor.getSuccessor();
			newPredecessor.setSuccessor(this);
		}		
		
		predecessor = newPredecessor;		
	}
	
	public void insertBefore(Node<V> newSuccessor, Node<V> tail) {		
		if (newSuccessor == null) {
			predecessor = tail;
			newSuccessor.setPredecessor(this);
		} else {
			predecessor = newSuccessor.getPredecessor();
		}

		successor = newSuccessor;
	}
	
	public Node<V> getPredecessor() {
		return predecessor;
	}

	public void setPredecessor(Node<V> newPredecessor) {
		predecessor = newPredecessor;
	}

	public Node<V> getSuccessor() {
		return successor;
	}

	public void setSuccessor(Node<V> newSuccessor) {
		successor = newSuccessor;
	}

	public void remove() {
		if ((null == predecessor) || (null == successor)) {
			return;
		}
		
		predecessor.setSuccessor(successor);
		successor.setPredecessor(predecessor);
	}
	
	public String toString() {
		return (value != null) ? value.toString() : null;
	}
}