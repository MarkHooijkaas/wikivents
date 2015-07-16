package org.kisst.item4j.seq;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kisst.item4j.Item;

public class ListItemSequence implements ItemSequence<Item> {
	private final List<Object> list;
	public ListItemSequence() { this.list=new ArrayList<Object>(); }
	@SuppressWarnings("unchecked")
	public ListItemSequence(List<?> list) { this.list=(List<Object>) list; }

	@Override public Class<?> getElementClass() { return Item.class; }
	@Override public int size() { return list.size();}
	@Override public Object getObject(int index) { return list.get(index);}
	@Override public Iterator<Item> iterator() { return new IteratorWrapper(list.iterator());} 

	public List<?> getList() { return  this.list; }
	public void append(Object obj) { list.add(obj); }
	
	public final class MyIterator implements Iterator<Item>{
		private final Iterator<?> it;
		public MyIterator(Iterator<?> it) { this.it=it; }
		@Override public boolean hasNext() { return it.hasNext();}
		@Override public Item next() { return  Item.asItem(it.next()); }
		@Override public void remove() { throw new RuntimeException("remove is not allowed on this list"); }
	}

}
