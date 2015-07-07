package org.kisst.struct4j.seq;

import java.util.Iterator;

public class TypedArraySequence<T> implements Sequence<T> {
	public final Class<?> cls;
	private final T[] array; 
	public TypedArraySequence(Class<?> cls, T[] arr) {
		this.cls=cls;
		this.array=createArray(arr.length);
		for (int i=0; i<arr.length; i++)
			this.array[i]=arr[i];
	}
	public TypedArraySequence(Class<?> cls, ItemSequence seq) {
		this.cls=cls;
		this.array=createArray(seq.size());
		int i=0;
		for (Object obj : seq)
			this.array[i++]=transformObject(obj);
	}


	public int size() { return array.length; }
	public T get(int index) { return (T) array[index]; }

	private T[] createArray(int length) { return (T[]) new Object[length]; }
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
