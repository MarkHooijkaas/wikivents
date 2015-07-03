package org.kisst.struct4j.seq;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimpleSequence extends BaseSequence {
	private final ArrayList<Object> list;
	public SimpleSequence() { this.list=new ArrayList<Object>(); }
	public SimpleSequence(List<Object> list) { this.list=new ArrayList<Object>(list); }

	@Override public int size() { return list.size();}
	@Override public Object getObject(int index) { return list.get(index);}
	@Override public Iterator<Object> iterator() { return list.iterator();} // TODO use immutable iterator

	public void append(Object obj) { list.add(obj); }
}
