package org.kisst.item4j;

import java.util.Collection;
import java.util.Iterator;
import java.util.RandomAccess;

import org.kisst.item4j.seq.TypedSequence;

public interface Immutable {


	///////////////////////////////////////////////////////////////////////////////////////////////////

	//public class Item implements org.kisst.item4j.Item, Immutable {
	//	@Override public Object asObject() { return this;} // TODO
	//}


	///////////////////////////////////////////////////////////////////////////////////////////////////

	public static Immutable.ItemSequence items(Object ... obj) { return new Immutable.ItemSequence.ArraySequence(obj); }

	public abstract class ItemSequence implements org.kisst.item4j.seq.ItemSequence, Immutable {
		private ItemSequence() { } 


		public Immutable.ItemSequence subsequence(int start, int end) { return new SubSequence(this, start, end); }
		public Immutable.ItemSequence subsequence(int start) { return new SubSequence(this, start, size()); }

		public static  ItemSequence realCopy(org.kisst.item4j.seq.ItemSequence seq) {
			Object[] arr = new Object[seq.size()];
			int i=0; 
			for (Object obj: seq.objects())
				arr[i++]= obj; 
			return new ArraySequence(arr);
		}
		public static  ItemSequence realCopy(Collection<?> collection) {
			Object[] arr = new Object[collection.size()];
			int i=0; for (Object obj : collection) arr[i++]=obj;
			return new ArraySequence(arr);
		}

		public static  Immutable.ItemSequence smartCopy(org.kisst.item4j.seq.ItemSequence seq) {
			if (seq instanceof Immutable.ItemSequence) 
				return (Immutable.ItemSequence) seq;
			return realCopy(seq);
		}

		public static  Immutable.ItemSequence smartCopy(Collection<?> collection) {
			if (collection instanceof org.kisst.item4j.seq.ItemSequence) 
				return smartCopy((org.kisst.item4j.seq.ItemSequence) collection); 
			return realCopy(collection);
		}

		public ItemSequence removeFirst() { return subsequence(1); }
		public ItemSequence removeLast()  { return subsequence(0,size()-1); }
		public ItemSequence remove(int index) {
			if (index==0)      return removeFirst();
			if (index==size()) return removeLast();
			return subsequence(0,index).join(subsequence(index+1));
		}
		public ItemSequence remove(int begin, int end) {
			if (begin==0)      return subsequence(end+1);
			if (end==size()) return subsequence(0, begin);
			return subsequence(0,begin).join(this.subsequence(end));
		}

		public final ItemSequence join(ItemSequence ... sequences) {
			if (sequences.length==0) return this;
			ItemSequence[] result= (ItemSequence[]) new ItemSequence[sequences.length+1];
			result[0]=this;
			int i=1;
			for (ItemSequence seq  : sequences) result[i++]=seq;
			return new MultiSequence(result);
		}

		public ItemSequence join(Collection<?> ... collections) {
			Immutable.ItemSequence[] result= new Immutable.ItemSequence[collections.length+1];
			result[0]=this;
			int i=1;
			for (Collection<?> col : collections)
				result[i++]=smartCopy(col);
			return new MultiSequence(result);
		}

		private final static class ArraySequence extends Immutable.ItemSequence {
			private final Object[] array; 
			private ArraySequence(Object[] arr) {
				this.array=arr;
			}
			@Override public int size() { return array.length; }
			@Override public Object getObject(int index) { return array[index]; }
		}

		private final static class SubSequence extends Immutable.ItemSequence {
			private final ArraySequence seq;
			private final int start;
			private final int end;
			private  SubSequence(Immutable.ItemSequence seq, int start, int end) {
				if (seq instanceof ArraySequence) {
					this.seq=(ArraySequence) seq;
					this.start=start;
					this.end=end;
				}
				else if (seq instanceof SubSequence) {
					SubSequence sub = (SubSequence) seq;
					this.seq=sub.seq;
					this.start=sub.start+start;
					this.end=sub.start+end;
				}
				else
					throw new RuntimeException("Unsupported Immutable.ItemSequence type "+seq.getClass()); // should never happen
				seq.checkIndex(this.start);
				seq.checkIndex(this.end);
				if (this.start>this.end)
					throw new IndexOutOfBoundsException("subsequence start "+start+" should be less or equal to end "+end);
			}
			@Override public int size() { return end-start; }
			@Override public Object getObject(int index) { return seq.array[start+index]; }
		}

