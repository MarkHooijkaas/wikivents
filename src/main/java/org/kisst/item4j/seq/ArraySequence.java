package org.kisst.item4j.seq;

import java.util.Iterator;
import java.util.List;

import org.kisst.item4j.Item;

public class ArraySequence<T> implements TypedSequence<T> {
	public final Class<?> cls;
	private final T[] array; 
	public ArraySequence(Class<?> cls, T[] arr) {
		this.cls=cls;
		this.array=createArray(arr.length);
		for (int i=0; i<arr.length; i++)
			this.array[i]=arr[i];
	}
	@SuppressWarnings("unchecked")
	public ArraySequence(Item.Factory factory, Class<?> cls, ItemSequence seq) {
		this.cls=cls;
		this.array=createArray(seq.size());
		int i=0;
		for (Object obj : seq.items())
			this.array[i++]=(T) Item.asType(factory, getElementClass(), obj);
	}
	public ArraySequence(Class<?> cls, List<T> list) {
		this.cls=cls;
		this.array=createArray(list.size());
		int i=0;
		for (T obj : list)
			this.array[i++]=obj;
	}


	@Override public int size() { return array.length; }
	@Override public Object getObject(int index) { return array[index]; }
	@Override public Class<?> getElementClass() { return cls;}

	@SuppressWarnings("unchecked")
	private T[] createArray(int length) { return (T[]) new Object[length]; }

	
	private class MyIterator implements Iterator<T> {
		private int index=0;
		@Override public boolean hasNext() { return index<array.length; }
		@Override public T next() { return array[index++]; }
		@Override public void remove() { throw new RuntimeException("not implemented");}
	}
	@Override public Iterator<T> iterator() { return new MyIterator();}
}
