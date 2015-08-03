package org.kisst.crud4j.index;

import java.util.concurrent.ConcurrentSkipListMap;

import org.kisst.crud4j.CrudModel.OrderedIndex;
import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.crud4j.CrudTable;

public class MemoryOrderedIndex<T extends CrudObject> extends Index<T> implements OrderedIndex<T> {
	private ConcurrentSkipListMap<T,T> set;
	
	public MemoryOrderedIndex(CrudSchema<T> schema) { 
		super(schema);
		this.set=new ConcurrentSkipListMap<T,T>();
	}

	@Override public Iterable<T> all() { return set.values(); }

	@Override public boolean allow(CrudTable<T>.Change change) { return true; }
	@Override public void commit(CrudTable<T>.Change change) {
		if (change.newRecord!=null)
			set.put(change.newRecord,change.newRecord);
		if (change.oldRecord!=null)
			set.remove(change.oldRecord);
	}

	@Override public void rollback(CrudTable<T>.Change change) {
		if (change.oldRecord!=null)
			set.put(change.oldRecord,change.oldRecord);
		if (change.newRecord!=null)
			set.remove(change.newRecord);
	}
}
