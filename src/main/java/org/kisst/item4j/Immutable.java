package org.kisst.item4j;

import java.util.Collection;
import java.util.Iterator;
import java.util.RandomAccess;

import org.kisst.item4j.seq.TypedSequence;

public interface Immutable {

	
	///////////////////////////////////////////////////////////////////////////////////////////////////

	public class Item implements org.kisst.item4j.Item, Immutable {
		@Override public Object asObject() { return this;} // TODO
	}

	
	///////////////////////////////////////////////////////////////////////////////////////////////////

	public abstract class ItemSequence implements org.kisst.item4j.seq.ItemSequence, Immutable {
		private final Class<?> elementClass;
		private ItemSequence(Class<?> elementClass) { this.elementClass=elementClass; } 
		@Override public Class<?> getElementClass() { return elementClass; }

		//@Override public int size() { }
		//@Override public Object getObject(int index) {}

	}


	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	@SuppressWarnings("unchecked")
	public static<T> Sequence<T> sequence(Class<?> cls, T ... obj) { return new Immutable.Sequence.ArraySequence<T>(cls, obj); }

	
	public abstract class Sequence<T> implements TypedSequence<T>, RandomAccess, Immutable  {
		private final Class<?> elementClass;
		private Sequence(Class<?> elementClass) { this.elementClass=elementClass;}
		public Class<?> getElementClass() { return elementClass; }


		public SubSequence<T> subsequence(int start, int end) { return new SubSequence<T>(this, start, end); }
		public SubSequence<T> subsequence(int start) { return new SubSequence<T>(this, start, size()); }

		public static <E> Sequence<E> realCopy(TypedSequence<? extends E> seq) {
			E[] arr = createArray(seq.size());
			int i=0; for (E obj : seq) arr[i++]=obj;
			return new ArraySequence<E>(seq.getElementClass(), arr);
		}
		@SuppressWarnings("unchecked")
		public static <E> Sequence<E> realCopy(org.kisst.item4j.seq.ItemSequence seq) {
			E[] arr = createArray(seq.size());
			int i=0; 
			for (org.kisst.item4j.Item item : seq.items())
				arr[i++]= (E) org.kisst.item4j.Item.asType(seq.getElementClass(), item); 
			return new ArraySequence<E>(seq.getElementClass(), arr);
		}
		public static <E> Sequence<E> realCopy(Class<?> elementClass, Collection<? extends E> collection) {
			E[] arr = createArray(collection.size());
			int i=0; for (E obj : collection) arr[i++]=obj;
			return new ArraySequence<E>(elementClass, arr);
		}


		@SuppressWarnings("unchecked")
		public static <E> Sequence<E> smartCopy(TypedSequence<? extends E> seq) {
			if (seq instanceof Sequence) 
				return (Sequence<E>) seq; 
			return realCopy(seq);
		}

		@SuppressWarnings("unchecked")
		public static <E> Sequence<E> smartCopy(org.kisst.item4j.seq.ItemSequence seq) {
			if (seq instanceof Sequence) 
				return (Sequence<E>) seq;
			return realCopy(seq);
		}

		@SuppressWarnings("unchecked")
		public static <E> Sequence<E> smartCopy(Class<?> elementClass, Collection<? extends E> collection) {
			if (collection instanceof Sequence) 
				return (Sequence<E>) collection; // TODO: prevent memory leak if small subrange of huge array
			return smartCopy(elementClass, collection);
		}

		public Sequence<T> removeFirst() { return subsequence(1); }
		public Sequence<T> removeLast()  { return subsequence(size()-1); }

		public Sequence<T> remove(int index) {
			if (index==0)      return removeFirst();
			if (index==size()) return removeLast();
			return subsequence(0,index).join(subsequence(index+1));
		}
		
		@SafeVarargs
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public final Sequence<T> join(Sequence<T> ... sequences) {
			if (sequences.length==0) return this;
			Sequence<T>[] result= (Sequence<T>[]) new Sequence<?>[sequences.length+1];
			result[0]=this;
			int i=1;
			for (Sequence<T> seq  : sequences) result[i++]=seq;
			return new MultiSequence(elementClass, result);
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Sequence<T> join(Collection<T> ... collections) {
			Sequence<T>[] result= createArray(collections.length+1);
			result[0]=this;
			int i=1;
			for (Collection<T> col : collections) {
				if (col instanceof Sequence)
					result[i++]=(Sequence<T>) col;
				else if (!elementClass.isAssignableFrom(col.getClass()))
					result[i++]=(Sequence<T>) smartCopy(elementClass, col);
			}
			return new MultiSequence(elementClass, result);
		}


		@SuppressWarnings("unchecked")
		private static <E> E[] createArray(int length) { return (E[]) new Object[length]; }


		private static class MyIterator<E> implements Iterator<E> {
			private final E[] array;
			private int index;
			private final int end;
			private MyIterator(E[] array, int begin, int end) {
				this.array=array; this.index=begin;	this.end=end;
			}
			@Override public boolean hasNext() { return index<end; }
			@Override public E next() { return array[index++]; }
			@Override public void remove() { throw new RuntimeException("not implemented");}
		}

		private final static class ArraySequence<T> extends Immutable.Sequence<T> {
			private final T[] array; 
			private ArraySequence(Class<?> cls, T[] arr) {
				super(cls);
				this.array=arr;
			}
			@Override public int size() { return array.length; }
			@Override public Object getObject(int index) { return array[index]; }
			@Override public Iterator<T> iterator() { return new MyIterator<T>(array, 0, array.length);}
		}

		private final static class SubSequence<T> extends Immutable.Sequence<T> {
			private final ArraySequence<T> seq;
			private final int start;
			private final int end;
			private  SubSequence(Immutable.Sequence<T> seq, int start, int end) {
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
				checkIndex(this.start);
				if (this.start>this.end)
					throw new IndexOutOfBoundsException("subsequence start "+start+" should be less or equal to end "+end);
			}
			@Override public Iterator<T> iterator() { return new MyIterator<T>(seq.array, start, end);}
			@Override public int size() { return end-start; }
			@Override public Object getObject(int index) { return seq.array[start+index]; }
		}

		private final static class MultiSequence<TT> extends Immutable.Sequence<TT> {
			private final Immutable.Sequence<TT>[] sequences;
			private final int size; 
			private MultiSequence(Class<?> cls, @SuppressWarnings("unchecked") Immutable.Sequence<TT> ... sequences) {
				super(cls);
				this.sequences=sequences;
				int size=0;
				for (Sequence<TT> seq : this.sequences)
					size+=seq.size();
				this.size=size;
			}
			@Override public int size() { return size; }
			@Override public Object getObject(int index) { 
				for (Sequence<TT> seq : this.sequences) {
					if (index< seq.size())
						return seq.getObject(index);
					index -=seq.size();
				}
				throw new IndexOutOfBoundsException("index too large for size "+size());
			}
			@Override public Iterator<TT> iterator() { return new IndexIterator<TT>(this);}
		}

	}
}
