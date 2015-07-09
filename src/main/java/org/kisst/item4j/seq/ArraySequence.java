package org.kisst.item4j.seq;

import java.util.Iterator;

public class ArraySequence<T> implements Sequence<T> {
	public final Class<?> cls;
	private final T[] array; 
	public ArraySequence(Class<?> cls, T[] arr) {
		this.cls=cls;
		this.array=createArray(arr.length);
		for (int i=0; i<arr.length; i++)
			this.array[i]=arr[i];
	}
	public ArraySequence(Class<?> cls, ItemSequence seq) {
		this.cls=cls;
		this.array=createArray(seq.size());
		int i=0;
		for (Object obj : seq)
			this.array[i++]=transformObject(obj);
	}


	public int size() { return array.length; }
	public T get(int index) { return (T) array[index]; }

	@SuppressWarnings("unchecked")
	private T[] createArray(int length) { return (T[]) new Object[length]; }
	@SuppressWarnings("unchecked")
	private T transformObject(Object obj) {
		// TODO 
		// if instanceof T
		// if instanceof Struct
		return (T) obj;
	}
	
	private class MyIterator implements Iterator<T> {
		private int index=0;
		@Override public boolean hasNext() { return index<array.length; }
		@Override public T next() { return array[index++]; }
		@Override public void remove() { throw new RuntimeException("not implemented");}
	}
	@Override public Iterator<T> iterator() { return new MyIterator();}
}
