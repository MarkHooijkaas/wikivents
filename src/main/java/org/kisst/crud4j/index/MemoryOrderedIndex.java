package org.kisst.crud4j.index;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListMap;

import org.kisst.crud4j.CrudModel.OrderedIndex;
import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;

public class MemoryOrderedIndex<T extends CrudObject> extends AbstractKeyedIndex<T> implements OrderedIndex<T> {
	private final ConcurrentSkipListMap<String, T> map=new ConcurrentSkipListMap<String,T>();

	public MemoryOrderedIndex(CrudSchema<T> schema) { super(schema); }

	@Override protected void add(String key, T record) { map.put(key, record); }
	@Override protected void remove(String key) { map.remove(key); }
	@Override protected String calcUniqueKey(T record) { return record.getUniqueSortingKey(); }
	@Override public boolean keyExists(String key) { return map.containsKey(key); }

	@Override public Iterator<T> iterator() { return map.values().iterator(); }

	@Override public Collection<T> tailList(String fromKey) { return map.tailMap(fromKey).values(); } 
	@Override public Collection<T> headList(String toKey) { return map.tailMap(toKey).values(); } 
	@Override public Collection<T> subList(String fromKey,String toKey) { return map.subMap(fromKey, toKey).values(); } 
}
