package org.kisst.item4j.seq;

import java.util.Iterator;

public interface TypedSequence<T> extends ItemSequence, Iterable<T>{
	public Class<?> getElementClass();
	public int size();
	@SuppressWarnings("unchecked")
	default public T get(int index) { return (T) getObject(index); }
	
	public final class IndexIterator<TT> implements Iterator<TT>{
		private final TypedSequence<TT> seq;
		public IndexIterator(TypedSequence<TT> seq) { this.seq=seq; }
		private int index=0;
		@Override public boolean hasNext() { return index<seq.size();}
		@Override public TT next() { return seq.get(index++); }
		@Override public void remove() { throw new RuntimeException("remove is not allowed on this list"); }
	}
}
