package org.kisst.struct4j.seq;

import java.util.Iterator;
import java.util.List;

import org.kisst.struct4j.Item;

final public class ImmutableSequence implements ItemSequence {
	private final Object[] arr;
	public ImmutableSequence(List<?> list) { 
		this.arr=new Object[list.size()];
		int i=0;
		for (Object obj : list)
			this.arr[i++]=obj;
	}
	public ImmutableSequence(Object ... objects) { 
		this.arr=objects;
	} 

	@Override public int size() { return arr.length;}
	@Override public Item get(int index) { return Item.asItem(arr[index]);}
	@Override public Object getObject(int index) { return arr[index];}
	@Override public Iterator<Item> iterator() { return new MyIterator();} 

	private class MyIterator implements Iterator<Item>{
		private int index=0;
		@Override public boolean hasNext() { return index<arr.length; }
		@Override public Item next() { return Item.asItem(arr[index++]); }
		@Override public void remove() { throw new RuntimeException("remove is not allowed on this list"); }
	}
}
