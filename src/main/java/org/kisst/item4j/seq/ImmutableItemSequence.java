package org.kisst.item4j.seq;

import java.util.Iterator;
import java.util.List;

import org.kisst.item4j.Item;

final public class ImmutableItemSequence implements ItemSequence<Item> {
	private final Object[] arr;
	public ImmutableItemSequence(List<?> list) { 
		this.arr=new Object[list.size()];
		int i=0;
		for (Object obj : list)
			this.arr[i++]=obj;
	}
	public ImmutableItemSequence(Object ... objects) { 
		this.arr=objects;
	} 
	public Class<?> getElementClass() { return Item.class;}

	@Override public int size() { return arr.length;}
	@Override public Object getObject(int index) { return arr[index];}
	@Override public Iterator<Item> iterator() { return new MyIterator();} 

	private class MyIterator implements Iterator<Item>{
		private int index=0;
		@Override public boolean hasNext() { return index<arr.length; }
		@Override public Item next() { return Item.asItem(arr[index++]); }
		@Override public void remove() { throw new RuntimeException("remove is not allowed on this list"); }
	}
}
