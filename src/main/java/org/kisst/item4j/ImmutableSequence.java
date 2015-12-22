package org.kisst.item4j;

import java.util.Collection;
import java.util.Iterator;
import java.util.RandomAccess;

import org.kisst.item4j.seq.TypedSequence;

@SuppressWarnings("unchecked")

public abstract class ImmutableSequence<T> implements TypedSequence<T>, RandomAccess, Immutable  {
	public static<T> ImmutableSequence<T> typedSequence(Class<?> cls, T ... obj) { return new ImmutableSequence.ArraySequence<T>(cls, obj); }
	private final Class<?> elementClass;
	private ImmutableSequence(Class<?> elementClass) { this.elementClass=elementClass;}
	public Class<?> getElementClass() { return elementClass; }

	@Override public Iterator<T> iterator() { return new MyIterator<T>(this);}


	@Override public String toString() { 
		StringBuilder result=new StringBuilder("[");
		String sep="";
		for( T obj: this) {
			result.append(sep+obj);
			sep=";";
		}
		result.append("]");
		return result.toString();
	}
	public ImmutableSequence<T> subsequence(int start, int end) { return new SubSequence<T>(this, start, end); }
	public ImmutableSequence<T> subsequence(int start) { return subsequence(start, size()); }
	public ImmutableSequence<T> reverse() { return new ReverseSequence<T>(this); }
	public boolean contains(T elm) {
		if (elm==null) return false;
		for (T e: this)
			if (elm.equals(e))
				return true;
		return false;
	}

	@SafeVarargs
	public static <E> ImmutableSequence<E> of(Class<E> type, E ... elements) {
		// TODO: safecopy
		return new ImmutableSequence.ArraySequence<E>(type, elements);
	}

	@FunctionalInterface
	public static interface StringExpression { public String calculateString(Object item); }
	
	public boolean hasItem(StringExpression expr, String key) { return findItemOrNull(expr, key)!=null; }
	public T findItemOrNull(StringExpression expr, String key) {
		if (key==null)
			return null;
		for (T item : this)
			if (key.equals(expr.calculateString(item)))
				return item;
		return null;
	}
	public ImmutableSequence<T> removeKeyedItem(StringExpression expr, String key) { // TODO: remove all for a key?
		int index=0;
		for (T item: this) {
			if (key.equals(expr.calculateString(item)))
				return remove(index);
			index++;
		}
		return this;
	}

	
	
	public static <E> ImmutableSequence<E> realCopy(Item.Factory factory, Class<?> type, org.kisst.item4j.seq.ItemSequence seq) {
		E[] arr = createArray(seq.size());
		int i=0; 
		for (Object obj: seq.objects())
			arr[i++]= (E) Item.asType(factory, type, obj); 
		return new ImmutableSequence.ArraySequence<E>(type, arr);
	}
	public static <E> ImmutableSequence<E> realCopy(Item.Factory factory, Class<?> elementClass, Collection<? extends E> collection) {
		//System.out.println("Converting "+collection.getClass()+" to Immutable.Sequence of "+ReflectionUtil.smartClassName(elementClass));
		E[] arr = createArray(collection.size());
		int i=0; for (E obj : collection) 
			arr[i++]=Item.asType(factory, elementClass, obj);
		return new ImmutableSequence.ArraySequence<E>(elementClass, arr);
	}

	public static <E> ImmutableSequence<E> smartCopy(Item.Factory factory, Class<?> type, org.kisst.item4j.seq.ItemSequence seq) {
		if (seq==null) throw new NullPointerException("Can not make smartCopy of null");
		if (seq instanceof TypedSequence) {
			if (type==seq.getElementClass())
				return (ImmutableSequence<E>) seq;
			return ImmutableSequence.realCopy(factory, type,  seq);
		}
		if (seq instanceof Collection)
			return realCopy(factory, type,(Collection<E>) seq);
		throw new ClassCastException("Can not make a TypedSequence of type "+seq.getClass()+", "+seq);
	}
	public static <E> ImmutableSequence<E> smartCopy(Item.Factory factory, Class<?> type, Collection<? extends E> collection) {
		if (collection instanceof org.kisst.item4j.seq.ItemSequence ) 
			return smartCopy(factory, type, (org.kisst.item4j.seq.ItemSequence) collection); 
		return realCopy(factory, type, collection);
	}

	public ImmutableSequence<T> growTail(T tail) { return new GrowTailSequence<T>(this, tail); }
	public ImmutableSequence<T> removeFirst() { return subsequence(1); }
	public ImmutableSequence<T> removeLast()  { return subsequence(0,size()-1); }
	public ImmutableSequence<T> remove(int index) {
		if (index==0)      return removeFirst();
		if (index==size()) return removeLast();
		return subsequence(0,index).join(subsequence(index+1));
	}
	public ImmutableSequence<T> remove(int begin, int end) {
		if (begin==0)      return subsequence(end+1);
		if (end==size()) return subsequence(0, begin);
		return subsequence(0,begin).join(this.subsequence(end));
	}

