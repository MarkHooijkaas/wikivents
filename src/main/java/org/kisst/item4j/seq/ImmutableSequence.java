package org.kisst.item4j.seq;

import java.util.Collection;
import java.util.Iterator;
import java.util.RandomAccess;

import org.kisst.item4j.Item;

public abstract class ImmutableSequence<T> implements Sequence<T>, RandomAccess  {
	private final Class<?> elementClass;
	private ImmutableSequence(Class<?> elementClass) { this.elementClass=elementClass;}
	public Class<?> getElementClass() { return elementClass; }

	public SubSequence<T> subsequence(int start, int end) { return new SubSequence<T>(this, start, end); }
	public SubSequence<T> subsequence(int start) { return new SubSequence<T>(this, start, size()); }

	@SuppressWarnings("unchecked")
	public static <E> ImmutableSequence<E> copyOf(Sequence<? extends E> seq) {
		if (seq instanceof ImmutableSequence) 
			return (ImmutableSequence<E>) seq; // TODO: prevent memory leak if small subrange of huge array
		E[] arr = createArray(seq.size());
		int i=0;
		for (E obj : seq)
			arr[i++]=obj;
		return new ArraySequence<E>(seq.getElementClass(), arr);
	}

	@SuppressWarnings("unchecked")
	public static <E> ImmutableSequence<E> copyOf(ItemSequence seq) {
		if (seq instanceof ImmutableSequence) 
			return (ImmutableSequence<E>) seq; // TODO: prevent memory leak if small subrange of huge array
		E[] arr = createArray(seq.size());
		int i=0;
		for (Object obj : seq)
			arr[i++]= transformObject(obj);
		return new ArraySequence<E>(seq.getElementClass(), arr);
	}


	@SuppressWarnings("unchecked")
	public static <E> ImmutableSequence<E> copyOf(Class<?> elementClass, Collection<? extends E> collection) {
		if (collection instanceof ImmutableSequence) 
			return (ImmutableSequence<E>) collection; // TODO: prevent memory leak if small subrange of huge array
		E[] arr = createArray(collection.size());
		int i=0;
		for (E obj : collection)
			arr[i++]=obj;
		return new ArraySequence<E>(elementClass, arr);
	}


	@SuppressWarnings("unchecked")
	private static <E> E[] createArray(int length) { return (E[]) new Object[length]; }
	@SuppressWarnings("unchecked")
	private static<E> E transformObject(Object obj) {
		// TODO 
		// if instanceof T
		// if instanceof Struct
		return (E) obj;
	}



	private static class MyIterator<E> implements Iterator<E> {
		private final E[] array;
		private int index;
		private final int end;
		private MyIterator(E[] array, int begin, int end) {
			this.array=array;
			this.index=begin;
			this.end=end;
		}
		@Override public boolean hasNext() { return index<end; }
		@Override public E next() { return array[index++]; }
		@Override public void remove() { throw new RuntimeException("not implemented");}
	}

	public static class ArraySequence<T> extends ImmutableSequence<T> {
		private final T[] array; 
		private ArraySequence(Class<?> cls, T[] arr) {
			super(cls);
			this.array=arr;
		}
		public int size() { return array.length; }
		public T get(int index) { return (T) array[index]; }
		@Override public Iterator<T> iterator() { return new MyIterator<T>(array, 0, array.length);}
	}

	public final static class SubSequence<T> extends ImmutableSequence<T> {
		private final ArraySequence<T> seq;
		private final int start;
		private final int end;
		private  SubSequence(ImmutableSequence<T> seq, int start, int end) {
			super(seq.elementClass);
			if (seq instanceof ArraySequence) {
				this.seq=(ArraySequence<T>) seq;
				this.start=start;
				this.end=end;
			}
			else if (seq instanceof SubSequence) {
				SubSequence<T> sub = (SubSequence<T>) seq;
				this.seq=sub.seq;
				this.start=sub.start+start;
				this.end=sub.start+end;
			}
			else
				throw new RuntimeException("Unsupported ImmutableSequence type "+seq.getClass()); // should never happen
			if (this.start>this.end)
				throw new IllegalArgumentException("subsequence start "+start+" should be less or equal to end "+end);
			if (this.start<0)
				throw new IllegalArgumentException("subsequence start "+start+" should be >=0");
			if (this.end>seq.size())
				throw new IllegalArgumentException("subsequence end "+end+" should be less or equal to size "+seq.size());

		}
		@Override public Iterator<T> iterator() { return new MyIterator<T>(seq.array, start, end);}
		@Override public int size() { return end-start; }
		@Override public T get(int index) { return seq.array[start+index]; }
	}

	public final static class ImmutableItemSequence extends ArraySequence<Item> implements ItemSequence {
		private ImmutableItemSequence(Class<?> elementClass, Item[] array) { super(elementClass, array); }
	}
}
