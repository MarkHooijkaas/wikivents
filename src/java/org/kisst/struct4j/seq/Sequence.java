package org.kisst.struct4j.seq;


public interface Sequence<T> extends Iterable<T>{
	public int size();
	public T get(int index);
}
