package org.kisst.item4j.seq;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kisst.item4j.Item;

public class ListItemSequence<T> implements ItemSequence<T> {
	private final List<T> list;
	public ListItemSequence() { this.list=new ArrayList<T>(); }
	public ListItemSequence(List<T> list) { this.list=list; }

	@Override public Class<?> getElementClass() { return Item.class; }
	@Override public int size() { return list.size();}
	@Override public Object getObject(int index) { return list.get(index);}
	@Override public Iterator<T> iterator() { return list.iterator();} 

	public List<?> getList() { return  this.list; }
	public void append(T obj) { list.add(obj); }
}