		private final static class MultiSequence extends ItemSequence {
			private final Immutable.ItemSequence[] sequences;
			private final int size; 
			private MultiSequence(Immutable.ItemSequence ... sequences) {
				this.sequences=sequences;
				int size=0;
				for (Immutable.ItemSequence seq : this.sequences)
					size+=seq.size();
				this.size=size;
			}
			@Override public int size() { return size; }
			@Override public Object getObject(int index) { 
				for (Immutable.ItemSequence seq : this.sequences) {
					if (index< seq.size())
						return seq.getObject(index);
					index -=seq.size();
				}
				throw new IndexOutOfBoundsException("index too large for size "+size());
			}
			@Override public Immutable.ItemSequence subsequence(int start, int end) {
				checkIndex(start);
				checkIndex(end);
				int offset=0;
				for (Immutable.ItemSequence seq : this.sequences) {
					if (offset+start< seq.size()) {
						if (offset+end<seq.size())
							return seq.subsequence(start-offset, end-offset);
						else
							break; // subsequence not in one segment
					}
					offset +=seq.size();
				}
				Object[] result=new Object[end-start];
				for(int i=start; i<end; i++)
					result[i-start]=getObject(i);
				return new ArraySequence(result); }
		}

	}



	///////////////////////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	public static<T> Sequence<T> typedSequence(Class<?> cls, T ... obj) { return new Immutable.Sequence.ArraySequence<T>(cls, obj); }

	public abstract class Sequence<T> implements TypedSequence<T>, RandomAccess, Immutable  {
		private final Class<?> elementClass;
		private Sequence(Class<?> elementClass) { this.elementClass=elementClass;}
		public Class<?> getElementClass() { return elementClass; }


		public Sequence<T> subsequence(int start, int end) { return new SubSequence<T>(this, start, end); }
		public Sequence<T> subsequence(int start) { return subsequence(start, size()); }

		@SuppressWarnings("unchecked")
		public static <E> Sequence<E> realCopy(Class<?> type, org.kisst.item4j.seq.ItemSequence seq) {
			E[] arr = createArray(seq.size());
			int i=0; 
			for (Object obj: seq.objects())
				arr[i++]= (E) Item.asType(type, obj); 
			return new Immutable.Sequence.ArraySequence<E>(type, arr);
		}
		public static <E> Sequence<E> realCopy(Class<?> elementClass, Collection<? extends E> collection) {
			E[] arr = createArray(collection.size());
			int i=0; for (E obj : collection) arr[i++]=obj;
			return new Immutable.Sequence.ArraySequence<E>(elementClass, arr);
		}

		@SuppressWarnings("unchecked")
		public static <E> Sequence<E> smartCopy(Class<?> type, org.kisst.item4j.seq.ItemSequence seq) {
			if (seq==null) throw new NullPointerException("Can not make smartCopy of null");
			if (seq instanceof TypedSequence) {
				if (type==seq.getElementClass())
					return (Sequence<E>) seq;
				return Immutable.Sequence.realCopy(type,  seq);
			}
			if (seq instanceof Collection)
				return realCopy(type,(Collection<E>) seq);
			throw new ClassCastException("Can not make a TypedSequence of type "+seq.getClass()+", "+seq);
		}
		public static <E> Sequence<E> smartCopy(Class<?> type, Collection<? extends E> collection) {
			if (collection instanceof org.kisst.item4j.seq.ItemSequence ) 
				return smartCopy(type, (org.kisst.item4j.seq.ItemSequence) collection); 
			return realCopy(type, collection);
		}

		public Sequence<T> removeFirst() { return subsequence(1); }
		public Sequence<T> removeLast()  { return subsequence(0,size()-1); }
		public Sequence<T> remove(int index) {
			if (index==0)      return removeFirst();
			if (index==size()) return removeLast();
			return subsequence(0,index).join(subsequence(index+1));
		}
		public Sequence<T> remove(int begin, int end) {
			if (begin==0)      return subsequence(end+1);
			if (end==size()) return subsequence(0, begin);
			return subsequence(0,begin).join(this.subsequence(end));
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
					throw new RuntimeException("Unsupported Immutable.Sequence type "+seq.getClass()); // should never happen
				seq.checkIndex(this.start);
				seq.checkIndex(this.end);
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
			@Override public Sequence<TT> subsequence(int start, int end) {
				checkIndex(start);
				checkIndex(end);
				int offset=0;
				for (Sequence<TT> seq : this.sequences) {
					if (offset+start< seq.size()) {
						if (offset+end<seq.size())
							return seq.subsequence(start-offset, end-offset);
						else
							break; // subsequence not in one segment
					}
					offset +=seq.size();
				}
				TT[] result=createArray(end-start);
				for(int i=start; i<end; i++)
					result[i-start]=get(i);
				return new ArraySequence<TT>(getElementClass(), result); }
		}

	}
}
