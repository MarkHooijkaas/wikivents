package org.kisst.item4j.seq;


public interface Sequence<T> extends ItemSequence, Iterable<T>{
	public int size();
	@SuppressWarnings("unchecked")
	default public T get(int index) { return (T) getObject(index); }
	public Class<?> getElementClass();
}
