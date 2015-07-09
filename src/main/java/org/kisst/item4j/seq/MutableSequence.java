package org.kisst.item4j.seq;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kisst.item4j.Item;

public class MutableSequence implements ItemSequence {
	private final List<Object> list;
	public MutableSequence() { this.list=new ArrayList<Object>(); }
	public MutableSequence(List<Object> list) { this.list=list; }

	@Override public int size() { return list.size();}
	@Override public Item get(int index) { return Item.asItem(list.get(index));}
	@Override public Object getObject(int index) { return list.get(index);}
	@Override public Iterator<Item> iterator() { return new IteratorWrapper(list.iterator());} 

	public void append(Object obj) { list.add(obj); }
}
