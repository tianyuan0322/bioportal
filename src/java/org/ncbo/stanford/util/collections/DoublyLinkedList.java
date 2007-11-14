package org.ncbo.stanford.util.collections;

import java.util.Iterator;

/**
 * Very simple implementation of a doubly linked list.
 * 
 * Unlike the Javasoft version, it doesn't know its size and can be easily
 * mutated into an inconsistent form.
 * 
 * None of the methods are synchronized. Synchronization, if necessary, should
 * be done elsewhere.
 * 
 * @author Michael Dorf
 * 
 */
public class DoublyLinkedList<V> {
//	public static void main(String[] args) {
//		DoublyLinkedList<Node<String>> l = new DoublyLinkedList<Node<String>>();
//		
//		l.add(new Node<String>("1"));
//		l.add(new Node<String>("2"));
//		l.add(new Node<String>("3"));
//		l.add(new Node<String>("4"));
//		l.add(new Node<String>("5"));
//		l.add(new Node<String>("6"));
//		
//		System.out.println("List: " + l);
//	}

	
	private Node<V> head;
	private Node<V> tail;

	public Iterator<V> iterator() {
		return new DoublyLinkedListIterator();
	}
	
	public Node<V> add(V value) {
		resynchronizeLast();
		Node<V> newLast = new Node<V>(value);
		
		if (isEmpty()) {
			head = newLast;
		} else {
			newLast.insertAfter(tail, head);
		}

		tail = newLast;

		return tail;
	}
	
	public boolean isEmpty() {
		return (head == null && tail == null);
	}
	
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		
		for (Iterator<V> i = iterator(); i.hasNext();) {
			strBuf.append(i.next());
			
			if (i.hasNext()) {
				strBuf.append(" --> ");
			}
		}
		
		return strBuf.toString();
	}
	
	private void resynchronizeLast() {
		if (null == tail) {
			return;
		}

		while (null != tail.getSuccessor()) {
			tail = tail.getSuccessor();
		}
	}

	private class DoublyLinkedListIterator implements Iterator<V> {
		private Node<V> currentValue = null;
		private Node<V> nextValue = head;

		public boolean hasNext() {
			return (null != nextValue);
		}

		public V next() {
			currentValue = nextValue;
			nextValue = nextValue.getSuccessor();
			
			return currentValue.getValue();
		}

		public void remove() {
			if (null != currentValue) {
				currentValue.remove();
			}
		}
	}
}