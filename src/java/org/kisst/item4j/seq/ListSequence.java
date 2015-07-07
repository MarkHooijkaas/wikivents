package org.kisst.item4j.seq;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kisst.item4j.Item;

public class ListSequence<T> implements ItemSequence {
	private final List<T> list;
	public ListSequence() { this.list=new ArrayList<T>(); }
	public ListSequence(List<T> list) { this.list=list; }

	public List<?> getList() { return  this.list; }
	@Override public int size() { return list.size();}
	@Override public Item get(int index) { return Item.asItem(list.get(index));}
	@Override public Object getObject(int index) { return list.get(index);}
	@Override public Iterator<Item> iterator() { return new IteratorWrapper(list.iterator());} 

	public void append(T obj) { list.add(obj); }
}
