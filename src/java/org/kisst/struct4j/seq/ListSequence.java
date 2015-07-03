package org.kisst.struct4j.seq;

import java.util.Iterator;
import java.util.List;

public class ListSequence extends BaseSequence {
	private final List<Object> list;
	public ListSequence(List<Object> list) { this.list=list; }

	@Override public int size() { return list.size();}
	@Override public Object getObject(int index) { return list.get(index);}
	@Override public Iterator<Object> iterator() { return list.iterator();} // TODO use immutable iterator

}
