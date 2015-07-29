package org.kisst.type4j.seq;

public interface SmartSequence<T> extends Iterable<T> { 
	public Class<T> getElementClass();
	public int size();
	
	public T get(int index);
	public Object getObject(int index); // get content without any conversion
}
