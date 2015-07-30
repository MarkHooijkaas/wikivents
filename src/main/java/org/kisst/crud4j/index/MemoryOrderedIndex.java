package org.kisst.crud4j.index;

import java.util.concurrent.ConcurrentSkipListMap;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.crud4j.CrudTable;

public class MemoryOrderedIndex<T extends CrudObject> extends Index<T> implements CrudTable.OrderedIndex<T> {
	private ConcurrentSkipListMap<T,T> set;
	
	public MemoryOrderedIndex(CrudSchema<T> schema) { 
		super(schema);
		this.set=new ConcurrentSkipListMap<T,T>();
	}

	@Override public Iterable<T> all() { return set.values(); }

	@Override public void notifyCreate(T newRecord) {set.put(newRecord,newRecord);} 
	@Override public void notifyUpdate(T oldRecord, T newRecord) {
		set.put(newRecord,newRecord);
		set.remove(oldRecord);
	}
	@Override public void notifyDelete(T oldRecord) { set.remove(oldRecord); }
}