	@SafeVarargs
	public final ImmutableSequence<T> join(ImmutableSequence<T> ... sequences) {
		if (sequences.length==0) return this;
		ImmutableSequence<T>[] result= (ImmutableSequence<T>[]) new ImmutableSequence<?>[sequences.length+1];
		result[0]=this;
		int i=1;
		for (ImmutableSequence<T> seq  : sequences) result[i++]=seq;
		return new MultiSequence<T>(elementClass, result);
	}

	public ImmutableSequence<T> join(Item.Factory factory, Collection<T> ... collections) {
		ImmutableSequence<T>[] result= createArray(collections.length+1);
		result[0]=this;
		int i=1;
		for (Collection<T> col : collections) {
			if (col instanceof ImmutableSequence)
				result[i++]=(ImmutableSequence<T>) col;
			else if (!elementClass.isAssignableFrom(col.getClass()))
				result[i++]=smartCopy(factory, elementClass, col);
		}
		return new MultiSequence<T>(elementClass, result);
	}


	private static <E> E[] createArray(int length) { return (E[]) new Object[length]; }


	private static class MyIterator<E> implements Iterator<E> {
		private final ImmutableSequence<E> seq;
		private int index;
		private final int end;
		private MyIterator(ImmutableSequence<E> seq) {
			this.seq=seq; this.index=0;	this.end=seq.size();
		}
		@Override public boolean hasNext() { return index<end; }
		@Override public E next() { return seq.get(index++); }
		@Override public void remove() { throw new RuntimeException("not implemented");}
	}

	private final static class ArraySequence<T> extends ImmutableSequence<T> {
		private final T[] array; 
		private ArraySequence(Class<?> cls, T[] arr) {
			super(cls);
			this.array=arr;
		}
		@Override public int size() { return array.length; }
		@Override public Object getObject(int index) { return array[index]; }
	}

	private final static class ReverseSequence<T> extends ImmutableSequence<T> {
		private final ImmutableSequence<T> seq; 
		private final int lastIndex;
		private ReverseSequence(ImmutableSequence<T> seq) { 
			super(seq.elementClass); 
			this.seq=seq;
			this.lastIndex=seq.size()-1;
		}
		@Override public int size() { return seq.size(); }
		@Override public Object getObject(int index) { return seq.getObject(lastIndex-index); }
	}

	
	private final static class SubSequence<T> extends ImmutableSequence<T> {
		private final ImmutableSequence<T> seq;
		private final int start;
		private final int end;
		private  SubSequence(ImmutableSequence<T> seq, int start, int end) {
			super(seq.elementClass);
			if (seq instanceof SubSequence) {
				SubSequence<T> sub = (SubSequence<T>) seq;
				this.seq=sub.seq;
				this.start=sub.start+start;
				this.end=sub.start+end;
			}
			else {
				this.seq=seq;
				this.start=start;
				this.end=end;
			}
			seq.checkIndex(this.start);
			seq.checkIndex(this.end);
			if (this.start>this.end)
				throw new IndexOutOfBoundsException("subsequence start "+start+" should be less or equal to end "+end);
		}
		@Override public int size() { return end-start; }
		@Override public Object getObject(int index) { return seq.getObject(start+index); }
	}
	private final static class GrowTailSequence<T> extends ImmutableSequence<T> {
		private final ImmutableSequence<T> seq;
		private final T tail;
		private final int size;
		private  GrowTailSequence(ImmutableSequence<T> seq, T tail) {
			super(seq.elementClass);
			this.seq=seq;
			this.tail=tail;
			this.size=seq.size()+1;
		}
		@Override public Iterator<T> iterator() { return new IndexIterator<T>(this);}
		@Override public int size() { return size; }
		@Override public Object getObject(int index) { return (index==size-1) ? tail : seq.getObject(index); }
	}


	private final static class MultiSequence<TT> extends ImmutableSequence<TT> {
		private final ImmutableSequence<TT>[] sequences;
		private final int size; 
		private MultiSequence(Class<?> cls, ImmutableSequence<TT> ... sequences) {
			super(cls);
			this.sequences=sequences;
			int size=0;
			for (ImmutableSequence<TT> seq : this.sequences)
				size+=seq.size();
			this.size=size;
		}
		@Override public int size() { return size; }
		@Override public Object getObject(int index) { 
			for (ImmutableSequence<TT> seq : this.sequences) {
				if (index< seq.size())
					return seq.getObject(index);
				index -=seq.size();
			}
			throw new IndexOutOfBoundsException("index too large for size "+size());
		}
		@Override public Iterator<TT> iterator() { return new IndexIterator<TT>(this);}
		@Override public ImmutableSequence<TT> subsequence(int start, int end) {
			checkIndex(start);
			checkIndex(end);
			int offset=0;
			for (ImmutableSequence<TT> seq : this.sequences) {
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

	public static EmptySequence<?> EMPTY=new EmptySequence<>();
	public final static class EmptySequence<TT> extends ImmutableSequence<TT> {
		private EmptySequence() { super(null); }
		@Override public int size() { return 0;}
		@Override public TT getObject(int index) { throw new IndexOutOfBoundsException();}
		@Override public Iterator<TT> iterator() { return new IndexIterator<TT>(this);}

	}
}