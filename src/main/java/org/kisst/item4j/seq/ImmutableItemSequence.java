package org.kisst.item4j.seq;

import java.util.List;

import org.kisst.item4j.Item;

final public class ImmutableItemSequence implements ItemSequence {
	private final Object[] arr;
	public ImmutableItemSequence(List<?> list) { 
		this.arr=new Object[list.size()];
		int i=0;
		for (Object obj : list)
			this.arr[i++]=obj;
	}
	public ImmutableItemSequence(Object ... objects) { this.arr=objects; } 
	public Class<?> getElementClass() { return Item.class;}

	@Override public int size() { return arr.length;}
	@Override public Object getObject(int index) { return arr[index];}
}
